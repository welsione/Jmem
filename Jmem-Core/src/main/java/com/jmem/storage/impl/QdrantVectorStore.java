package com.jmem.storage.impl;

import com.jmem.config.JmemProperties.VectorStoreConfig;
import com.jmem.storage.Payload;
import com.jmem.storage.SearchFilter;
import com.jmem.storage.VectorStore;
import com.jmem.util.JsonUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Qdrant vector store implementation using HTTP REST API.
 */
public class QdrantVectorStore implements VectorStore {

    private final String baseUrl;
    private final String collectionName;
    private final int vectorSize;
    private final HttpClient httpClient;

    // Qdrant API response POJOs
    private record SearchResponse(List<SearchResult> result) {}
    private record SearchResult(String id, double score, Payload payload, float[] vector) {}
    private record GetVectorResponse(GetPointResult result) {}
    private record GetPointResult(float[] vector) {}

    public QdrantVectorStore(VectorStoreConfig config) {
        this(config != null && config.getUrl() != null ? config.getUrl() : "http://localhost:6333",
             config != null && config.getCollectionName() != null ? config.getCollectionName() : "jmem_memories",
             1024);
    }

    public QdrantVectorStore(String url, String collectionName, int vectorSize) {
        this.baseUrl = url;
        this.collectionName = collectionName;
        this.vectorSize = vectorSize;
        this.httpClient = HttpClient.newHttpClient();
        initializeCollection();
    }

    private void initializeCollection() {
        try {
            String url = baseUrl + "/collections/" + collectionName;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 404) {
                createCollection();
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not initialize Qdrant collection: " + e.getMessage());
        }
    }

    private void createCollection() throws IOException, InterruptedException {
        String url = baseUrl + "/collections/" + collectionName;

        QdrantRequests.CreateCollectionRequest request = QdrantRequests.CreateCollectionRequest.builder()
                .config(QdrantRequests.CollectionConfig.builder()
                        .vectors(new QdrantRequests.CollectionConfig.VectorConfig(vectorSize, "Cosine"))
                        .build())
                .build();

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(JsonUtils.toJson(request)))
                .build();

        httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public void upsert(String id, float[] vector, Payload payload) {
        try {
            String url = baseUrl + "/collections/" + collectionName + "/points";

            QdrantRequests.PointsBody body = QdrantRequests.PointsBody.builder()
                    .points(List.of(QdrantRequests.Point.builder()
                            .id(id)
                            .vector(vector)
                            .payload(payload)
                            .build()))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(JsonUtils.toJson(body)))
                    .build();

            httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to upsert vector: " + e.getMessage(), e);
        }
    }

    @Override
    public void upsertBatch(List<VectorEntry> entries) {
        try {
            String url = baseUrl + "/collections/" + collectionName + "/points";

            List<QdrantRequests.Point> points = new ArrayList<>();
            for (VectorEntry entry : entries) {
                points.add(QdrantRequests.Point.builder()
                        .id(entry.getId())
                        .vector(entry.getVector())
                        .payload(entry.getPayload())
                        .build());
            }

            QdrantRequests.PointsBody body = QdrantRequests.PointsBody.builder()
                    .points(points)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(JsonUtils.toJson(body)))
                    .build();

            httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to upsert batch: " + e.getMessage(), e);
        }
    }

    @Override
    public List<VectorSearchResult> search(float[] queryVector, int topK, SearchFilter filter) {
        try {
            String url = baseUrl + "/collections/" + collectionName + "/points/search";

            QdrantRequests.SearchRequest request = QdrantRequests.SearchRequest.builder()
                    .vector(queryVector)
                    .limit(topK)
                    .filter(filter)
                    .build();

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(JsonUtils.toJson(request)))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Search failed: " + response.statusCode() + " - " + response.body());
            }

            SearchResponse searchResponse = JsonUtils.fromJson(response.body(), SearchResponse.class);

            List<VectorSearchResult> searchResults = new ArrayList<>();
            if (searchResponse != null && searchResponse.result() != null) {
                for (SearchResult result : searchResponse.result()) {
                    searchResults.add(new VectorSearchResult(result.id(), result.vector(), result.payload(), (float) result.score()));
                }
            }

            return searchResults;

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to search: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<float[]> getVector(String id) {
        try {
            String url = baseUrl + "/collections/" + collectionName + "/points/" + id;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 404) {
                return Optional.empty();
            }

            GetVectorResponse vectorResponse = JsonUtils.fromJson(response.body(), GetVectorResponse.class);

            if (vectorResponse == null || vectorResponse.result() == null ||
                vectorResponse.result().vector() == null || vectorResponse.result().vector().length == 0) {
                return Optional.empty();
            }

            return Optional.of(vectorResponse.result().vector());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to get vector: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String id) {
        try {
            String url = baseUrl + "/collections/" + collectionName + "/points/delete";

            QdrantRequests.DeleteRequest body = QdrantRequests.DeleteRequest.builder()
                    .points(List.of(id))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(JsonUtils.toJson(body)))
                    .build();

            httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to delete vector: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteCollection() {
        try {
            String url = baseUrl + "/collections/" + collectionName;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .DELETE()
                    .build();

            httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to delete collection: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean exists(String id) {
        return getVector(id).isPresent();
    }
}
