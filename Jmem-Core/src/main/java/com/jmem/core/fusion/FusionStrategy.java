package com.jmem.core.fusion;

import com.jmem.model.Memory;

import java.util.List;

/**
 * 结果融合策略接口。
 */
public interface FusionStrategy {

    /**
     * 融合多个搜索结果。
     *
     * @param resultLists 结果列表
     * @param topK        返回结果数量
     * @param scope       记忆范围过滤
     * @return 融合后的记忆列表
     */
    List<Memory> fuse(List<? extends List<? extends ScoredResult>> resultLists, int topK, com.jmem.model.MemoryScope scope);

    /**
     * 带分数的搜索结果接口。
     */
    interface ScoredResult {
        String getId();
        double getScore();
        Memory getMemory();
    }
}
