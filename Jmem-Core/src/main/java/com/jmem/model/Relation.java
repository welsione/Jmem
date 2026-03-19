package com.jmem.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 图关系，表示知识图谱中两个实体之间的边。
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
