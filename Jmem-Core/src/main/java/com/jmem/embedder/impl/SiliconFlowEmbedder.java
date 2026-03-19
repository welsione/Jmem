package com.jmem.embedder.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jmem.config.JmemProperties.EmbedderConfig;
import com.jmem.embedder.Embedder;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

/**
 * Embedder implementation using SiliconFlow API.
 * API Docs: https://docs.siliconflow.cn/cn/api-reference/embeddings/create-embeddings
 */
public class SiliconFlowEmbedder implements Embedder {
    
    private static final String API_URL = "https://api.siliconflow.cn/v1/embeddings";
    
    private final String apiKey;
    private final String model;
    private final int dimension;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public SiliconFlowEmbedder() {
        this(null, null, 0);
    }
    
    public SiliconFlowEmbedder(EmbedderConfig config) {
        this(config != null ? config.getApiKey() : null,
                config != null ? config.getModel() : null,
                config != null ? config.getDimension() : 0);
    }
    
    public SiliconFlowEmbedder(String apiKey, String model, int dimension) {
        this.apiKey = apiKey;
        this.model = model;
        this.dimension = dimension;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public float[] embed(String text) {
        if (text == null || text.isEmpty()) {
            return new float[dimension];
        }
        
        try {
            String requestBody = objectMapper.writeValueAsString(Map.of(
                    "model", model,
                    "input", text
            ));
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                throw new RuntimeException("SiliconFlow API error: " + response.statusCode() + " - " + response.body());
            }
            
            JsonNode root = objectMapper.readTree(response.body());
            JsonNode embeddingNode = root.path("data").path(0).path("embedding");
            
            float[] embedding = new float[embeddingNode.size()];
            for (int i = 0; i < embedding.length; i++) {
                embedding[i] = (float) embeddingNode.get(i).asDouble();
            }
            
            return embedding;
            
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to get embedding from SiliconFlow: " + e.getMessage(), e);
        }
    }
    
    @Override
    public int getDimension() {
        return dimension;
    }
    
    public String getModel() {
        return model;
    }
}
