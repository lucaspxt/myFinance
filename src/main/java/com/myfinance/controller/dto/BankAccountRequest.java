package com.myfinance.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BankAccountRequest {

    private String name;
    private boolean defaultAccount;
    private boolean archived;
}
