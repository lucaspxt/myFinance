package com.myfinance.ai;

import org.springframework.stereotype.Component;

/**
 * Context holder for tracking transaction operations during AI chat interactions.
 * Uses ThreadLocal to ensure thread-safety across concurrent requests.
 */
@Component
public class TransactionContext {
    
    private static final ThreadLocal<Boolean> hasTransaction = ThreadLocal.withInitial(() -> false);
    
    /**
     * Marks that a transaction operation occurred in the current thread
     */
    public static void markTransactionOccurred() {
        hasTransaction.set(true);
    }
    
    /**
     * Checks if a transaction operation occurred in the current thread
     */
    public static boolean hasTransactionOccurred() {
        return hasTransaction.get();
    }
    
    /**
     * Clears the transaction flag for the current thread.
     * Should be called at the end of each request to prevent memory leaks.
     */
    public static void clear() {
        hasTransaction.remove();
    }
}
