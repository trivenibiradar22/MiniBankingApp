package com.example.minibanking.dto;

import java.math.BigDecimal;

public record DepositRequest(
		Long accountId,
		BigDecimal amount){

}
