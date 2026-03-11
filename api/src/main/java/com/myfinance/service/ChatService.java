package com.myfinance.service;

import org.springframework.stereotype.Service;

import com.myfinance.ai.FinanceAssistant;
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
        Long memoryId = userService.getCurrentUserId();

        messageRepository.save(new Message(userMessage, MessageType.USER, memoryId));

        String response = assistant.chat(memoryId, userMessage);

        messageRepository.save(new Message(response, MessageType.ASSISTANT, memoryId));

        // Always set isTransaction=true as the AI assistant might have created/updated/deleted transactions
        return new ChatResponseDTO(response, "success", true);
    }
}
