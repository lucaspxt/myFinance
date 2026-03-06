package com.myfinance.service;

import com.myfinance.ai.FinanceAssistant;
import com.myfinance.ai.TransactionExtractionResult;
import com.myfinance.model.BankAccount;
import com.myfinance.model.Category;
import com.myfinance.model.Message;
import com.myfinance.model.MessageType;
import com.myfinance.repository.BankAccountRepository;
import com.myfinance.repository.CategoryRepository;
import com.myfinance.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private final FinanceAssistant assistant;
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final TransactionService transactionService;
    private final CategoryRepository categoryRepository;
    private final BankAccountRepository bankAccountRepository;

    public ChatService(FinanceAssistant assistant,
                       MessageRepository messageRepository,
                       UserService userService,
                       TransactionService transactionService,
                       CategoryRepository categoryRepository,
                       BankAccountRepository bankAccountRepository) {
        this.assistant = assistant;
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.transactionService = transactionService;
        this.categoryRepository = categoryRepository;
        this.bankAccountRepository = bankAccountRepository;
    }

    public String chat(String userMessage) {
        Long memoryId = userService.getCurrentUserId();

        messageRepository.save(new Message(userMessage, MessageType.USER, memoryId));

        String response = assistant.chat(memoryId, userMessage);

        tryCreateTransaction(userMessage, memoryId);

        messageRepository.save(new Message(response, MessageType.ASSISTANT, memoryId));

        return response;
    }

    private void tryCreateTransaction(String userMessage, Long userId) {
        try {
            TransactionExtractionResult extraction = assistant.extractTransaction(userId, userMessage);
            if (extraction == null
                    || extraction.type() == null
                    || extraction.categoryName() == null
                    || extraction.bankAccountName() == null
                    || extraction.value() == null) {
                return;
            }

            Optional<Category> category = categoryRepository.findByUserIdAndNameIgnoreCase(userId, extraction.categoryName());
            Optional<BankAccount> bankAccount = bankAccountRepository.findByUserIdAndNameIgnoreCase(userId, extraction.bankAccountName());

            if (category.isEmpty() || bankAccount.isEmpty()) {
                return;
            }

            transactionService.create(extraction.type(), category.get().getId(), bankAccount.get().getId(), extraction.value());
        } catch (Exception e) {
            log.warn("Failed to extract transaction from message", e);
        }
    }
}
