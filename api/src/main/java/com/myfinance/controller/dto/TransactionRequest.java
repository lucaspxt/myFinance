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
    private Double value;
    private String description;
    // Note: Despite the name, this field represents the transaction date (when it occurred),
    // not the system creation timestamp. It's user-editable.
    private LocalDateTime createdAt;
}
