package com.jmem.storage;

import java.util.List;
import java.util.Optional;

/**
 * 向量存储接口，用于基于向量的存储和检索。
 */
public interface VectorStore {

    /**
     * 插入或更新单个向量。
     *
     * @param id      唯一标识符
     * @param vector  向量数据
     * @param payload 附加元数据
     */
    void upsert(String id, float[] vector, Payload payload);

    /**
     * 批量插入或更新多个向量。
     *
     * @param entries 向量条目列表
     */
    void upsertBatch(List<VectorEntry> entries);

    /**
     * 搜索相似向量。
     *
     * @param queryVector 查询向量
     * @param topK       返回结果数量
     * @param filter      可选的元数据过滤器
     * @return 搜索结果列表
     */
    List<VectorSearchResult> search(float[] queryVector, int topK, SearchFilter filter);

    /**
     * 根据 ID 获取向量。
     *
     * @param id 向量 ID
     * @return 找到的向量
     */
    Optional<float[]> getVector(String id);

    /**
     * 根据 ID 删除向量。
     *
     * @param id 向量 ID
     */
    void delete(String id);

    /**
     * 删除集合中的所有向量。
     */
    void deleteCollection();

    /**
     * 检查向量是否存在。
     *
     * @param id 向量 ID
     * @return 是否存在
     */
    boolean exists(String id);

    /**
     * 带元数据的向量条目。
     */
    class VectorEntry {
        private final String id;
        private final float[] vector;
        private final Payload payload;

        public VectorEntry(String id, float[] vector, Payload payload) {
            this.id = id;
            this.vector = vector;
            this.payload = payload;
        }

        public String getId() { return id; }
        public float[] getVector() { return vector; }
        public Payload getPayload() { return payload; }
    }

    /**
     * 向量搜索结果。
     */
    class VectorSearchResult {
        private final String id;
        private final float[] vector;
        private final Payload payload;
        private final float score;

        public VectorSearchResult(String id, float[] vector, Payload payload, float score) {
            this.id = id;
            this.vector = vector;
            this.payload = payload;
            this.score = score;
        }

        public String getId() { return id; }
        public float[] getVector() { return vector; }
        public Payload getPayload() { return payload; }
        public float getScore() { return score; }
    }
}
