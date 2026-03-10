package com.myfinance.service;

import com.myfinance.model.BankAccount;
import com.myfinance.model.User;
import com.myfinance.repository.BankAccountRepository;
import com.myfinance.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public BankAccount create(String name, boolean defaultAccount) {
        Long userId = userService.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        if (defaultAccount) {
            clearDefaultAccount(userId);
        }
        BankAccount bankAccount = new BankAccount(name, user);
        bankAccount.setDefaultAccount(defaultAccount);
        return bankAccountRepository.save(bankAccount);
    }

    public BankAccount get(Long id) {
        return bankAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BankAccount not found with id: " + id));
    }

    public List<BankAccount> getAll() {
        return bankAccountRepository.findAll();
    }

    @Transactional
    public BankAccount update(Long id, String name, boolean defaultAccount, boolean archived) {
        BankAccount bankAccount = get(id);
        if (defaultAccount && !bankAccount.isDefaultAccount()) {
            clearDefaultAccount(bankAccount.getUser().getId());
        }
        bankAccount.setName(name);
        bankAccount.setDefaultAccount(defaultAccount);
        bankAccount.setArchived(archived);
        return bankAccountRepository.save(bankAccount);
    }

    public void delete(Long id) {
        BankAccount bankAccount = get(id);
        bankAccountRepository.delete(bankAccount);
    }

    private void clearDefaultAccount(Long userId) {
        bankAccountRepository.findByUserIdAndDefaultAccountTrue(userId).ifPresent(existing -> {
            existing.setDefaultAccount(false);
            bankAccountRepository.save(existing);
        });
    }
}
