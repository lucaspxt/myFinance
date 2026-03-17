package com.myfinance.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myfinance.controller.dto.TransactionRequest;
import com.myfinance.model.Transaction;
import com.myfinance.service.TransactionService;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<Transaction> create(@RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.create(
                request.getType(),
                request.getCategoryId(),
                request.getBankAccountId(),
                request.getFromAccountId(),
                request.getValue(),
                request.getDescription(),
                request.getCreatedAt()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> get(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.get(id));
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAll() {
        return ResponseEntity.ok(transactionService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> update(@PathVariable Long id, @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.update(
                id,
                request.getType(),
                request.getCategoryId(),
                request.getBankAccountId(),
                request.getFromAccountId(),
                request.getValue(),
                request.getDescription(),
                request.getCreatedAt()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        transactionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
