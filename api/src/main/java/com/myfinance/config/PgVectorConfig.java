package com.myfinance.config;

import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PgVectorConfig {

    @Value("${langchain4j.pgvector.embedding-store.host:localhost}")
    private String host;

    @Value("${langchain4j.pgvector.embedding-store.port:5432}")
    private int port;

    @Value("${langchain4j.pgvector.embedding-store.database:myfinance}")
    private String database;

    @Value("${langchain4j.pgvector.embedding-store.user:postgres}")
    private String user;

    @Value("${langchain4j.pgvector.embedding-store.password:postgres}")
    private String password;

    @Value("${langchain4j.pgvector.embedding-store.table:embeddings}")
    private String table;

    @Value("${langchain4j.pgvector.embedding-store.dimension:1536}")
    private int dimension;

    @Value("${langchain4j.pgvector.embedding-store.create-table:true}")
    private boolean createTable;

    @Value("${langchain4j.pgvector.embedding-store.drop-table-first:false}")
    private boolean dropTableFirst;

    @Bean
    public PgVectorEmbeddingStore pgVectorEmbeddingStore() {
        return PgVectorEmbeddingStore.builder()
                .host(host)
                .port(port)
                .database(database)
                .user(user)
                .password(password)
                .table(table)
                .dimension(dimension)
                .createTable(createTable)
                .dropTableFirst(dropTableFirst)
                .build();
    }

}
