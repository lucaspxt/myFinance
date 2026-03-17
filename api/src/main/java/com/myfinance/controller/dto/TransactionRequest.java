package com.myfinance.controller.dto;

import com.myfinance.model.TransactionType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TransactionRequest {

    private TransactionType type;
    private Long categoryId;
    private Long bankAccountId;
    private Long fromAccountId;
    private Double value;
    private String description;
    private LocalDateTime createdAt;
}
