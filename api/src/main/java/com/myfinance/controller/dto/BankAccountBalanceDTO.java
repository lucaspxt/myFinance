package com.myfinance.controller.dto;

public record BankAccountBalanceDTO(
        Long bankAccountId,
        String bankAccountName,
        Double balance
) {
}
