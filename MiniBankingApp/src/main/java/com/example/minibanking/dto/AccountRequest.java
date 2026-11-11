package com.example.minibanking.dto;

public record AccountRequest(
    String username,
    String password
) {
}