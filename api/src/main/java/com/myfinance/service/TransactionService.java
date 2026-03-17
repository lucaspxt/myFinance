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

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BankAccountRepository bankAccountRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;

    public Transaction create(TransactionType type, Long categoryId, Long bankAccountId, Double value) {
        return create(type, categoryId, bankAccountId, value, null, null, null);
    }

    public Transaction create(TransactionType type, Long categoryId, Long bankAccountId, Double value, String description) {
        return create(type, categoryId, bankAccountId, value, description, null, null);
    }

    public Transaction create(TransactionType type, Long categoryId, Long bankAccountId, Double value, String description, LocalDateTime createdAt) {
        return create(type, categoryId, bankAccountId, null, value, description, createdAt);
    }

    public Transaction create(TransactionType type, Long categoryId, Long bankAccountId, Long fromAccountId, Double value, String description, LocalDateTime createdAt) {
        Long userId = userService.getCurrentUserId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
        BankAccount bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElse(bankAccountRepository.findByUserIdAndDefaultAccountTrue(userId)
                        .orElseThrow(() -> new RuntimeException("Default bank account not found for user: " + userId)));
        
        BankAccount fromAccount = null;
        if (fromAccountId != null) {
            fromAccount = bankAccountRepository.findById(fromAccountId)
                    .orElseThrow(() -> new RuntimeException("From account not found with id: " + fromAccountId));
        }
        
        Transaction transaction = new Transaction(type, category, bankAccount, fromAccount, value, description, createdAt);
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
        return update(id, type, categoryId, bankAccountId, value, null, null, null);
    }

    public Transaction update(Long id, TransactionType type, Long categoryId, Long bankAccountId, Double value, String description) {
        return update(id, type, categoryId, bankAccountId, value, description, null, null);
    }

    public Transaction update(Long id, TransactionType type, Long categoryId, Long bankAccountId, Double value, String description, LocalDateTime createdAt) {
        return update(id, type, categoryId, bankAccountId, null, value, description, createdAt);
    }

    public Transaction update(Long id, TransactionType type, Long categoryId, Long bankAccountId, Long fromAccountId, Double value, String description, LocalDateTime createdAt) {
        Transaction transaction = get(id);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
        BankAccount bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new RuntimeException("BankAccount not found with id: " + bankAccountId));
        
        BankAccount fromAccount = null;
        if (fromAccountId != null) {
            fromAccount = bankAccountRepository.findById(fromAccountId)
                    .orElseThrow(() -> new RuntimeException("From account not found with id: " + fromAccountId));
        }
        
        transaction.setType(type);
        transaction.setCategory(category);
        transaction.setBankAccount(bankAccount);
        transaction.setFromAccount(fromAccount);
        transaction.setValue(value);
        transaction.setDescription(description);
        if (createdAt != null) {
            transaction.setCreatedAt(createdAt);
        }
        return transactionRepository.save(transaction);
    }

    public void delete(Long id) {
        Transaction transaction = get(id);
        transactionRepository.delete(transaction);
    }
}
