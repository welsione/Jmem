package com.jmem.storage;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Vector storage interface for embedding-based storage and retrieval.
 */
public interface VectorStore {

    /**
     * Upsert a single vector entry.
     *
     * @param id      unique identifier
     * @param vector  the vector data
     * @param payload additional metadata
     */
    void upsert(String id, float[] vector, Map<String, Object> payload);

    /**
     * Upsert multiple vector entries in batch.
     *
     * @param entries list of vector entries
     */
    void upsertBatch(List<VectorEntry> entries);

    /**
     * Search for similar vectors.
     *
     * @param queryVector the query vector
     * @param topK        number of results to return
     * @param filter      optional metadata filter
     * @return list of search results
     */
    List<VectorSearchResult> search(float[] queryVector, int topK, Map<String, Object> filter);

    /**
     * Get vector by ID.
     *
     * @param id the vector ID
     * @return the vector if found
     */
    Optional<float[]> getVector(String id);

    /**
     * Delete a vector by ID.
     *
     * @param id the vector ID
     */
    void delete(String id);

    /**
     * Delete all vectors in the collection.
     */
    void deleteCollection();

    /**
     * Check if a vector exists.
     *
     * @param id the vector ID
     * @return true if exists
     */
    boolean exists(String id);

    /**
     * Represents a vector entry with metadata.
     */
    class VectorEntry {
        private final String id;
        private final float[] vector;
        private final Map<String, Object> payload;

        public VectorEntry(String id, float[] vector, Map<String, Object> payload) {
            this.id = id;
            this.vector = vector;
            this.payload = payload;
        }

        public String getId() {
            return id;
        }

        public float[] getVector() {
            return vector;
        }

        public Map<String, Object> getPayload() {
            return payload;
        }
    }

    /**
     * Represents a vector search result.
     */
    class VectorSearchResult {
        private final String id;
        private final float[] vector;
        private final Map<String, Object> payload;
        private final float score;

        public VectorSearchResult(String id, float[] vector, Map<String, Object> payload, float score) {
            this.id = id;
            this.vector = vector;
            this.payload = payload;
            this.score = score;
        }

        public String getId() {
            return id;
        }

        public float[] getVector() {
            return vector;
        }

        public Map<String, Object> getPayload() {
            return payload;
        }

        public float getScore() {
            return score;
        }
    }
}
