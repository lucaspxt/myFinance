package com.myfinance.controller.dto;

import org.springframework.stereotype.Component;

import com.myfinance.model.Transaction;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TransactionMapper {

    private final CategoryMapper categoryMapper;
    private final BankAccountMapper bankAccountMapper;

    public TransactionDTO toDTO(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        return new TransactionDTO(
                transaction.getId(),
                transaction.getType(),
                categoryMapper.toDTO(transaction.getCategory()),
                bankAccountMapper.toDTO(transaction.getBankAccount()),
                transaction.getValue(),
                transaction.getDescription(),
                transaction.getCreatedAt()
        );
    }
}
