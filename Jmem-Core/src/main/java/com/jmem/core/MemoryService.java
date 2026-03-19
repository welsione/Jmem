package com.jmem.core;

import com.jmem.model.Memory;
import com.jmem.model.MemoryScope;

import java.util.List;

/**
 * Core memory service interface providing unified memory operations.
 */
public interface MemoryService {

    /**
     * Add a new memory.
     *
     * @param memory the memory to add
     * @return the ID of the added memory
     */
    String add(Memory memory);

    /**
     * Search memories by text query.
     *
     * @param query  the search query
     * @param scope  the memory scope to search within
     * @param limit  maximum number of results
     * @return list of matching memories
     */
    List<Memory> search(String query, MemoryScope scope, int limit);

    /**
     * Perform hybrid search combining vector and graph search.
     *
     * @param query  the search query
     * @param scope  the memory scope
     * @param limit  maximum number of results
     * @return list of matching memories
     */
    List<Memory> hybridSearch(String query, MemoryScope scope, int limit);

    /**
     * Get all memories within a scope.
     *
     * @param scope   the memory scope
     * @param scopeId the scope ID (optional based on scope type)
     * @return list of memories
     */
    List<Memory> getAll(MemoryScope scope, String scopeId);

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
     * Reset (delete all) memories within a scope.
     *
     * @param scope   the memory scope
     * @param scopeId the scope ID (optional based on scope type)
     */
    void reset(MemoryScope scope, String scopeId);

    /**
     * Extract structured knowledge from a memory using LLM.
     *
     * @param memory the memory to extract knowledge from
     * @return extracted knowledge
     */
    String extractKnowledge(Memory memory);
}
