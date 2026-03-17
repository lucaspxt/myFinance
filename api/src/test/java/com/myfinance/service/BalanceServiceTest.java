package com.myfinance.service;

import com.myfinance.controller.dto.BankAccountBalanceDTO;
import com.myfinance.controller.dto.CategoryBalanceDTO;
import com.myfinance.controller.dto.TotalBalanceDTO;
import com.myfinance.model.BankAccount;
import com.myfinance.model.Category;
import com.myfinance.model.Transaction;
import com.myfinance.model.TransactionType;
import com.myfinance.model.User;
import com.myfinance.repository.BankAccountRepository;
import com.myfinance.repository.CategoryRepository;
import com.myfinance.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @InjectMocks
    private BalanceService balanceService;

    private User user;
    private Category category;
    private BankAccount bankAccount;

    @BeforeEach
    void setUp() {
        user = new User("Test User");
        user.setId(1L);

        category = new Category("Food", 1L);
        category.setId(1L);

        bankAccount = new BankAccount("Test Account", user);
        bankAccount.setId(1L);
    }

    @Test
    void getTotalBalance_onlyCountsCompletedTransactions() {
        Transaction completedIncome = new Transaction(TransactionType.CREDIT, category, bankAccount, 100.0, "Income", LocalDateTime.now(), true);
        Transaction completedExpense = new Transaction(TransactionType.DEBIT, category, bankAccount, 30.0, "Expense", LocalDateTime.now(), true);
        Transaction pendingIncome = new Transaction(TransactionType.CREDIT, category, bankAccount, 200.0, "Future Income", LocalDateTime.now().plusDays(5), false);
        Transaction pendingExpense = new Transaction(TransactionType.DEBIT, category, bankAccount, 50.0, "Future Expense", LocalDateTime.now().plusDays(3), false);

        when(transactionRepository.findByUserId(1L)).thenReturn(Arrays.asList(
                completedIncome, completedExpense, pendingIncome, pendingExpense
        ));

        TotalBalanceDTO result = balanceService.getTotalBalance(1L);

        // Only completed transactions should be counted: 100 (income) - 30 (expense) = 70
        assertEquals(70.0, result.totalBalance());
        assertEquals(100.0, result.totalIncome());
        assertEquals(30.0, result.totalExpense());
    }

    @Test
    void getBalanceByBankAccount_onlyCountsCompletedTransactions() {
        Transaction completedIncome = new Transaction(TransactionType.CREDIT, category, bankAccount, 100.0, "Income", LocalDateTime.now(), true);
        Transaction completedExpense = new Transaction(TransactionType.DEBIT, category, bankAccount, 30.0, "Expense", LocalDateTime.now(), true);
        Transaction pendingIncome = new Transaction(TransactionType.CREDIT, category, bankAccount, 200.0, "Future Income", LocalDateTime.now().plusDays(5), false);

        when(bankAccountRepository.findByUserId(1L)).thenReturn(List.of(bankAccount));
        when(transactionRepository.findByUserIdAndBankAccountId(1L, 1L)).thenReturn(Arrays.asList(
                completedIncome, completedExpense, pendingIncome
        ));

        List<BankAccountBalanceDTO> result = balanceService.getBalanceByBankAccount(1L);

        assertEquals(1, result.size());
        assertEquals(70.0, result.get(0).balance()); // 100 - 30 = 70
    }

    @Test
    void getBalanceByCategory_onlyCountsCompletedTransactions() {
        Transaction completedIncome = new Transaction(TransactionType.CREDIT, category, bankAccount, 100.0, "Income", LocalDateTime.now(), true);
        Transaction completedExpense = new Transaction(TransactionType.DEBIT, category, bankAccount, 30.0, "Expense", LocalDateTime.now(), true);
        Transaction pendingExpense = new Transaction(TransactionType.DEBIT, category, bankAccount, 50.0, "Future Expense", LocalDateTime.now().plusDays(3), false);

        when(categoryRepository.findByUserId(1L)).thenReturn(List.of(category));
        when(transactionRepository.findByUserIdAndCategoryId(1L, 1L)).thenReturn(Arrays.asList(
                completedIncome, completedExpense, pendingExpense
        ));

        List<CategoryBalanceDTO> result = balanceService.getBalanceByCategory(1L);

        assertEquals(1, result.size());
        assertEquals(70.0, result.get(0).balance()); // 100 - 30 = 70
    }
}
