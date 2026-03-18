package com.myfinance.controller.dto;

public record BankAccountDTO(
        Long id,
        String name,
        boolean defaultAccount,
        boolean archived
) {
}
