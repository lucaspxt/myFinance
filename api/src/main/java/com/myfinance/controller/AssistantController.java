package com.myfinance.controller;

import com.myfinance.controller.dto.ChatRequest;
import com.myfinance.controller.dto.ChatResponseDTO;
import com.myfinance.service.ChatService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class AssistantController {

    private final ChatService chatService;

    public AssistantController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ChatResponseDTO chat(@RequestBody ChatRequest request) {
        return chatService.chat(request.getMessage());
    }

}
