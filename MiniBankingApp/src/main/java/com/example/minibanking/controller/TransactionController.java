
package com.example.minibanking.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.minibanking.dto.TransactionResponse;
import com.example.minibanking.dto.TransferRequest;
import com.example.minibanking.entity.Account;
import com.example.minibanking.service.BankService;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final BankService service;

    public TransactionController(BankService service) {
        this.service = service;
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(
            @RequestBody TransferRequest request,
            Authentication authentication) {

        Account sender = service.getAccountByUsername(
                authentication.getName());

        TransferRequest secureRequest = new TransferRequest(
                sender.getId(),
                request.receiverId(),
                request.amount()
        );

        return ResponseEntity.ok(
                service.transferMoney(secureRequest));
    }

    @GetMapping("/history")
    public ResponseEntity<?> getTransactionHistory(
            Authentication authentication) {

        Account account = service.getAccountByUsername(
                authentication.getName());

        List<TransactionResponse> history =
                service.getTransactionHistory(account.getId())
                .stream()
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

        return ResponseEntity.ok(history);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransaction(
            @PathVariable Long id,
            Authentication authentication) {

        Long accountId = service.getAccountByUsername(
                authentication.getName()).getId();

        TransactionResponse transaction =
                service.getTransactionById(id, accountId);

        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/statement")
    public ResponseEntity<?> getStatement(
            Authentication authentication) {

        Account account = service.getAccountByUsername(
                authentication.getName());

        return ResponseEntity.ok(
                service.getTransactionHistory(account.getId()));
    }
}
