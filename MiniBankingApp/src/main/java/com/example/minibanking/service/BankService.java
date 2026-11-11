
package com.example.minibanking.service;

import com.example.minibanking.entity.Account;
import com.example.minibanking.entity.Transaction;

import com.example.minibanking.repository.AccountRepository;
import com.example.minibanking.repository.TransactionRepository;

import com.example.minibanking.dto.AccountRequest;
import com.example.minibanking.dto.DepositRequest;
import com.example.minibanking.dto.TransactionResponse;
import com.example.minibanking.dto.TransferRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BankService {

    private final AccountRepository repository;
    private final PasswordEncoder encoder;
    private final TransactionRepository transactionRepository;

    public BankService(
            AccountRepository repository,
            PasswordEncoder encoder,
            TransactionRepository transactionRepository) {

        this.repository = repository;
        this.encoder = encoder;
        this.transactionRepository = transactionRepository;
    }

    // Get transaction history
    public List<Transaction> getTransactionHistory(Long accountId) {

        if (!repository.existsById(accountId)) {
            throw new RuntimeException("Account not found");
        }

        return transactionRepository
                .findBySenderIdOrReceiverIdOrderByTransactionDateDesc(
                        accountId, accountId);
    }

    // Get account by username
    public Account getAccountByUsername(String username) {

        return repository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("Account not found"));
    }

    // Create account
    public Account createAccount(AccountRequest request) {

        if (request.username() == null ||
                request.username().isBlank() ||
                request.password() == null ||
                request.password().length() < 8) {

            throw new RuntimeException(
                    "Username is required and password must contain at least 8 characters");
        }

        if (repository.findByUsername(request.username()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        Account account = new Account();
        account.setUsername(request.username());
        account.setPassword(encoder.encode(request.password()));

        return repository.save(account);
    }

    // Login
    public Account login(String username, String password) {

        Account account = repository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("Invalid username or password"));

        if (!encoder.matches(password, account.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        return account;
    }

    // Get balance
    public Account getBalance(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Account not found"));
    }

    // Update account role
    public void updateRole(String username, String role) {

        Account account = repository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("Account not found"));

        if (role == null ||
                (!role.equals("USER") && !role.equals("ADMIN"))) {
            throw new RuntimeException("Invalid role");
        }

        account.setRole(role);
        repository.save(account);
    }

    // Transfer money
    @Transactional
    public String transferMoney(TransferRequest request) {

        if (request.senderId() == null ||
                request.receiverId() == null ||
                request.amount() == null ||
                request.amount().compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException("Invalid transfer details");
        }

        if (request.senderId().equals(request.receiverId())) {
            throw new RuntimeException(
                    "Cannot transfer to same account");
        }

        Account sender = repository.findById(request.senderId())
                .orElseThrow(() ->
                        new RuntimeException("Sender not found"));

        Account receiver = repository.findById(request.receiverId())
                .orElseThrow(() ->
                        new RuntimeException("Receiver not found"));

        if (sender.getBalance().compareTo(request.amount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        sender.setBalance(
                sender.getBalance().subtract(request.amount()));

        receiver.setBalance(
                receiver.getBalance().add(request.amount()));

        repository.save(sender);
        repository.save(receiver);

        Transaction transaction = new Transaction();

        transaction.setSenderId(sender.getId());
        transaction.setReceiverId(receiver.getId());
        transaction.setAmount(request.amount());
        transaction.setTransactionType("TRANSFER");
        transaction.setStatus("SUCCESS");

        transactionRepository.save(transaction);

        return "Transaction successful";
    }

    // Deposit money
    @Transactional
    public String depositMoney(DepositRequest request) {

        if (request.accountId() == null ||
                request.amount() == null ||
                request.amount().compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException("Invalid deposit details");
        }

        Account account = repository.findById(request.accountId())
                .orElseThrow(() ->
                        new RuntimeException("Account not found"));

        account.setBalance(
                account.getBalance().add(request.amount()));

        repository.save(account);

        Transaction transaction = new Transaction();

        transaction.setReceiverId(account.getId());
        transaction.setAmount(request.amount());
        transaction.setTransactionType("DEPOSIT");
        transaction.setStatus("SUCCESS");

        transactionRepository.save(transaction);

        return "Money deposited successfully";
    }

    // Withdraw money
    @Transactional
    public String withdrawMoney(DepositRequest request) {

        if (request.accountId() == null ||
                request.amount() == null ||
                request.amount().compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException("Invalid withdrawal details");
        }

        Account account = repository.findById(request.accountId())
                .orElseThrow(() ->
                        new RuntimeException("Account not found"));

        if (account.getBalance().compareTo(request.amount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        account.setBalance(
                account.getBalance().subtract(request.amount()));

        repository.save(account);

        Transaction transaction = new Transaction();

        transaction.setSenderId(account.getId());
        transaction.setAmount(request.amount());
        transaction.setTransactionType("WITHDRAW");
        transaction.setStatus("SUCCESS");

        transactionRepository.save(transaction);

        return "Money withdrawn successfully";
    }

public List<TransactionResponse> getAllTransactions() {
    return transactionRepository.findAll().stream()
        .map(t -> new TransactionResponse(
            t.getId(),
            t.getTransactionType(),
            t.getSenderId(),
            t.getReceiverId(),
            t.getAmount(),
            t.getTransactionDate(),
            t.getStatus()
        ))
        .toList();
}

    // Get transaction by ID
    public TransactionResponse getTransactionById(
            Long transactionId, Long accountId) {

        Transaction transaction = transactionRepository
                .findById(transactionId)
                .orElseThrow(() ->
                        new RuntimeException("Transaction not found"));

        if (!accountId.equals(transaction.getSenderId()) &&
                !accountId.equals(transaction.getReceiverId())) {

            throw new RuntimeException(
                    "You are not authorized to view this transaction");
        }

        return new TransactionResponse(
                transaction.getId(),
                transaction.getTransactionType(),
                transaction.getSenderId(),
                transaction.getReceiverId(),
                transaction.getAmount(),
                transaction.getTransactionDate(),
                transaction.getStatus()
        );
    }

    // Get all accounts (Admin)
    public List<Account> getAllAccounts() {
        return repository.findAll();
    }

    // Total customers
    public long getTotalCustomers() {
        return repository.count();
    }

    // Total bank balance
    public BigDecimal getTotalBankBalance() {
        return repository.getTotalBankBalance();
    }

    // Total transactions
    public long getTotalTransactions() {
        return transactionRepository.count();
    }

    // Total deposits
    public BigDecimal getTotalDeposits() {
        return transactionRepository.getTotalDeposits();
    }

    // Total withdrawals
    public BigDecimal getTotalWithdrawals() {
        return transactionRepository.getTotalWithdrawals();
    }

    // Total transfers
    public BigDecimal getTotalTransfers() {
        return transactionRepository.getTotalTransfers();
    }
}
