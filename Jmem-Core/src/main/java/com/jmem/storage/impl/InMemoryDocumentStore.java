package com.jmem.storage.impl;

import com.jmem.model.Memory;
import com.jmem.model.MemoryScope;
import com.jmem.storage.DocumentStore;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 基于 ConcurrentHashMap 的内存文档存储实现。
 */
public class InMemoryDocumentStore implements DocumentStore {

    private final ConcurrentHashMap<String, Memory> documents;

    public InMemoryDocumentStore() {
        this.documents = new ConcurrentHashMap<>();
    }

    @Override
    public void save(Memory memory) {
        documents.put(memory.getId(), memory);
    }

    @Override
    public void saveBatch(List<Memory> memories) {
        for (Memory memory : memories) {
            save(memory);
        }
    }

    @Override
    public Optional<Memory> findById(String id) {
        return Optional.ofNullable(documents.get(id));
    }

    @Override
    public List<Memory> findByScope(String scope, String scopeId) {
        MemoryScope memoryScope = MemoryScope.valueOf(scope);
        return documents.values().stream()
                .filter(m -> m.getScope() == memoryScope)
                .filter(m -> {
                    if (scopeId == null) return true;
                    return switch (memoryScope) {
                        case USER -> Objects.equals(m.getUserId(), scopeId);
                        case SESSION -> Objects.equals(m.getSessionId(), scopeId);
                        case AGENT -> Objects.equals(m.getAgentId(), scopeId);
                    };
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Memory> search(String query, int limit) {
        String lowerQuery = query.toLowerCase();
        return documents.values().stream()
                .filter(m -> matchesQuery(m, lowerQuery))
                .sorted((a, b) -> {
                    long aTime = a.getCreatedAt() != null ? a.getCreatedAt().toEpochMilli() : 0;
                    long bTime = b.getCreatedAt() != null ? b.getCreatedAt().toEpochMilli() : 0;
                    return Long.compare(bTime, aTime);
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public void update(Memory memory) {
        if (documents.containsKey(memory.getId())) {
            documents.put(memory.getId(), memory);
        }
    }

    @Override
    public void delete(String id) {
        documents.remove(id);
    }

    @Override
    public void deleteByScope(String scope, String scopeId) {
        List<String> toDelete = findByScope(scope, scopeId).stream()
                .map(Memory::getId)
                .collect(Collectors.toList());
        toDelete.forEach(documents::remove);
    }

    @Override
    public void deleteAll() {
        documents.clear();
    }

    private boolean matchesQuery(Memory memory, String query) {
        if (memory.getData() == null) return false;
        String content = memory.getData().toString().toLowerCase();
        String[] queryTerms = query.split("\\s+");
        for (String term : queryTerms) {
            if (content.contains(term)) {
                return true;
            }
        }
        return false;
    }
}
