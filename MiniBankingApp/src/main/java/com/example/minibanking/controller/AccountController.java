
package com.example.minibanking.controller;

import com.example.minibanking.dto.AccountRequest;
import com.example.minibanking.dto.DepositRequest;
import com.example.minibanking.dto.LoginRequest;
import com.example.minibanking.entity.Account;
import com.example.minibanking.security.JwtService;
import com.example.minibanking.service.BankService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final BankService service;
    private final JwtService jwtService;

    public AccountController(BankService service, JwtService jwtService) {
        this.service = service;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody AccountRequest request) {
        Account account = service.createAccount(request);

        return Map.of(
                "message", "Account created successfully",
                "accountId", account.getId(),
                "username", account.getUsername(),
                "balance", account.getBalance()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Account account = service.login(request.username(), request.password());
        String token = jwtService.generateToken(account.getUsername());

        return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "token", token,
                "username", account.getUsername(),
                "role", account.getRole(),
                "accountId", account.getId()
        ));
    }

    @GetMapping("/balance")
    public ResponseEntity<?> getBalance(Authentication authentication) {
        Account account = service.getAccountByUsername(authentication.getName());

        return ResponseEntity.ok(Map.of(
                "accountId", account.getId(),
                "username", account.getUsername(),
                "balance", account.getBalance()
        ));
    }

    @GetMapping("/all")
    public List<Map<String, Object>> getAllAccounts() {
        return service.getAllAccounts().stream()
                .map(account -> Map.<String, Object>of(
                        "accountId", account.getId(),
                        "username", account.getUsername(),
                        "balance", account.getBalance()
                ))
                .toList();
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(
            @RequestBody DepositRequest request,
            Authentication authentication) {

        Account account = service.getAccountByUsername(authentication.getName());

        DepositRequest secureRequest =
                new DepositRequest(account.getId(), request.amount());

        return ResponseEntity.ok(service.depositMoney(secureRequest));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(
            @RequestBody DepositRequest request,
            Authentication authentication) {

        Account account = service.getAccountByUsername(authentication.getName());

        DepositRequest secureRequest =
                new DepositRequest(account.getId(), request.amount());

        return ResponseEntity.ok(service.withdrawMoney(secureRequest));
    }
}
