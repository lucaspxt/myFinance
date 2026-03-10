package com.myfinance.controller;

import com.myfinance.controller.dto.BankAccountBalanceDTO;
import com.myfinance.controller.dto.CategoryBalanceDTO;
import com.myfinance.controller.dto.TotalBalanceDTO;
import com.myfinance.service.BalanceService;
import com.myfinance.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/balance")
public class BalanceController {

    private final BalanceService balanceService;
    private final UserService userService;

    public BalanceController(BalanceService balanceService, UserService userService) {
        this.balanceService = balanceService;
        this.userService = userService;
    }

    @GetMapping("/total")
    public TotalBalanceDTO getTotalBalance() {
        Long userId = userService.getCurrentUserId();
        return balanceService.getTotalBalance(userId);
    }

    @GetMapping("/by-bank-account")
    public List<BankAccountBalanceDTO> getBalanceByBankAccount() {
        Long userId = userService.getCurrentUserId();
        return balanceService.getBalanceByBankAccount(userId);
    }

    @GetMapping("/by-category")
    public List<CategoryBalanceDTO> getBalanceByCategory() {
        Long userId = userService.getCurrentUserId();
        return balanceService.getBalanceByCategory(userId);
    }
}
