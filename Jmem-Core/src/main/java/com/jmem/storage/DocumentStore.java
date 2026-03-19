package com.jmem.storage;

import com.jmem.model.Memory;

import java.util.List;
import java.util.Optional;

/**
 * Document storage interface for memory document storage and retrieval.
 */
public interface DocumentStore {

    /**
     * Save a memory document.
     *
     * @param memory the memory to store
     */
    void save(Memory memory);

    /**
     * Save multiple memory documents in batch.
     *
     * @param memories list of memories
     */
    void saveBatch(List<Memory> memories);

    /**
     * Find memory by ID.
     *
     * @param id the memory ID
     * @return the memory if found
     */
    Optional<Memory> findById(String id);

    /**
     * Find all memories by scope.
     *
     * @param scope   the memory scope
     * @param scopeId the scope ID
     * @return list of memories
     */
    List<Memory> findByScope(String scope, String scopeId);

    /**
     * Search memories by content query.
     *
     * @param query the search query
     * @param limit maximum number of results
     * @return list of matching memories
     */
    List<Memory> search(String query, int limit);

    /**
     * Update an existing memory.
     *
     * @param memory the memory to update
     */
    void update(Memory memory);

    /**
     * Delete a memory by ID.
     *
     * @param id the memory ID
     */
    void delete(String id);

    /**
     * Delete all memories by scope.
     *
     * @param scope   the memory scope
     * @param scopeId the scope ID
     */
    void deleteByScope(String scope, String scopeId);

    /**
     * Delete all memories.
     */
    void deleteAll();
}
