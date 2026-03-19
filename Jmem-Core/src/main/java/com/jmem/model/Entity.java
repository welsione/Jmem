package com.jmem.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 图实体，表示知识图谱中的一个节点。
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
