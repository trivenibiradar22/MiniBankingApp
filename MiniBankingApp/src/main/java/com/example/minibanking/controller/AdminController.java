
package com.example.minibanking.controller;

import com.example.minibanking.dto.TransactionResponse;
import com.example.minibanking.service.BankService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final BankService service;

    public AdminController(BankService service) {
        this.service = service;
    }

    @GetMapping("/customers/count")
    public Map<String, Long> getTotalCustomers() {
        return Map.of(
                "totalCustomers",
                service.getTotalCustomers()
        );
    }
    @GetMapping("/total-balance")
    public Map<String, BigDecimal> getTotalBankBalance() {
        return Map.of(
            "totalBankBalance",
            service.getTotalBankBalance()
        );
    }
    @GetMapping("/transactions/count")
    public Map<String, Long> getTotalTransactions() {
        return Map.of(
            "totalTransactions",
            service.getTotalTransactions()
        );
    }
    @GetMapping("/total-deposits")
    public Map<String, BigDecimal> getTotalDeposits() {
        return Map.of(
            "totalDeposits",
            service.getTotalDeposits()
        );
    }
    @GetMapping("/total-withdrawals")
    public Map<String, BigDecimal> getTotalWithdrawals() {
        return Map.of(
            "totalWithdrawals",
            service.getTotalWithdrawals()
        );
    }
    @GetMapping("/total-transfers")
    public Map<String, BigDecimal> getTotalTransfers() {
        return Map.of(
            "totalTransfers",
            service.getTotalTransfers()
        );
    }

@GetMapping("/transactions")
public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
    return ResponseEntity.ok(
        service.getAllTransactions()
    );
}


@GetMapping("/dashboard")
public Map<String, Object> getDashboard() {

    return Map.of(
        "totalCustomers", service.getTotalCustomers(),
        "totalBankBalance", service.getTotalBankBalance(),
        "totalTransactions", service.getTotalTransactions(),
        "totalDeposits", service.getTotalDeposits(),
        "totalWithdrawals", service.getTotalWithdrawals(),
        "totalTransfers", service.getTotalTransfers()
    );
}

}
