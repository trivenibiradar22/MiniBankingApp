package com.example.minibanking.repository;

import com.example.minibanking.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByUsername(String username);

    long count();
    @Query("SELECT COALESCE(SUM(a.balance), 0) FROM Account a")
    BigDecimal getTotalBankBalance();
}