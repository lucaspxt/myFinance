package com.myfinance.controller.dto;

public record CategoryDTO(
        Long id,
        String name,
        boolean archived
) {
}
