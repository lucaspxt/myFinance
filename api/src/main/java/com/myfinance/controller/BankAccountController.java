package com.myfinance.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myfinance.controller.dto.BankAccountDTO;
import com.myfinance.controller.dto.BankAccountRequest;
import com.myfinance.service.BankAccountService;

@RestController
@RequestMapping("/api/bank-accounts")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @PostMapping
    public BankAccountDTO create(@RequestBody BankAccountRequest request) {
        return bankAccountService.create(
                request.getName(),
                request.isDefaultAccount()
        );
    }

    @GetMapping("/{id}")
    public BankAccountDTO get(@PathVariable Long id) {
        return bankAccountService.get(id);
    }

    @GetMapping
    public List<BankAccountDTO> getAll() {
        return bankAccountService.getAll();
    }

    @PutMapping("/{id}")
    public BankAccountDTO update(@PathVariable Long id, @RequestBody BankAccountRequest request) {
        return bankAccountService.update(
                id,
                request.getName(),
                request.isDefaultAccount(),
                request.isArchived()
        );
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        bankAccountService.delete(id);
    }
}
