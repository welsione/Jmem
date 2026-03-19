package com.jmem.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * 记忆实体，表示存储的记忆片段及相关元数据。
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
