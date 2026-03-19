package com.jmem.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;

/**
 * Jackson JSON 工具类封装。
 */
public class JsonUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    private JsonUtils() {
    }

    /**
     * 将对象序列化为 JSON 字符串。
     */
    public static String toJson(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("序列化 JSON 失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将 JSON 字符串反序列化为对象。
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("反序列化 JSON 失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将对象转换为 JsonNode。
     */
    public static JsonNode toNode(Object obj) {
        return MAPPER.valueToTree(obj);
    }

    /**
     * 将 JsonNode 转换为对象。
     */
    public static <T> T fromNode(JsonNode node, Class<T> clazz) {
        try {
            return MAPPER.treeToValue(node, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("转换 JsonNode 失败: " + e.getMessage(), e);
        }
    }

    /**
     * 解析 JSON 字符串为 JsonNode。
     */
    public static JsonNode parse(String json) {
        try {
            return MAPPER.readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("解析 JSON 失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取 ObjectMapper 实例。
     */
    public static ObjectMapper mapper() {
        return MAPPER;
    }

    /**
     * 检查字符串是否为有效的 JSON。
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
