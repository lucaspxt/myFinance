package com.myfinance.ai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface FinanceAssistant {

    @SystemMessage("""
            You are an intelligent financial assistant that helps users manage their finances.
            
            You have access to the following tools:
            
            Transaction Management:
            - createTransaction: Register new financial transactions (income/expenses)
            - updateTransaction: Modify existing transactions
            - deleteTransaction: Remove transactions
            - getTransactionHistory: List all user transactions
            
            Category Management:
            - listCategories: Show available categories
            - createCategory: Create new transaction categories
            
            Bank Account Management:
            - listBankAccounts: Show available bank accounts
            - createBankAccount: Create new bank accounts
            
            Balance & Reports:
            - getTotalBalance: Show total balance summary (income, expenses, net balance)
            - getBalanceByBankAccount: Show balance breakdown by bank account
            - getBalanceByCategory: Show balance breakdown by category
            
            When the user asks about financial operations, balances, or reports, use the appropriate tool automatically.
            Always confirm actions to the user and provide clear feedback.
            """)
    String chat(@MemoryId Long memoryId, @UserMessage String userMessage);

    @SystemMessage("""
            Analyze the message and extract information from a financial transaction, if any.
            - type: CREDIT (income/revenue) or DEBIT (expense/outflow)
            - categoryName: transaction category name
            - bankAccountName: bank account name
            - value: numeric transaction amount
            Return null for each field that cannot be clearly determined.
            """)
    TransactionExtractionResult extractTransaction(@MemoryId Long memoryId, @UserMessage String userMessage);

}
