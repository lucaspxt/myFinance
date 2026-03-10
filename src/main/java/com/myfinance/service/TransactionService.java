package com.myfinance.service;

import com.myfinance.model.BankAccount;
import com.myfinance.model.Category;
import com.myfinance.model.Transaction;
import com.myfinance.model.TransactionType;
import com.myfinance.repository.BankAccountRepository;
import com.myfinance.repository.CategoryRepository;
import com.myfinance.repository.TransactionRepository;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BankAccountRepository bankAccountRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;

    public Transaction create(TransactionType type, Long categoryId, Long bankAccountId, Double value) {
        Long userId = userService.getCurrentUserId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
        BankAccount bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElse(bankAccountRepository.findByUserIdAndDefaultAccountTrue(userId)
                        .orElseThrow(() -> new RuntimeException("Default bank account not found for user: " + userId)));
        Transaction transaction = new Transaction(type, category, bankAccount, value);
        return transactionRepository.save(transaction);
    }

    public Transaction get(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
    }

    public List<Transaction> getAll() {
        Long userId = userService.getCurrentUserId();
        return transactionRepository.findByUserId(userId);
    }

    public Transaction update(Long id, TransactionType type, Long categoryId, Long bankAccountId, Double value) {
        Transaction transaction = get(id);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
        BankAccount bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new RuntimeException("BankAccount not found with id: " + bankAccountId));
        transaction.setType(type);
        transaction.setCategory(category);
        transaction.setBankAccount(bankAccount);
        transaction.setValue(value);
        return transactionRepository.save(transaction);
    }

    public void delete(Long id) {
        Transaction transaction = get(id);
        transactionRepository.delete(transaction);
    }
}
