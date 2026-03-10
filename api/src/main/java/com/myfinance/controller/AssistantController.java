package com.myfinance.controller;

import com.myfinance.controller.dto.ChatRequest;
import com.myfinance.service.ChatService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:4200")
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
