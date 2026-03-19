package com.jmem.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Graph entity representing a node in a knowledge graph.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Entity {
    private String id;
    private String type;
    private Map<String, Object> properties;
    private float[] embedding;
}
