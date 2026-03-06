package com.myfinance.service;

import com.myfinance.model.BankAccount;
import com.myfinance.model.User;
import com.myfinance.repository.BankAccountRepository;
import com.myfinance.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private BankAccountService bankAccountService;

    private User user;
    private BankAccount bankAccount;

    @BeforeEach
    void setUp() {
        user = new User("Test User");
        user.setId(1L);

        bankAccount = new BankAccount("Test Account", user);
        bankAccount.setId(1L);
    }

    @Test
    void create_success() {
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(bankAccount);

        BankAccount result = bankAccountService.create("Test Account");

        assertNotNull(result);
        assertEquals("Test Account", result.getName());
        assertEquals(user, result.getUser());
        verify(bankAccountRepository).save(any(BankAccount.class));
    }

    @Test
    void create_userNotFound_throwsException() {
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bankAccountService.create("Test Account"));
    }

    @Test
    void get_success() {
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(bankAccount));

        BankAccount result = bankAccountService.get(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void get_notFound_throwsException() {
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bankAccountService.get(1L));
    }

    @Test
    void getAll_success() {
        when(bankAccountRepository.findAll()).thenReturn(List.of(bankAccount));

        List<BankAccount> result = bankAccountService.getAll();

        assertEquals(1, result.size());
    }

    @Test
    void update_success() {
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(bankAccount));
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(bankAccount);

        BankAccount result = bankAccountService.update(1L, "Updated Account");

        assertEquals("Updated Account", result.getName());
        verify(bankAccountRepository).save(any(BankAccount.class));
    }

    @Test
    void delete_success() {
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(bankAccount));

        bankAccountService.delete(1L);

        verify(bankAccountRepository).delete(bankAccount);
    }

    @Test
    void delete_notFound_throwsException() {
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bankAccountService.delete(1L));
    }
}
