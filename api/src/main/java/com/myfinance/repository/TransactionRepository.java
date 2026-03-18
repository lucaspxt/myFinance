package com.myfinance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.myfinance.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE t.bankAccount.user.id = :userId")
    List<Transaction> findByUserId(@Param("userId") Long userId);

    @Query("SELECT t FROM Transaction t WHERE t.bankAccount.user.id = :userId " +
           "AND (:categoryId IS NULL OR t.category.id = :categoryId) " +
           "AND (:bankAccountId IS NULL OR t.bankAccount.id = :bankAccountId) " +
           "AND (:month IS NULL OR MONTH(t.createdAt) = :month) " +
           "AND (:year IS NULL OR YEAR(t.createdAt) = :year)")
    List<Transaction> findByUserIdWithFilters(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("bankAccountId") Long bankAccountId,
            @Param("month") Integer month,
            @Param("year") Integer year
    );

    @Query("SELECT DISTINCT YEAR(t.createdAt) FROM Transaction t WHERE t.bankAccount.user.id = :userId ORDER BY YEAR(t.createdAt) DESC")
    List<Integer> findDistinctYearsByUserId(@Param("userId") Long userId);
}
