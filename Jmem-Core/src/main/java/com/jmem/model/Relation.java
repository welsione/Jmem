package com.jmem.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Graph relation representing an edge between two entities in a knowledge graph.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Relation {
    private String id;
    private String sourceId;
    private String targetId;
    private String type;
    private Map<String, Object> properties;
}
