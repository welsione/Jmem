package com.jmem.config;

import com.jmem.core.MemoryService;
import com.jmem.core.impl.DefaultMemoryService;
import com.jmem.embedder.Embedder;
import com.jmem.embedder.impl.SimpleEmbedder;
import com.jmem.storage.DocumentStore;
import com.jmem.storage.VectorStore;
import com.jmem.storage.impl.InMemoryDocumentStore;
import com.jmem.storage.impl.InMemoryVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreConfig {

    @Bean
    public VectorStore vectorStore() {
        return new InMemoryVectorStore();
    }

    @Bean
    public DocumentStore documentStore() {
        return new InMemoryDocumentStore();
    }

    @Bean
    public Embedder embedder() {
        return new SimpleEmbedder();
    }

    @Bean
    public MemoryService memoryService(VectorStore vectorStore, DocumentStore documentStore, Embedder embedder) {
        return new DefaultMemoryService(vectorStore, documentStore, embedder);
    }
}
