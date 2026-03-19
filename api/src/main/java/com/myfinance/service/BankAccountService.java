package com.myfinance.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myfinance.controller.dto.BankAccountDTO;
import com.myfinance.controller.dto.BankAccountMapper;
import com.myfinance.model.BankAccount;
import com.myfinance.model.User;
import com.myfinance.repository.BankAccountRepository;
import com.myfinance.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final BankAccountMapper bankAccountMapper;

    @Transactional
    public BankAccountDTO create(String name, boolean defaultAccount) {
        Long userId = userService.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        if (defaultAccount) {
            clearDefaultAccount(userId);
        }
        BankAccount bankAccount = new BankAccount(name, user);
        bankAccount.setDefaultAccount(defaultAccount);
        return bankAccountMapper.toDTO(bankAccountRepository.save(bankAccount));
    }

    public BankAccountDTO get(Long id) {
        Long userId = userService.getCurrentUserId();
        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BankAccount not found with id: " + id));
        if (!bankAccount.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied to this bank account");
        }
        return bankAccountMapper.toDTO(bankAccount);
    }

    public List<BankAccountDTO> getAll() {
        Long userId = userService.getCurrentUserId();
        return bankAccountRepository.findByUserId(userId).stream()
                .map(bankAccountMapper::toDTO)
                .toList();
    }

    @Transactional
    public BankAccountDTO update(Long id, String name, boolean defaultAccount, boolean archived) {
        Long userId = userService.getCurrentUserId();
        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BankAccount not found with id: " + id));
        if (!bankAccount.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied to this bank account");
        }
        if (defaultAccount && !bankAccount.isDefaultAccount()) {
            clearDefaultAccount(bankAccount.getUser().getId());
        }
        bankAccount.setName(name);
        bankAccount.setDefaultAccount(defaultAccount);
        bankAccount.setArchived(archived);
        return bankAccountMapper.toDTO(bankAccountRepository.save(bankAccount));
    }

    public void delete(Long id) {
        Long userId = userService.getCurrentUserId();
        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BankAccount not found with id: " + id));
        if (!bankAccount.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied to this bank account");
        }
        bankAccountRepository.delete(bankAccount);
    }

    private void clearDefaultAccount(Long userId) {
        bankAccountRepository.findByUserIdAndDefaultAccountTrue(userId).ifPresent(existing -> {
            existing.setDefaultAccount(false);
            bankAccountRepository.save(existing);
        });
    }
}
