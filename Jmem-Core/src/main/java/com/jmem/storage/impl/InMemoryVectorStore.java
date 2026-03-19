package com.jmem.storage.impl;

import com.jmem.model.VectorSearchResult;
import com.jmem.storage.VectorStore;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory vector store implementation using ConcurrentHashMap.
 * Provides cosine similarity-based search functionality.
 */
public class InMemoryVectorStore implements VectorStore {

    private final ConcurrentHashMap<String, float[]> vectors;
    private final ConcurrentHashMap<String, Map<String, Object>> payloads;

    public InMemoryVectorStore() {
        this.vectors = new ConcurrentHashMap<>();
        this.payloads = new ConcurrentHashMap<>();
    }

    @Override
    public void upsert(String id, float[] vector, Map<String, Object> payload) {
        vectors.put(id, vector);
        payloads.put(id, payload);
    }

    @Override
    public void upsertBatch(List<VectorEntry> entries) {
        for (VectorEntry entry : entries) {
            upsert(entry.getId(), entry.getVector(), entry.getPayload());
        }
    }

    @Override
    public List<VectorSearchResult> search(float[] queryVector, int topK, Map<String, Object> filter) {
        return vectors.entrySet().stream()
                .map(entry -> new VectorSearchResult(
                        entry.getKey(),
                        entry.getValue(),
                        payloads.get(entry.getKey()),
                        (float) cosineSimilarity(queryVector, entry.getValue())))
                .sorted((a, b) -> Float.compare(b.getScore(), a.getScore()))
                .limit(topK)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<float[]> getVector(String id) {
        return Optional.ofNullable(vectors.get(id));
    }

    @Override
    public void delete(String id) {
        vectors.remove(id);
        payloads.remove(id);
    }

    @Override
    public void deleteCollection() {
        vectors.clear();
        payloads.clear();
    }

    @Override
    public boolean exists(String id) {
        return vectors.containsKey(id);
    }

    /**
     * Calculates cosine similarity between two vectors.
     *
     * @param vec1 the first vector
     * @param vec2 the second vector
     * @return the cosine similarity score between -1 and 1
     */
    private double cosineSimilarity(float[] vec1, float[] vec2) {
        if (vec1.length != vec2.length) {
            throw new IllegalArgumentException("Vectors must have the same dimension");
        }

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int i = 0; i < vec1.length; i++) {
            dotProduct += vec1[i] * vec2[i];
            norm1 += vec1[i] * vec1[i];
            norm2 += vec2[i] * vec2[i];
        }

        double denominator = Math.sqrt(norm1) * Math.sqrt(norm2);
        if (denominator == 0.0) {
            return 0.0;
        }

        return dotProduct / denominator;
    }
}
