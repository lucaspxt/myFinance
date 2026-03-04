package com.myfinance.ai;

import dev.langchain4j.service.spring.AiService;

@AiService
public interface FinanceAssistant {

    String chat(String userMessage);

}
