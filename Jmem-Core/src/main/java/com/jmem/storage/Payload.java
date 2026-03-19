package com.jmem.storage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 向量存储操作的负载数据。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payload {

    private String memoryId;
    private String userId;
    private String sessionId;
    private String agentId;
    private String scope;
    private String content;
    private String metadata;
}
