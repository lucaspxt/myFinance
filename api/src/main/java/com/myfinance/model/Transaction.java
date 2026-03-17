package com.myfinance.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "bank_account_id")
    private BankAccount bankAccount;

    @ManyToOne
    @JoinColumn(name = "from_account_id")
    private BankAccount fromAccount;

    private Double value;

    private String description;

    private LocalDateTime createdAt;

    public Transaction(TransactionType type, Category category, BankAccount bankAccount, Double value) {
        this(type, category, bankAccount, value, null, LocalDateTime.now());
    }

    public Transaction(TransactionType type, Category category, BankAccount bankAccount, Double value, String description) {
        this(type, category, bankAccount, value, description, LocalDateTime.now());
    }

    public Transaction(TransactionType type, Category category, BankAccount bankAccount, Double value, String description, LocalDateTime createdAt) {
        this(type, category, bankAccount, null, value, description, createdAt);
    }

    public Transaction(TransactionType type, Category category, BankAccount bankAccount, BankAccount fromAccount, Double value, String description, LocalDateTime createdAt) {
        this.type = type;
        this.category = category;
        this.bankAccount = bankAccount;
        this.fromAccount = fromAccount;
        this.value = value;
        this.description = description;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
}
