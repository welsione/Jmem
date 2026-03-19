package com.jmem.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Vector search result containing an ID, similarity score, and payload.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VectorSearchResult {
    private String id;
    private double score;
    private Map<String, Object> payload;
}
