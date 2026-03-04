package com.myfinance.controller;

import com.myfinance.ai.FinanceAssistant;
import com.myfinance.controller.dto.ChatRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class AssistantController {

    private final FinanceAssistant assistant;

    public AssistantController(FinanceAssistant assistant) {
        this.assistant = assistant;
    }

    @PostMapping
    public String chat(@RequestBody ChatRequest request) {
        return assistant.chat(request.getMessage());
    }

}
