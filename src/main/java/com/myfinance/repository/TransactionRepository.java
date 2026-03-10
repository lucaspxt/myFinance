package com.myfinance.repository;

import com.myfinance.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE t.bankAccount.user.id = :userId")
    List<Transaction> findByUserId(@Param("userId") Long userId);

    @Query("SELECT t FROM Transaction t WHERE t.bankAccount.user.id = :userId AND t.bankAccount.id = :bankAccountId")
    List<Transaction> findByUserIdAndBankAccountId(@Param("userId") Long userId, @Param("bankAccountId") Long bankAccountId);

    @Query("SELECT t FROM Transaction t WHERE t.bankAccount.user.id = :userId AND t.category.id = :categoryId")
    List<Transaction> findByUserIdAndCategoryId(@Param("userId") Long userId, @Param("categoryId") Long categoryId);
}
