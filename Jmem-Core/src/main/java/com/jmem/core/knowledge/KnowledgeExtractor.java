package com.jmem.core.knowledge;

import com.jmem.model.Memory;

import java.util.List;

/**
 * 知识提取器接口。
 * 用于从记忆内容中提取结构化知识。
 */
public interface KnowledgeExtractor {

    /**
     * 从单条记忆内容中提取知识。
     *
     * @param memory 记忆
     * @return 提取的知识
     */
    String extract(Memory memory);

    /**
     * 从多条记忆中提取汇总知识。
     *
     * @param memories 记忆列表
     * @return 汇总知识
     */
    String summarize(List<Memory> memories);

    /**
     * 判断是否支持此提取器。
     *
     * @return true if supported
     */
    default boolean isSupported() {
        return true;
    }
}
