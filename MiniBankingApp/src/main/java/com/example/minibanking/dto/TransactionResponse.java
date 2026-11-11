
package com.example.minibanking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long transactionId,
        String transactionType,
        Long senderId,
        Long receiverId,
        BigDecimal amount,
        LocalDateTime transactionDate,
        String status
) {}
