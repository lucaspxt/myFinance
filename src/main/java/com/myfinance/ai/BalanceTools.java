package com.myfinance.ai;

import com.myfinance.controller.dto.BankAccountBalanceDTO;
import com.myfinance.controller.dto.CategoryBalanceDTO;
import com.myfinance.controller.dto.TotalBalanceDTO;
import com.myfinance.service.BalanceService;
import com.myfinance.service.UserService;
import dev.langchain4j.agent.tool.Tool;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class BalanceTools {

    private final BalanceService balanceService;
    private final UserService userService;

    @Tool("""
            Gets the total balance summary for the user.
            Returns total balance (income - expenses), total income, and total expenses.
            """)
    public String getTotalBalance() {
        try {
            Long userId = userService.getCurrentUserId();
            TotalBalanceDTO balance = balanceService.getTotalBalance(userId);

            return String.format("""
                    💰 Total Balance Summary:
                    
                    Total Balance: $ %.2f
                    Total Income:  $ %.2f
                    Total Expense: $ %.2f
                    """,
                    balance.totalBalance(),
                    balance.totalIncome(),
                    balance.totalExpense());
        } catch (Exception e) {
            return "❌ Error retrieving total balance: " + e.getMessage();
        }
    }

    @Tool("""
            Gets the balance breakdown by bank account.
            Shows how much money is in each account (income - expenses per account).
            """)
    public String getBalanceByBankAccount() {
        try {
            Long userId = userService.getCurrentUserId();
            List<BankAccountBalanceDTO> balances = balanceService.getBalanceByBankAccount(userId);

            if (balances.isEmpty()) {
                return "No bank accounts found.";
            }

            StringBuilder result = new StringBuilder("💳 Balance by Bank Account:\n\n");
            for (BankAccountBalanceDTO balance : balances) {
                result.append(String.format("%-20s $ %.2f\n",
                        balance.bankAccountName() + ":",
                        balance.balance()));
            }

            return result.toString();
        } catch (Exception e) {
            return "❌ Error retrieving balance by bank account: " + e.getMessage();
        }
    }

    @Tool("""
            Gets the balance breakdown by category.
            Shows net balance (income - expenses) for each category.
            Useful to see spending/earning patterns by category.
            """)
    public String getBalanceByCategory() {
        try {
            Long userId = userService.getCurrentUserId();
            List<CategoryBalanceDTO> balances = balanceService.getBalanceByCategory(userId);

            if (balances.isEmpty()) {
                return "No categories found.";
            }

            StringBuilder result = new StringBuilder("📊 Balance by Category:\n\n");
            for (CategoryBalanceDTO balance : balances) {
                String indicator = balance.balance() >= 0 ? "+" : "";
                result.append(String.format("%-20s %s$ %.2f\n",
                        balance.categoryName() + ":",
                        indicator,
                        balance.balance()));
            }

            return result.toString();
        } catch (Exception e) {
            return "❌ Error retrieving balance by category: " + e.getMessage();
        }
    }
}
