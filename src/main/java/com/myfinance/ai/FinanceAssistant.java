package com.myfinance.ai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface FinanceAssistant {

    String chat(@MemoryId Long memoryId, @UserMessage String userMessage);

    @SystemMessage("""
            Analise a mensagem e extraia informações de uma transação financeira, se houver.
            - type: CREDIT (entrada/receita) ou DEBIT (saída/despesa)
            - categoryName: nome da categoria da transação
            - bankAccountName: nome da conta bancária
            - value: valor numérico da transação
            Retorne null em cada campo que não puder ser determinado com clareza.
            """)
    TransactionExtractionResult extractTransaction(@MemoryId Long memoryId, @UserMessage String userMessage);

}
