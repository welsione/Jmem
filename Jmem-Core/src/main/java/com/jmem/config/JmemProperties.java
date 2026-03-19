package com.jmem.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "jmem")
public class JmemProperties {

    private boolean enabled = false;
    private String provider = "in-memory";

    private EmbedderConfig embedder = new EmbedderConfig();
    private VectorStoreConfig vectorStore = new VectorStoreConfig();

    @Data
    public static class EmbedderConfig {
        private String apiKey = System.getenv("SILICONFLOW_API_KEY");
        private String model = "BAAI/bge-m3";
        private int dimension = 1024;
    }

    @Data
    public static class VectorStoreConfig {
        private String type = "in-memory";  // "in-memory" or "qdrant"
        private String url = "http://localhost:6333";
        private String collectionName = "jmem_memories";
        private Integer vectorSize = 1024;
    }
}
