package com.myfinance.controller.dto;

public record CategoryBalanceDTO(
        Long categoryId,
        String categoryName,
        Double balance
) {
}
