package com.myfinance.controller.dto;

import java.time.LocalDateTime;

import com.myfinance.model.TransactionType;

public record TransactionDTO(
        Long id,
        TransactionType type,
        CategoryDTO category,
        BankAccountDTO bankAccount,
        Double value,
        String description,
        LocalDateTime createdAt
) {
}
