package com.myfinance.service;

import org.springframework.stereotype.Service;

import com.myfinance.ai.FinanceAssistant;
import com.myfinance.ai.TransactionContext;
import com.myfinance.controller.dto.ChatResponseDTO;
import com.myfinance.model.Message;
import com.myfinance.model.MessageType;
import com.myfinance.repository.MessageRepository;

@Service
public class ChatService {

    private final FinanceAssistant assistant;
    private final MessageRepository messageRepository;
    private final UserService userService;

    public ChatService(FinanceAssistant assistant,
                       MessageRepository messageRepository,
                       UserService userService) {
        this.assistant = assistant;
        this.messageRepository = messageRepository;
        this.userService = userService;
    }

    public ChatResponseDTO chat(String userMessage) {
        try {
            Long memoryId = userService.getCurrentUserId();

            messageRepository.save(new Message(userMessage, MessageType.USER, memoryId));

            String response = assistant.chat(memoryId, userMessage);

            messageRepository.save(new Message(response, MessageType.ASSISTANT, memoryId));

            // Only set isTransaction=true if the AI assistant actually created/updated/deleted a transaction
            boolean isTransaction = TransactionContext.hasTransactionOccurred();
            return new ChatResponseDTO(response, "success", isTransaction);
        } finally {
            // Clear ThreadLocal to prevent memory leaks
            TransactionContext.clear();
        }
    }
}
