package com.myfinance.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.myfinance.controller.dto.BankAccountBalanceDTO;
import com.myfinance.controller.dto.CategoryBalanceDTO;
import com.myfinance.controller.dto.TotalBalanceDTO;
import com.myfinance.model.BankAccount;
import com.myfinance.model.Category;
import com.myfinance.model.Transaction;
import com.myfinance.model.TransactionType;
import com.myfinance.repository.BankAccountRepository;
import com.myfinance.repository.CategoryRepository;
import com.myfinance.repository.TransactionRepository;

@Service
public class BalanceService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final BankAccountRepository bankAccountRepository;

    public BalanceService(TransactionRepository transactionRepository,
                          CategoryRepository categoryRepository,
                          BankAccountRepository bankAccountRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.bankAccountRepository = bankAccountRepository;
    }

    public TotalBalanceDTO getTotalBalance(Long userId) {
        List<Transaction> transactions = transactionRepository.findByUserId(userId);

        double totalIncome = transactions.stream()
                .filter(t -> t.getType() == TransactionType.CREDIT)
                .mapToDouble(Transaction::getValue)
                .sum();

        double totalExpense = transactions.stream()
                .filter(t -> t.getType() == TransactionType.DEBIT)
                .mapToDouble(Transaction::getValue)
                .sum();

        double totalBalance = totalIncome - totalExpense;

        return new TotalBalanceDTO(totalBalance, totalIncome, totalExpense);
    }

    public List<BankAccountBalanceDTO> getBalanceByBankAccount(Long userId) {
        List<BankAccount> bankAccounts = bankAccountRepository.findByUserId(userId);

        return bankAccounts.stream()
                .map(account -> {
                    List<Transaction> transactions = transactionRepository
                            .findByUserIdWithFilters(userId, null, account.getId(), null, null);

                    double balance = calculateBalance(transactions);

                    return new BankAccountBalanceDTO(
                            account.getId(),
                            account.getName(),
                            balance
                    );
                })
                .collect(Collectors.toList());
    }

    public List<CategoryBalanceDTO> getBalanceByCategory(Long userId) {
        List<Category> categories = categoryRepository.findByUserId(userId);

        return categories.stream()
                .map(category -> {
                    List<Transaction> transactions = transactionRepository
                            .findByUserIdWithFilters(userId, category.getId(), null, null, null);

                    double balance = calculateBalance(transactions);

                    return new CategoryBalanceDTO(
                            category.getId(),
                            category.getName(),
                            balance
                    );
                })
                .collect(Collectors.toList());
    }

    private double calculateBalance(List<Transaction> transactions) {
        double income = transactions.stream()
                .filter(t -> t.getType() == TransactionType.CREDIT)
                .mapToDouble(Transaction::getValue)
                .sum();

        double expense = transactions.stream()
                .filter(t -> t.getType() == TransactionType.DEBIT)
                .mapToDouble(Transaction::getValue)
                .sum();

        return income - expense;
    }
}
