package com.myfinance.service;

import com.myfinance.model.BankAccount;
import com.myfinance.model.Category;
import com.myfinance.model.Transaction;
import com.myfinance.model.TransactionType;
import com.myfinance.repository.BankAccountRepository;
import com.myfinance.repository.CategoryRepository;
import com.myfinance.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BankAccountRepository bankAccountRepository;
    private final CategoryRepository categoryRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              BankAccountRepository bankAccountRepository,
                              CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.categoryRepository = categoryRepository;
    }

    public Transaction create(TransactionType type, Long categoryId, Long bankAccountId, Double value) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
        BankAccount bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new RuntimeException("BankAccount not found with id: " + bankAccountId));
        Transaction transaction = new Transaction(type, category, bankAccount, value);
        return transactionRepository.save(transaction);
    }

    public Transaction get(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
    }

    public List<Transaction> getAll() {
        return transactionRepository.findAll();
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
