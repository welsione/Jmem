package com.jmem.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;

/**
 * Jackson JSON utilities wrapper.
 */
public class JsonUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    private JsonUtils() {
    }

    /**
     * Serialize object to JSON string.
     */
    public static String toJson(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize to JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Deserialize JSON string to object.
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize from JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Convert object to JsonNode.
     */
    public static JsonNode toNode(Object obj) {
        return MAPPER.valueToTree(obj);
    }

    /**
     * Convert JsonNode to object.
     */
    public static <T> T fromNode(JsonNode node, Class<T> clazz) {
        try {
            return MAPPER.treeToValue(node, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert node to object: " + e.getMessage(), e);
        }
    }

    /**
     * Parse JSON string to JsonNode.
     */
    public static JsonNode parse(String json) {
        try {
            return MAPPER.readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Get ObjectMapper instance.
     */
    public static ObjectMapper mapper() {
        return MAPPER;
    }

    /**
     * Check if a string is valid JSON.
     */
    public static boolean isValid(String json) {
        try {
            MAPPER.readTree(json);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
