package com.jmem;

import com.jmem.config.JmemProperties;
import com.jmem.core.MemoryService;
import com.jmem.core.impl.DefaultMemoryService;
import com.jmem.embedder.Embedder;
import com.jmem.embedder.impl.SiliconFlowEmbedder;
import com.jmem.embedder.impl.SimpleEmbedder;
import com.jmem.storage.DocumentStore;
import com.jmem.storage.VectorStore;
import com.jmem.storage.impl.InMemoryDocumentStore;
import com.jmem.storage.impl.InMemoryVectorStore;
import com.jmem.storage.impl.QdrantVectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JmemProperties.class)
@ConditionalOnProperty(prefix = "jmem", name = "enabled", havingValue = "true", matchIfMissing = false)
public class JmemAutoConfiguration {

    private final JmemProperties properties;

    public JmemAutoConfiguration(JmemProperties properties) {
        this.properties = properties;
    }

    @Bean
    public DocumentStore documentStore() {
        return new InMemoryDocumentStore();
    }

    @Bean
    public VectorStore vectorStore() {
        if ("qdrant".equalsIgnoreCase(properties.getVectorStore().getType())) {
            return new QdrantVectorStore(properties.getVectorStore());
        }
        return new InMemoryVectorStore();
    }

    @Bean
    public Embedder embedder() {
        String apiKey = properties.getEmbedder().getApiKey();
        // Use SiliconFlow if API key is available, otherwise fallback to SimpleEmbedder
        if (apiKey != null && !apiKey.isBlank()) {
            return new SiliconFlowEmbedder(properties.getEmbedder());
        }

        return new SimpleEmbedder();
    }

    @Bean
    public MemoryService memoryService(VectorStore vectorStore, DocumentStore documentStore, Embedder embedder) {
        return new DefaultMemoryService(vectorStore, documentStore, embedder);
    }
}
