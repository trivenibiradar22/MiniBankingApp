
package com.example.minibanking.repository;

import com.example.minibanking.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findBySenderIdOrReceiverIdOrderByTransactionDateDesc(
            Long senderId, Long receiverId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.transactionType = 'DEPOSIT'")
    BigDecimal getTotalDeposits();

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.transactionType = 'WITHDRAW'")
    BigDecimal getTotalWithdrawals();

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.transactionType = 'TRANSFER'")
    BigDecimal getTotalTransfers();
}
