package com.myfinance.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.myfinance.controller.dto.TransactionDTO;
import com.myfinance.controller.dto.TransactionMapper;
import com.myfinance.model.BankAccount;
import com.myfinance.model.Category;
import com.myfinance.model.Transaction;
import com.myfinance.model.TransactionType;
import com.myfinance.repository.BankAccountRepository;
import com.myfinance.repository.CategoryRepository;
import com.myfinance.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BankAccountRepository bankAccountRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final TransactionMapper transactionMapper;

    public TransactionDTO create(TransactionType type, Long categoryId, Long bankAccountId, Double value) {
        return create(type, categoryId, bankAccountId, value, null, null);
    }

    public TransactionDTO create(TransactionType type, Long categoryId, Long bankAccountId, Double value, String description) {
        return create(type, categoryId, bankAccountId, value, description, null);
    }

    public TransactionDTO create(TransactionType type, Long categoryId, Long bankAccountId, Double value, String description, LocalDateTime createdAt) {
        Long userId = userService.getCurrentUserId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
        if (!userId.equals(category.getUserId())) {
            throw new RuntimeException("Access denied to this category");
        }
        BankAccount bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElse(bankAccountRepository.findByUserIdAndDefaultAccountTrue(userId)
                        .orElseThrow(() -> new RuntimeException("Default bank account not found for user: " + userId)));
        if (!userId.equals(bankAccount.getUser().getId())) {
            throw new RuntimeException("Access denied to this bank account");
        }
        Transaction transaction = new Transaction(type, category, bankAccount, value, description, createdAt);
        return transactionMapper.toDTO(transactionRepository.save(transaction));
    }

    public TransactionDTO get(Long id) {
        Long userId = userService.getCurrentUserId();
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
        if (!userId.equals(transaction.getBankAccount().getUser().getId())) {
            throw new RuntimeException("Access denied to this transaction");
        }
        return transactionMapper.toDTO(transaction);
    }

    public List<TransactionDTO> getAll() {
        Long userId = userService.getCurrentUserId();
        return transactionRepository.findByUserId(userId).stream()
                .map(transactionMapper::toDTO)
                .toList();
    }

    public List<TransactionDTO> getAllFiltered(Long categoryId, Long bankAccountId, Integer month, Integer year) {
        Long userId = userService.getCurrentUserId();
        return transactionRepository.findByUserIdWithFilters(userId, categoryId, bankAccountId, month, year).stream()
                .map(transactionMapper::toDTO)
                .toList();
    }

    public List<TransactionDTO> getAllFilteredWithPagination(Long categoryId, Long bankAccountId, Integer month, Integer year, Integer limit, Integer offset) {
        Long userId = userService.getCurrentUserId();
        int page = offset / limit;
        Pageable pageable = PageRequest.of(page, limit);
        return transactionRepository.findByUserIdWithFiltersAndPagination(userId, categoryId, bankAccountId, month, year, pageable).stream()
                .map(transactionMapper::toDTO)
                .toList();
    }

    public List<Integer> getDistinctYears() {
        Long userId = userService.getCurrentUserId();
        return transactionRepository.findDistinctYearsByUserId(userId);
    }

    public TransactionDTO update(Long id, TransactionType type, Long categoryId, Long bankAccountId, Double value) {
        return update(id, type, categoryId, bankAccountId, value, null, null);
    }

    public TransactionDTO update(Long id, TransactionType type, Long categoryId, Long bankAccountId, Double value, String description) {
        return update(id, type, categoryId, bankAccountId, value, description, null);
    }

    public TransactionDTO update(Long id, TransactionType type, Long categoryId, Long bankAccountId, Double value, String description, LocalDateTime createdAt) {
        Long userId = userService.getCurrentUserId();
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
        if (!userId.equals(transaction.getBankAccount().getUser().getId())) {
            throw new RuntimeException("Access denied to this transaction");
        }
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
        if (!userId.equals(category.getUserId())) {
            throw new RuntimeException("Access denied to this category");
        }
        BankAccount bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new RuntimeException("BankAccount not found with id: " + bankAccountId));
        if (!userId.equals(bankAccount.getUser().getId())) {
            throw new RuntimeException("Access denied to this bank account");
        }
        transaction.setType(type);
        transaction.setCategory(category);
        transaction.setBankAccount(bankAccount);
        transaction.setValue(value);
        transaction.setDescription(description);
        if (createdAt != null) {
            transaction.setCreatedAt(createdAt);
        }
        return transactionMapper.toDTO(transactionRepository.save(transaction));
    }

    public void delete(Long id) {
        Long userId = userService.getCurrentUserId();
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
        if (!userId.equals(transaction.getBankAccount().getUser().getId())) {
            throw new RuntimeException("Access denied to this transaction");
        }
        transactionRepository.delete(transaction);
    }
}
