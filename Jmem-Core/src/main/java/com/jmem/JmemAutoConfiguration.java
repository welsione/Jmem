package com.jmem;

import com.jmem.config.JmemProperties;
import com.jmem.core.MemoryService;
import com.jmem.core.fusion.FusionStrategy;
import com.jmem.core.fusion.ReciprocalRankFusionStrategy;
import com.jmem.core.impl.DefaultMemoryService;
import com.jmem.core.knowledge.KnowledgeExtractor;
import com.jmem.core.knowledge.LLMKnowledgeExtractor;
import com.jmem.core.knowledge.RuleBasedKnowledgeExtractor;
import com.jmem.embedder.Embedder;
import com.jmem.embedder.impl.SiliconFlowEmbedder;
import com.jmem.embedder.impl.SimpleEmbedder;
import com.jmem.llm.LLM;
import com.jmem.llm.impl.SiliconFlowLLM;
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
        // 如果 API 密钥可用则使用 SiliconFlow，否则回退到 SimpleEmbedder
        if (apiKey != null && !apiKey.isBlank()) {
            return new SiliconFlowEmbedder(properties.getEmbedder());
        }

        return new SimpleEmbedder();
    }

    @Bean
    public LLM llm() {
        String llmType = properties.getLlm().getType();
        if ("siliconflow".equalsIgnoreCase(llmType)) {
            String apiKey = properties.getLlm().getApiKey();
            if (apiKey != null && !apiKey.isBlank()) {
                return new SiliconFlowLLM(properties.getLlm());
            }
        }
        // 默认返回 null，使用 RuleBasedKnowledgeExtractor
        return null;
    }

    @Bean
    public FusionStrategy fusionStrategy() {
        return new ReciprocalRankFusionStrategy();
    }

    @Bean
    public KnowledgeExtractor knowledgeExtractor(LLM llm) {
        if (llm != null) {
            return new LLMKnowledgeExtractor(llm);
        }
        return new RuleBasedKnowledgeExtractor();
    }

    @Bean
    public MemoryService memoryService(VectorStore vectorStore, DocumentStore documentStore,
                                       Embedder embedder, FusionStrategy fusionStrategy,
                                       KnowledgeExtractor knowledgeExtractor) {
        return new DefaultMemoryService(vectorStore, documentStore, embedder, fusionStrategy, knowledgeExtractor);
    }
}
