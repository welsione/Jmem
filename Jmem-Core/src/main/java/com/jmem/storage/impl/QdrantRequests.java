package com.jmem.storage.impl;

import com.jmem.storage.Payload;
import com.jmem.storage.SearchFilter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Qdrant 请求/响应 POJO 类，用于 JSON 序列化。
 */
public class QdrantRequests {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Point {
        private String id;
        private float[] vector;
        private Payload payload;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PointsBody {
        private List<Point> points;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchRequest {
        private float[] vector;
        private int limit;
        private SearchFilter filter;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteRequest {
        private List<String> points;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CollectionConfig {
        private VectorConfig vectors;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class VectorConfig {
            private int size;
            private String distance;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateCollectionRequest {
        private CollectionConfig config;
    }
}
