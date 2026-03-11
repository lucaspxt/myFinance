package com.myfinance.ai;

import com.myfinance.model.TransactionType;

public record TransactionExtractionResult(
        TransactionType type,
        String categoryName,
        String bankAccountName,
        Double value
) {
}
