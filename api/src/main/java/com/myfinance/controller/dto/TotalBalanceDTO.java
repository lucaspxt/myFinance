package com.myfinance.controller.dto;

public record TotalBalanceDTO(
        Double totalBalance,
        Double totalIncome,
        Double totalExpense
) {
}
