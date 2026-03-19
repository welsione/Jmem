package com.jmem.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * Memory entity representing a stored piece of memory with associated metadata.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Memory {
    private String id;
    private Object data;
    private String userId;
    private String sessionId;
    private String agentId;
    private MemoryScope scope;
    private Map<String, Object> metadata;
    private float[] embedding;
    private Instant createdAt;
    private Instant updatedAt;
}
