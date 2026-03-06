package com.myfinance.ai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface FinanceAssistant {

    String chat(@MemoryId Long memoryId, @UserMessage String userMessage);

}
