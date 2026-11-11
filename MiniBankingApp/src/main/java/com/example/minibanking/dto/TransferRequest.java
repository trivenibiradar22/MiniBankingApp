package com.example.minibanking.dto;

import java.math.BigDecimal;

public record TransferRequest(
    Long senderId,
    Long receiverId,
    BigDecimal amount
) {
}