package com.jmem.core;

import com.jmem.model.Memory;
import com.jmem.model.MemoryScope;

import java.util.List;

/**
 * 提供统一内存操作的核心内存服务接口。
 */
public interface MemoryService {

    /**
     * 添加新记忆。
     *
     * @param memory 要添加的记忆
     * @return 添加的记忆 ID
     */
    String add(Memory memory);

    /**
     * 按文本查询搜索记忆。
     *
     * @param query  搜索查询
     * @param scope  要搜索的记忆范围
     * @param limit  最大结果数
     * @return 匹配的记忆列表
     */
    List<Memory> search(String query, MemoryScope scope, int limit);

    /**
     * 执行结合向量和图搜索的混合搜索。
     *
     * @param query  搜索查询
     * @param scope  记忆范围
     * @param limit  最大结果数
     * @return 匹配的记忆列表
     */
    List<Memory> hybridSearch(String query, MemoryScope scope, int limit);

    /**
     * 获取范围内所有记忆。
     *
     * @param scope   记忆范围
     * @param scopeId 范围 ID（根据范围类型可选）
     * @return 记忆列表
     */
    List<Memory> getAll(MemoryScope scope, String scopeId);

    /**
     * 更新现有记忆。
     *
     * @param memory 要更新的记忆
     */
    void update(Memory memory);

    /**
     * 根据 ID 删除记忆。
     *
     * @param id 记忆 ID
     */
    void delete(String id);

    /**
     * 重置（删除）范围内所有记忆。
     *
     * @param scope   记忆范围
     * @param scopeId 范围 ID（根据范围类型可选）
     */
    void reset(MemoryScope scope, String scopeId);

    /**
     * 使用 LLM 从记忆中提取结构化知识。
     *
     * @param memory 要提取知识的记忆
     * @return 提取的知识
     */
    String extractKnowledge(Memory memory);
}
