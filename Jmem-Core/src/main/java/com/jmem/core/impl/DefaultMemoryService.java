package com.jmem.core.impl;

import com.jmem.core.MemoryService;
import com.jmem.core.fusion.FusionStrategy;
import com.jmem.core.fusion.ReciprocalRankFusionStrategy;
import com.jmem.core.knowledge.KnowledgeExtractor;
import com.jmem.core.knowledge.RuleBasedKnowledgeExtractor;
import com.jmem.embedder.Embedder;
import com.jmem.model.Memory;
import com.jmem.model.MemoryScope;
import com.jmem.storage.Payload;
import com.jmem.storage.SearchFilter;
import com.jmem.storage.VectorStore.VectorSearchResult;
import com.jmem.storage.DocumentStore;
import com.jmem.storage.VectorStore;

import java.util.*;
import java.util.stream.Collectors;

/**
 * MemoryService 的默认实现，提供混合搜索功能，
 * 使用可配置的结果融合策略和知识提取器。
 */
public class DefaultMemoryService implements MemoryService {

    private final VectorStore vectorStore;
    private final DocumentStore documentStore;
    private final Embedder embedder;
    private final FusionStrategy fusionStrategy;
    private final KnowledgeExtractor knowledgeExtractor;

    public DefaultMemoryService(VectorStore vectorStore, DocumentStore documentStore, Embedder embedder) {
        this(vectorStore, documentStore, embedder, new ReciprocalRankFusionStrategy(), new RuleBasedKnowledgeExtractor());
    }

    public DefaultMemoryService(VectorStore vectorStore, DocumentStore documentStore, Embedder embedder,
                                FusionStrategy fusionStrategy) {
        this(vectorStore, documentStore, embedder, fusionStrategy, new RuleBasedKnowledgeExtractor());
    }

    public DefaultMemoryService(VectorStore vectorStore, DocumentStore documentStore, Embedder embedder,
                                FusionStrategy fusionStrategy, KnowledgeExtractor knowledgeExtractor) {
        this.vectorStore = vectorStore;
        this.documentStore = documentStore;
        this.embedder = embedder;
        this.fusionStrategy = fusionStrategy;
        this.knowledgeExtractor = knowledgeExtractor;
    }

    @Override
    public String add(Memory memory) {
        String id = memory.getId();
        if (id == null) {
            id = UUID.randomUUID().toString();
        }

        float[] embedding = embedder.embed(memory.getData().toString());

        Payload payload = Payload.builder()
                .memoryId(id)
                .userId(memory.getUserId())
                .sessionId(memory.getSessionId())
                .agentId(memory.getAgentId())
                .scope(memory.getScope() != null ? memory.getScope().name() : null)
                .content(memory.getData() != null ? memory.getData().toString() : null)
                .build();

        vectorStore.upsert(id, embedding, payload);
        documentStore.save(memory);

        return id;
    }

    @Override
    public List<Memory> search(String query, MemoryScope scope, int limit) {
        float[] queryEmbedding = embedder.embed(query);

        SearchFilter filter = null;
        if (scope != null) {
            filter = SearchFilter.builder().scope(scope.name()).build();
        }

        List<VectorSearchResult> vectorResults = vectorStore.search(queryEmbedding, limit * 2, filter);

        return vectorResults.stream()
                .map(result -> {
                    Payload payload = result.getPayload();
                    if (payload != null && payload.getMemoryId() != null) {
                        return documentStore.findById(payload.getMemoryId()).orElse(null);
                    }
                    return documentStore.findById(result.getId()).orElse(null);
                })
                .filter(Objects::nonNull)
                .filter(m -> scope == null || m.getScope() == scope)
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<Memory> hybridSearch(String query, MemoryScope scope, int limit) {
        float[] queryEmbedding = embedder.embed(query);

        // 向量搜索
        List<VectorSearchResult> vectorResults = vectorStore.search(queryEmbedding, limit * 2, null);

        // 文档搜索
        List<Memory> documentResults = documentStore.search(query, limit * 2);

        // 转换为 ScoredResult 并融合
        List<FusionStrategy.ScoredResult> vectorScoredResults = vectorResults.stream()
                .map(this::toScoredResult)
                .toList();

        List<FusionStrategy.ScoredResult> docScoredResults = documentResults.stream()
                .map(this::toScoredResult)
                .toList();

        return fusionStrategy.fuse(List.of(vectorScoredResults, docScoredResults), limit, scope);
    }

    private FusionStrategy.ScoredResult toScoredResult(VectorSearchResult result) {
        return new FusionStrategy.ScoredResult() {
            @Override
            public String getId() {
                return result.getId();
            }

            @Override
            public double getScore() {
                return result.getScore();
            }

            @Override
            public Memory getMemory() {
                Payload payload = result.getPayload();
                if (payload != null && payload.getMemoryId() != null) {
                    return documentStore.findById(payload.getMemoryId()).orElse(null);
                }
                return documentStore.findById(result.getId()).orElse(null);
            }
        };
    }

    private FusionStrategy.ScoredResult toScoredResult(Memory memory) {
        return new FusionStrategy.ScoredResult() {
            @Override
            public String getId() {
                return memory.getId();
            }

            @Override
            public double getScore() {
                return 0.0;
            }

            @Override
            public Memory getMemory() {
                return memory;
            }
        };
    }

    @Override
    public List<Memory> getAll(MemoryScope scope, String scopeId) {
        if (scope == null) {
            // 返回所有记忆
            List<Memory> all = new ArrayList<>();
            all.addAll(documentStore.findByScope(MemoryScope.USER.name(), null));
            all.addAll(documentStore.findByScope(MemoryScope.SESSION.name(), null));
            all.addAll(documentStore.findByScope(MemoryScope.AGENT.name(), null));
            return all;
        }
        return documentStore.findByScope(scope.name(), scopeId);
    }

    @Override
    public void update(Memory memory) {
        if (documentStore.findById(memory.getId()).isPresent()) {
            documentStore.update(memory);
            // 重新向量化并更新向量存储
            float[] embedding = embedder.embed(memory.getData().toString());

            Payload payload = Payload.builder()
                    .memoryId(memory.getId())
                    .userId(memory.getUserId())
                    .sessionId(memory.getSessionId())
                    .agentId(memory.getAgentId())
                    .scope(memory.getScope() != null ? memory.getScope().name() : null)
                    .content(memory.getData() != null ? memory.getData().toString() : null)
                    .build();

            vectorStore.upsert(memory.getId(), embedding, payload);
        }
    }

    @Override
    public void delete(String id) {
        vectorStore.delete(id);
        documentStore.delete(id);
    }

    @Override
    public void reset(MemoryScope scope, String scopeId) {
        documentStore.deleteByScope(scope.name(), scopeId);
        // 注意: 向量存储重置需要按范围跟踪 ID
    }

    @Override
    public String extractKnowledge(Memory memory) {
        if (knowledgeExtractor != null && knowledgeExtractor.isSupported()) {
            return knowledgeExtractor.extract(memory);
        }
        return "知识提取器不可用";
    }

    /**
     * 获取知识提取器。
     *
     * @return 知识提取器实例
     */
    public KnowledgeExtractor getKnowledgeExtractor() {
        return knowledgeExtractor;
    }
}
