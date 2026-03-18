package com.myfinance.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.myfinance.controller.dto.TransactionDTO;
import com.myfinance.model.BankAccount;
import com.myfinance.model.Category;
import com.myfinance.model.Transaction;
import com.myfinance.model.TransactionType;
import com.myfinance.model.User;
import com.myfinance.repository.BankAccountRepository;
import com.myfinance.repository.CategoryRepository;
import com.myfinance.repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TransactionService transactionService;

    private Category category;
    private BankAccount bankAccount;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        User user = new User("Test User");
        user.setId(1L);

        category = new Category("Food", 1L);
        category.setId(1L);

        bankAccount = new BankAccount("Test Account", user);
        bankAccount.setId(1L);

        transaction = new Transaction(TransactionType.DEBIT, category, bankAccount, 100.0);
        transaction.setId(1L);
    }

    @Test
    void create_success() {
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(bankAccount));
        when(bankAccountRepository.findByUserIdAndDefaultAccountTrue(1L)).thenReturn(Optional.of(bankAccount));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionDTO result = transactionService.create(TransactionType.DEBIT, 1L, 1L, 100.0);

        assertNotNull(result);
        assertEquals(TransactionType.DEBIT, result.type());
        assertEquals(100.0, result.value());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void create_categoryNotFound_throwsException() {
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> transactionService.create(TransactionType.DEBIT, 1L, 1L, 100.0));
    }

    @Test
    void create_bankAccountNotFound_throwsException() {
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.empty());
        when(bankAccountRepository.findByUserIdAndDefaultAccountTrue(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> transactionService.create(TransactionType.DEBIT, 1L, 1L, 100.0));
    }

    @Test
    void get_success() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        TransactionDTO result = transactionService.get(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    void get_notFound_throwsException() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> transactionService.get(1L));
    }

    @Test
    void getAll_success() {
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(transactionRepository.findByUserId(1L)).thenReturn(List.of(transaction));

        List<TransactionDTO> result = transactionService.getAll();

        assertEquals(1, result.size());
    }

    @Test
    void update_success() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(bankAccount));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionDTO result = transactionService.update(1L, TransactionType.CREDIT, 1L, 1L, 200.0);

        assertNotNull(result);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void delete_success() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        transactionService.delete(1L);

        verify(transactionRepository).delete(transaction);
    }

    @Test
    void delete_notFound_throwsException() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> transactionService.delete(1L));
    }
}
