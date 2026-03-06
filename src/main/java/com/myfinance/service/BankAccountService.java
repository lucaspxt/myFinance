package com.myfinance.service;

import com.myfinance.model.BankAccount;
import com.myfinance.model.User;
import com.myfinance.repository.BankAccountRepository;
import com.myfinance.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public BankAccountService(BankAccountRepository bankAccountRepository,
                              UserRepository userRepository,
                              UserService userService) {
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public BankAccount create(String name) {
        Long userId = userService.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        BankAccount bankAccount = new BankAccount(name, user);
        return bankAccountRepository.save(bankAccount);
    }

    public BankAccount get(Long id) {
        return bankAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BankAccount not found with id: " + id));
    }

    public List<BankAccount> getAll() {
        return bankAccountRepository.findAll();
    }

    public BankAccount update(Long id, String name) {
        BankAccount bankAccount = get(id);
        bankAccount.setName(name);
        return bankAccountRepository.save(bankAccount);
    }

    public void delete(Long id) {
        BankAccount bankAccount = get(id);
        bankAccountRepository.delete(bankAccount);
    }
}
