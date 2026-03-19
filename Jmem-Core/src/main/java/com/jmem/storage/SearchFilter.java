package com.jmem.storage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Filter for vector search operations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchFilter {

    private String scope;
    private String userId;
    private String sessionId;
    private String agentId;
    private String content;  // for text matching
}
