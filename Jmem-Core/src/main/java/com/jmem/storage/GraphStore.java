package com.jmem.storage;

import com.jmem.model.Entity;
import com.jmem.model.Relation;

import java.util.List;
import java.util.Optional;

/**
 * Graph storage interface for entity-relationship storage.
 */
public interface GraphStore {

    /**
     * Upsert a single entity.
     *
     * @param entity the entity to upsert
     */
    void upsertEntity(Entity entity);

    /**
     * Get entity by ID.
     *
     * @param id the entity ID
     * @return the entity if found
     */
    Optional<Entity> getEntity(String id);

    /**
     * Get all entities of a specific type.
     *
     * @param type the entity type
     * @return list of entities
     */
    List<Entity> getEntitiesByType(String type);

    /**
     * Delete an entity by ID.
     *
     * @param id the entity ID
     */
    void deleteEntity(String id);

    /**
     * Upsert a single relation.
     *
     * @param relation the relation to upsert
     */
    void upsertRelation(Relation relation);

    /**
     * Get relation by ID.
     *
     * @param id the relation ID
     * @return the relation if found
     */
    Optional<Relation> getRelation(String id);

    /**
     * Get all relations for an entity.
     *
     * @param entityId the entity ID
     * @return list of relations
     */
    List<Relation> getRelations(String entityId);

    /**
     * Get neighbor entities connected via a specific relation type.
     *
     * @param entityId     the source entity ID
     * @param relationType the relation type
     * @return list of neighbor entities
     */
    List<Entity> getNeighborEntities(String entityId, String relationType);

    /**
     * Delete a relation by ID.
     *
     * @param id the relation ID
     */
    void deleteRelation(String id);

    /**
     * Search entities by query string.
     *
     * @param query the search query
     * @param topK  number of results to return
     * @return list of matching entities
     */
    List<Entity> searchEntities(String query, int topK);

    /**
     * Find paths between two entities.
     *
     * @param sourceId the source entity ID
     * @param targetId the target entity ID
     * @param maxDepth maximum path depth
     * @return list of paths found
     */
    List<Path> findPaths(String sourceId, String targetId, int maxDepth);

    /**
     * Upsert multiple entities in batch.
     *
     * @param entities list of entities
     */
    void upsertEntityBatch(List<Entity> entities);

    /**
     * Upsert multiple relations in batch.
     *
     * @param relations list of relations
     */
    void upsertRelationBatch(List<Relation> relations);

    /**
     * Represents a path between entities in the graph.
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
