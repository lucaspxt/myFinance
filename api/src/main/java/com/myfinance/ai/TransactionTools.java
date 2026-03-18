package com.myfinance.ai;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.myfinance.controller.dto.BankAccountDTO;
import com.myfinance.controller.dto.CategoryDTO;
import com.myfinance.controller.dto.TransactionDTO;
import com.myfinance.model.BankAccount;
import com.myfinance.model.Category;
import com.myfinance.model.TransactionType;
import com.myfinance.repository.BankAccountRepository;
import com.myfinance.repository.CategoryRepository;
import com.myfinance.service.BankAccountService;
import com.myfinance.service.CategoryService;
import com.myfinance.service.TransactionService;
import com.myfinance.service.UserService;

import dev.langchain4j.agent.tool.Tool;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class TransactionTools {

    private final TransactionService transactionService;
    private final CategoryRepository categoryRepository;
    private final BankAccountRepository bankAccountRepository;
    private final CategoryService categoryService;
    private final BankAccountService bankAccountService;
    private final UserService userService;

    @Tool("""
            Creates a new financial transaction (income or expense).
            - type: CREDIT for income/revenue, DEBIT for expense/outflow
            - categoryName: category name (use listCategories to see available ones)
            - bankAccountName: bank account name (use listBankAccounts to see available ones). Leave blank to use default account.
            - value: transaction amount
            - description: optional description of what the transaction represents
            - transactionDate: OPTIONAL date parameter. Leave empty/null to use TODAY's date (2026-03-11). Only provide if user explicitly mentions a different date. Format: yyyy-MM-dd or yyyy-MM-ddTHH:mm:ss.
             
             Example for today's date (DO NOT provide transactionDate):
             createTransaction(
                type="DEBIT",
                categoryName="Food",
                bankAccountName="Wallet",
                value=50.75,
                description="Lunch at restaurant",
                transactionDate=null
             )
             
             Example for specific past date:
             createTransaction(
                type="CREDIT",
                categoryName="Salary",
                bankAccountName="Bank",
                value=5000.00,
                description="March salary",
                transactionDate="2026-03-01"
             )
             """)
    public String createTransaction(
            String type,
            String categoryName,
            String bankAccountName,
            Double value,
            String description,
            String transactionDate) {
        log.debug("[TOOL] createTransaction called - type: {}, categoryName: {}, bankAccountName: {}, value: {}, description: {}, transactionDate: {}",
                type, categoryName, bankAccountName, value, description, transactionDate);
        try {
            Long userId = userService.getCurrentUserId();

            // Validate and convert type
            TransactionType transactionType;
            try {
                transactionType = TransactionType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                return "Invalid transaction type. Use CREDIT (income) or DEBIT (expense).";
            }

            // Find category
            Optional<Category> category = categoryRepository.findByUserIdAndNameIgnoreCase(userId, categoryName);
            if (category.isEmpty()) {
                return "Category not found: " + categoryName + ". Use listCategories() to see available categories.";
            }

            // Find bank account
            Optional<BankAccount> bankAccount;
            if (bankAccountName == null || bankAccountName.isBlank()) {
                bankAccount = bankAccountRepository.findByUserIdAndDefaultAccountTrue(userId);
                if (bankAccount.isEmpty()) {
                    return "Default bank account not found. Please specify an account or configure a default account.";
                }
            } else {
                bankAccount = bankAccountRepository.findByUserIdAndNameIgnoreCase(userId, bankAccountName);
                if (bankAccount.isEmpty()) {
                    return "Bank account not found: " + bankAccountName + ". Use listBankAccounts() to see available accounts.";
                }
            }

            // Parse transaction date (default to today if not provided)
            LocalDateTime createdAt = LocalDateTime.now();
            if (transactionDate != null && !transactionDate.isBlank()) {
                try {
                    // Try parsing as LocalDateTime first (yyyy-MM-ddTHH:mm:ss)
                    createdAt = LocalDateTime.parse(transactionDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                } catch (DateTimeParseException e1) {
                    try {
                        // Try parsing as LocalDate (yyyy-MM-dd) and convert to LocalDateTime at start of day
                        createdAt = LocalDate.parse(transactionDate, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
                    } catch (DateTimeParseException e2) {
                        return "Invalid date format. Use yyyy-MM-dd (e.g., 2026-03-10) or yyyy-MM-ddTHH:mm:ss (e.g., 2026-03-10T14:30:00).";
                    }
                }
            }

            // Create transaction
            transactionService.create(
                    transactionType,
                    category.get().getId(),
                    bankAccount.get().getId(),
                    value,
                    description,
                    createdAt
            );
            
            // Mark that a transaction occurred
            TransactionContext.markTransactionOccurred();

            String result = String.format("✅ Transaction created successfully!\nType: %s\nCategory: %s\nAccount: %s\nAmount: $ %.2f",
                    transactionType == TransactionType.CREDIT ? "Income" : "Expense",
                    category.get().getName(),
                    bankAccount.get().getName(),
                    value);
            
            if (description != null && !description.isBlank()) {
                result += "\nDescription: " + description;
            }
            
            if (createdAt != null) {
                result += "\nDate: " + createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            }
            
            return result;
        } catch (Exception e) {
            return "❌ Error creating transaction: " + e.getMessage();
        }
    }

    @Tool("Lists all categories available to the user")
    public String listCategories() {
        log.debug("[TOOL] listCategories called");
        try {
            Long userId = userService.getCurrentUserId();
            List<Category> categories = categoryRepository.findByUserId(userId);

            if (categories.isEmpty()) {
                return "No categories found.";
            }

            String categoriesList = categories.stream()
                    .map(c -> "- " + c.getName())
                    .collect(Collectors.joining("\n"));

            return "Available categories:\n" + categoriesList;
        } catch (Exception e) {
            return "Error listing categories: " + e.getMessage();
        }
    }

    @Tool("Lists all bank accounts available to the user")
    public String listBankAccounts() {
        log.debug("[TOOL] listBankAccounts called");
        try {
            Long userId = userService.getCurrentUserId();
            List<BankAccount> accounts = bankAccountRepository.findByUserId(userId);

            if (accounts.isEmpty()) {
                return "No bank accounts found.";
            }

            String accountsList = accounts.stream()
                    .map(a -> String.format("- %s%s",
                            a.getName(),
                            a.isDefaultAccount() ? " [DEFAULT]" : ""))
                    .collect(Collectors.joining("\n"));

            return "Available bank accounts:\n" + accountsList;
        } catch (Exception e) {
            return "Error listing accounts: " + e.getMessage();
        }
    }

    @Tool("""
            Updates an existing transaction.
            - transactionId: ID of the transaction to update
            - type: CREDIT for income/revenue, DEBIT for expense/outflow
            - categoryName: new category name
            - bankAccountName: new bank account name
            - value: new transaction amount
            - description: optional description of what the transaction represents
            - transactionDate: OPTIONAL - leave empty/null to keep the original transaction date. Only provide if changing the date. Format: yyyy-MM-dd or yyyy-MM-ddTHH:mm:ss
            """)
    public String updateTransaction(
            Long transactionId,
            String type,
            String categoryName,
            String bankAccountName,
            Double value,
            String description,
            String transactionDate) {
        log.debug("[TOOL] updateTransaction called - transactionId: {}, type: {}, categoryName: {}, bankAccountName: {}, value: {}, description: {}, transactionDate: {}",
                transactionId, type, categoryName, bankAccountName, value, description, transactionDate);
        try {
            Long userId = userService.getCurrentUserId();

            // Validate transaction exists (service layer will handle authorization)
            TransactionDTO existing = transactionService.get(transactionId);

            // Validate and convert type
            TransactionType transactionType;
            try {
                transactionType = TransactionType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                return "Invalid transaction type. Use CREDIT (income) or DEBIT (expense).";
            }

            // Find category
            Optional<Category> category = categoryRepository.findByUserIdAndNameIgnoreCase(userId, categoryName);
            if (category.isEmpty()) {
                return "Category not found: " + categoryName;
            }

            // Find bank account
            Optional<BankAccount> bankAccount = bankAccountRepository.findByUserIdAndNameIgnoreCase(userId, bankAccountName);
            if (bankAccount.isEmpty()) {
                return "Bank account not found: " + bankAccountName;
            }

            // Parse transaction date (keep existing date if not provided in update)
            LocalDateTime createdAt = null;
            if (transactionDate != null && !transactionDate.isBlank()) {
                try {
                    createdAt = LocalDateTime.parse(transactionDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                } catch (DateTimeParseException e1) {
                    try {
                        createdAt = LocalDate.parse(transactionDate, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
                    } catch (DateTimeParseException e2) {
                        return "Invalid date format. Use yyyy-MM-dd or yyyy-MM-ddTHH:mm:ss.";
                    }
                }
            }

            // Update transaction
            transactionService.update(
                    transactionId,
                    transactionType,
                    category.get().getId(),
                    bankAccount.get().getId(),
                    value,
                    description,
                    createdAt
            );
            
            // Mark that a transaction occurred
            TransactionContext.markTransactionOccurred();

            String result = String.format("✅ Transaction updated successfully!\nID: %d\nType: %s\nCategory: %s\nAccount: %s\nAmount: $ %.2f",
                    transactionId,
                    transactionType == TransactionType.CREDIT ? "Income" : "Expense",
                    category.get().getName(),
                    bankAccount.get().getName(),
                    value);
            
            if (description != null && !description.isBlank()) {
                result += "\nDescription: " + description;
            }
            
            if (createdAt != null) {
                result += "\nDate: " + createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            }
            
            return result;
        } catch (Exception e) {
            return "❌ Error updating transaction: " + e.getMessage();
        }
    }

    @Tool("""
            Deletes a transaction by its ID.
            - transactionId: ID of the transaction to delete
            """)
    public String deleteTransaction(Long transactionId) {
        log.debug("[TOOL] deleteTransaction called - transactionId: {}", transactionId);
        try {
            Long userId = userService.getCurrentUserId();

            // Validate transaction exists and belongs to user
            TransactionDTO existing = transactionService.get(transactionId);
            // Note: DTO doesn't expose user relationship directly, trust service layer validation

            transactionService.delete(transactionId);
            
            // Mark that a transaction occurred
            TransactionContext.markTransactionOccurred();

            return String.format("✅ Transaction #%d deleted successfully!", transactionId);
        } catch (Exception e) {
            return "❌ Error deleting transaction: " + e.getMessage();
        }
    }

    @Tool("""
            Lists all transactions (transaction history) for the current user.
            Returns a formatted list with transaction details.
            """)
    public String getTransactionHistory() {
        log.debug("[TOOL] getTransactionHistory called");
        try {
            List<TransactionDTO> transactions = transactionService.getAll();

            if (transactions.isEmpty()) {
                return "No transactions found.";
            }

            StringBuilder history = new StringBuilder("Transaction History:\n\n");
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            for (TransactionDTO t : transactions) {
                history.append(String.format("ID: %d | %s | %s | %s | %s | $ %.2f",
                        t.id(),
                        t.createdAt() != null ? t.createdAt().format(dateFormatter) : "N/A",
                        t.type() == TransactionType.CREDIT ? "Income" : "Expense",
                        t.category().name(),
                        t.bankAccount().name(),
                        t.value()));
                
                if (t.description() != null && !t.description().isBlank()) {
                    history.append(String.format(" | %s", t.description()));
                }
                
                history.append("\n");
            }

            return history.toString();
        } catch (Exception e) {
            return "❌ Error retrieving transaction history: " + e.getMessage();
        }
    }

    @Tool("""
            Creates a new category for organizing transactions.
            - categoryName: name of the category to create
            """)
    public String createCategory(String categoryName) {
        log.debug("[TOOL] createCategory called - categoryName: {}", categoryName);
        try {
            CategoryDTO category = categoryService.create(categoryName);

            return String.format("✅ Category created successfully!\nID: %d\nName: %s",
                    category.id(),
                    category.name());
        } catch (Exception e) {
            return "❌ Error creating category: " + e.getMessage();
        }
    }

    @Tool("""
            Creates a new bank account.
            - accountName: name of the bank account to create
            - setAsDefault: true to set this as the default account, false otherwise
            """)
    public String createBankAccount(String accountName, boolean setAsDefault) {
        log.debug("[TOOL] createBankAccount called - accountName: {}, setAsDefault: {}", accountName, setAsDefault);
        try {
            BankAccountDTO account = bankAccountService.create(accountName, setAsDefault);

            return String.format("✅ Bank account created successfully!\nID: %d\nName: %s%s",
                    account.id(),
                    account.name(),
                    account.defaultAccount() ? " [DEFAULT]" : "");
        } catch (Exception e) {
            return "❌ Error creating bank account: " + e.getMessage();
        }
    }
}
