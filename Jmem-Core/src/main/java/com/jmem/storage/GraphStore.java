package com.jmem.storage;

import com.jmem.model.Entity;
import com.jmem.model.Relation;

import java.util.List;
import java.util.Optional;

/**
 * 图存储接口，用于实体-关系存储。
 */
public interface GraphStore {

    /**
     * 插入或更新单个实体。
     *
     * @param entity 要插入的实体
     */
    void upsertEntity(Entity entity);

    /**
     * 根据 ID 获取实体。
     *
     * @param id 实体 ID
     * @return 找到的实体
     */
    Optional<Entity> getEntity(String id);

    /**
     * 获取特定类型的所有实体。
     *
     * @param type 实体类型
     * @return 实体列表
     */
    List<Entity> getEntitiesByType(String type);

    /**
     * 根据 ID 删除实体。
     *
     * @param id 实体 ID
     */
    void deleteEntity(String id);

    /**
     * 插入或更新单个关系。
     *
     * @param relation 要插入的关系
     */
    void upsertRelation(Relation relation);

    /**
     * 根据 ID 获取关系。
     *
     * @param id 关系 ID
     * @return 找到的关系
     */
    Optional<Relation> getRelation(String id);

    /**
     * 获取实体的所有关系。
     *
     * @param entityId 实体 ID
     * @return 关系列表
     */
    List<Relation> getRelations(String entityId);

    /**
     * 获取通过特定关系类型连接的邻居实体。
     *
     * @param entityId     源实体 ID
     * @param relationType 关系类型
     * @return 邻居实体列表
     */
    List<Entity> getNeighborEntities(String entityId, String relationType);

    /**
     * 根据 ID 删除关系。
     *
     * @param id 关系 ID
     */
    void deleteRelation(String id);

    /**
     * 按查询字符串搜索实体。
     *
     * @param query 搜索查询
     * @param topK  返回结果数量
     * @return 匹配的实体列表
     */
    List<Entity> searchEntities(String query, int topK);

    /**
     * 查找两个实体之间的路径。
     *
     * @param sourceId 源实体 ID
     * @param targetId 目标实体 ID
     * @param maxDepth 最大路径深度
     * @return 找到的路径列表
     */
    List<Path> findPaths(String sourceId, String targetId, int maxDepth);

    /**
     * 批量插入或更新多个实体。
     *
     * @param entities 实体列表
     */
    void upsertEntityBatch(List<Entity> entities);

    /**
     * 批量插入或更新多个关系。
     *
     * @param relations 关系列表
     */
    void upsertRelationBatch(List<Relation> relations);

    /**
     * 图中实体之间的路径。
     */
    class Path {
        private final List<Entity> entities;
        private final List<Relation> relations;

        public Path(List<Entity> entities, List<Relation> relations) {
            this.entities = entities;
            this.relations = relations;
        }

        public List<Entity> getEntities() {
            return entities;
        }

        public List<Relation> getRelations() {
            return relations;
        }
    }
}
