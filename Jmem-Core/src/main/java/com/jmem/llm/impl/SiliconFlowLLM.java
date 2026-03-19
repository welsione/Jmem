package com.jmem.llm.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jmem.config.JmemProperties.LLMConfig;
import com.jmem.llm.LLM;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 使用 SiliconFlow API 的 LLM 实现。
 */
public class SiliconFlowLLM implements LLM {

    private static final String API_URL = "https://api.siliconflow.cn/v1/chat/completions";

    private final String apiKey;
    private final String model;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public SiliconFlowLLM(LLMConfig config) {
        this(config != null && config.getApiKey() != null ? config.getApiKey() : System.getenv("SILICONFLOW_API_KEY"),
             config != null && config.getModel() != null ? config.getModel() : "Qwen/Qwen2.5-7B-Instruct");
    }

    public SiliconFlowLLM(String apiKey, String model) {
        this.apiKey = apiKey;
        this.model = model;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String generate(String prompt) {
        return chat(List.of(prompt));
    }

    @Override
    public String generateWithContext(List<String> messages) {
        return chat(messages);
    }

    private String chat(List<String> messages) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);

            java.util.List<Map<String, String>> formattedMessages = new java.util.ArrayList<>();
            for (int i = 0; i < messages.size(); i++) {
                String role = i % 2 == 0 ? "user" : "assistant";
                formattedMessages.add(Map.of("role", role, "content", messages.get(i)));
            }
            requestBody.put("messages", formattedMessages);

            String jsonBody = objectMapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("SiliconFlow API error: " + response.statusCode() + " - " + response.body());
            }

            JsonNode root = objectMapper.readTree(response.body());
            return root.path("choices").path(0).path("message").path("content").asText();

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("LLM 调用失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String extractKnowledge(String text) {
        // 使用专门的提示词进行知识提取
        String prompt = "请从以下文本中提取关键信息，以简洁的结构化格式返回：\n\n" + text;
        return generate(prompt);
    }
}
