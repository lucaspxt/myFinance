package com.myfinance.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.myfinance.controller.dto.TransactionDTO;
import com.myfinance.controller.dto.TransactionRequest;
import com.myfinance.service.TransactionService;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public TransactionDTO create(@RequestBody TransactionRequest request) {
        return transactionService.create(
                request.getType(),
                request.getCategoryId(),
                request.getBankAccountId(),
                request.getValue(),
                request.getDescription(),
                request.getCreatedAt()
        );
    }

    @GetMapping("/{id}")
    public TransactionDTO get(@PathVariable Long id) {
        return transactionService.get(id);
    }

    @GetMapping
    public List<TransactionDTO> getAll(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long bankAccountId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset) {
        // If limit and offset are provided, use pagination
        if (limit != null && offset != null) {
            return transactionService.getAllFilteredWithPagination(categoryId, bankAccountId, month, year, limit, offset);
        }
        // Otherwise, return all matching transactions
        return transactionService.getAllFiltered(categoryId, bankAccountId, month, year);
    }

    @GetMapping("/years")
    public List<Integer> getDistinctYears() {
        return transactionService.getDistinctYears();
    }

    @PutMapping("/{id}")
    public TransactionDTO update(@PathVariable Long id, @RequestBody TransactionRequest request) {
        return transactionService.update(
                id,
                request.getType(),
                request.getCategoryId(),
                request.getBankAccountId(),
                request.getValue(),
                request.getDescription(),
                request.getCreatedAt()
        );
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        transactionService.delete(id);
    }
}
