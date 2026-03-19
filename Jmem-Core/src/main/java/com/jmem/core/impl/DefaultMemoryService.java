package com.jmem.core.impl;

import com.jmem.core.MemoryService;
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
 * Default implementation of MemoryService providing hybrid search
 * with RRF (Reciprocal Rank Fusion) algorithm for result fusion.
 */
public class DefaultMemoryService implements MemoryService {

    private static final int RRF_K = 60;

    private final VectorStore vectorStore;
    private final DocumentStore documentStore;
    private final Embedder embedder;

    public DefaultMemoryService(VectorStore vectorStore, DocumentStore documentStore, Embedder embedder) {
        this.vectorStore = vectorStore;
        this.documentStore = documentStore;
        this.embedder = embedder;
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

        // Vector search
        List<VectorSearchResult> vectorResults = vectorStore.search(queryEmbedding, limit * 2, null);

        // Document search
        List<Memory> documentResults = documentStore.search(query, limit * 2);

        // RRF fusion
        return fuseResults(vectorResults, documentResults, limit, scope);
    }

    @Override
    public List<Memory> getAll(MemoryScope scope, String scopeId) {
        if (scope == null) {
            // Return all memories
            List<Memory> all = new ArrayList<>();
            documentStore.findByScope(MemoryScope.USER.name(), null).forEach(all::add);
            documentStore.findByScope(MemoryScope.SESSION.name(), null).forEach(all::add);
            documentStore.findByScope(MemoryScope.AGENT.name(), null).forEach(all::add);
            return all;
        }
        return documentStore.findByScope(scope.name(), scopeId);
    }

    @Override
    public void update(Memory memory) {
        if (documentStore.findById(memory.getId()).isPresent()) {
            documentStore.update(memory);
            // Re-embed and update vector store
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
        // Note: Vector store reset would require tracking IDs by scope
    }

    @Override
    public String extractKnowledge(Memory memory) {
        // This is a placeholder - actual implementation would use LLM
        return "Extracted knowledge from: " + memory.getData();
    }

    /**
     * Fuses vector and document search results using RRF algorithm.
     * Score = Σ 1/(k+rank) where k=60
     */
    private List<Memory> fuseResults(List<VectorSearchResult> vectorResults,
                                     List<Memory> documentResults,
                                     int topK,
                                     MemoryScope scope) {
        Map<String, Double> rrfScores = new HashMap<>();
        Map<String, Memory> memoryMap = new HashMap<>();

        // Score vector results (rank starting at 1)
        for (int rank = 0; rank < vectorResults.size(); rank++) {
            VectorSearchResult result = vectorResults.get(rank);
            String id = result.getId();
            double score = 1.0 / (RRF_K + rank + 1);
            rrfScores.merge(id, score, Double::sum);

            Payload payload = result.getPayload();
            if (payload != null && payload.getMemoryId() != null) {
                documentStore.findById(payload.getMemoryId()).ifPresent(m -> memoryMap.put(id, m));
            }
        }

        // Score document results (rank starting at 1)
        for (int rank = 0; rank < documentResults.size(); rank++) {
            String id = documentResults.get(rank).getId();
            double score = 1.0 / (RRF_K + rank + 1);
            rrfScores.merge(id, score, Double::sum);
            memoryMap.put(id, documentResults.get(rank));
        }

        // Sort by RRF score descending and take top K
        return rrfScores.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(topK)
                .map(entry -> memoryMap.get(entry.getKey()))
                .filter(Objects::nonNull)
                .filter(m -> scope == null || m.getScope() == scope)
                .collect(Collectors.toList());
    }
}
