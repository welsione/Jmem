package com.jmem.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Search result containing a memory and its relevance score.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult {
    private String id;
    private double score;
    private Memory memory;
    private String explanation;
}
