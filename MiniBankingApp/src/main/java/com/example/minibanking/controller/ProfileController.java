
package com.example.minibanking.controller;

import com.example.minibanking.entity.Account;
import com.example.minibanking.repository.AccountRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final AccountRepository repository;
    private final PasswordEncoder encoder;

    public ProfileController(
            AccountRepository repository,
            PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    @GetMapping
    public Map<String, Object> getProfile(Authentication authentication) {
        Account account = repository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        return Map.of(
                "accountId", account.getId(),
                "username", account.getUsername(),
                "balance", account.getBalance(),
                "role", account.getRole()
        );
    }

    @PutMapping("/change-password")
    public Map<String, String> changePassword(
            @RequestBody Map<String, String> request,
            Authentication authentication) {

        Account account = repository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");

        if (oldPassword == null || newPassword == null ||
                newPassword.length() < 8) {
            throw new RuntimeException("New password must contain at least 8 characters");
        }

        if (!encoder.matches(oldPassword, account.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        account.setPassword(encoder.encode(newPassword));
        repository.save(account);

        return Map.of("message", "Password changed successfully");
    }
}
