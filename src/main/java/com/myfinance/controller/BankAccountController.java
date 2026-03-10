package com.myfinance.controller;

import com.myfinance.controller.dto.BankAccountRequest;
import com.myfinance.model.BankAccount;
import com.myfinance.service.BankAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/bank-accounts")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @PostMapping
    public ResponseEntity<BankAccount> create(@RequestBody BankAccountRequest request) {
        return ResponseEntity.ok(bankAccountService.create(
                request.getName(),
                request.isDefaultAccount()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BankAccount> get(@PathVariable Long id) {
        return ResponseEntity.ok(bankAccountService.get(id));
    }

    @GetMapping
    public ResponseEntity<List<BankAccount>> getAll() {
        return ResponseEntity.ok(bankAccountService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BankAccount> update(@PathVariable Long id, @RequestBody BankAccountRequest request) {
        return ResponseEntity.ok(bankAccountService.update(
                id,
                request.getName(),
                request.isDefaultAccount(),
                request.isArchived()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bankAccountService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
