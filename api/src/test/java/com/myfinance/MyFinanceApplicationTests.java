package com.myfinance;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false",
        "langchain4j.open-ai.chat-model.api-key=test-key",
        "langchain4j.open-ai.embedding-model.api-key=test-key"
})
class MyFinanceApplicationTests {

    @MockBean
    private PgVectorEmbeddingStore pgVectorEmbeddingStore;

    @MockBean
    private ChatModel chatModel;

    @Test
    void contextLoads() {
    }

}
