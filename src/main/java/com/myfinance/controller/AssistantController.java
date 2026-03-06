package com.myfinance.controller;

import com.myfinance.controller.dto.ChatRequest;
import com.myfinance.service.ChatService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class AssistantController {

    private final ChatService chatService;

    public AssistantController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public String chat(@RequestBody ChatRequest request) {
        return chatService.chat(request.getMessage());
    }

}
