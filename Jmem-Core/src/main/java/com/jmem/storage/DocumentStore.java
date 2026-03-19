package com.jmem.storage;

import com.jmem.model.Memory;

import java.util.List;
import java.util.Optional;

/**
 * 记忆文档存储接口，用于记忆文档的存储和检索。
 */
public interface DocumentStore {

    /**
     * 保存记忆文档。
     *
     * @param memory 要存储的记忆
     */
    void save(Memory memory);

    /**
     * 批量保存多个记忆文档。
     *
     * @param memories 记忆列表
     */
    void saveBatch(List<Memory> memories);

    /**
     * 根据 ID 查找记忆。
     *
     * @param id 记忆 ID
     * @return 找到的记忆
     */
    Optional<Memory> findById(String id);

    /**
     * 根据范围查找所有记忆。
     *
     * @param scope   记忆范围
     * @param scopeId 范围 ID
     * @return 记忆列表
     */
    List<Memory> findByScope(String scope, String scopeId);

    /**
     * 按内容查询搜索记忆。
     *
     * @param query 搜索查询
     * @param limit 最大结果数
     * @return 匹配的记忆列表
     */
    List<Memory> search(String query, int limit);

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
     * 按范围删除所有记忆。
     *
     * @param scope   记忆范围
     * @param scopeId 范围 ID
     */
    void deleteByScope(String scope, String scopeId);

    /**
     * 删除所有记忆。
     */
    void deleteAll();
}
