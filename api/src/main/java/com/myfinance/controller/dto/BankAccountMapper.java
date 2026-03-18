package com.myfinance.controller.dto;

import org.springframework.stereotype.Component;

import com.myfinance.model.BankAccount;

@Component
public class BankAccountMapper {

    public BankAccountDTO toDTO(BankAccount bankAccount) {
        if (bankAccount == null) {
            return null;
        }
        return new BankAccountDTO(
                bankAccount.getId(),
                bankAccount.getName(),
                bankAccount.isDefaultAccount(),
                bankAccount.isArchived()
        );
    }
}
