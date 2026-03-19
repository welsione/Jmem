package com.jmem.util;

import com.jmem.model.Memory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 提示词工具类，负责模板参数组装。
 */
public class PromptUtils {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{(\\w+)}");

    private PromptUtils() {
    }

    /**
     * 组装单条记忆的提取模板参数。
     *
     * @param memory 记忆
     * @return 参数映射
     */
    public static Map<String, String> buildExtractParams(Memory memory) {
        Map<String, String> params = new HashMap<>();
        params.put("content", memory.getData() != null ? memory.getData().toString() : "");
        params.put("scope", memory.getScope() != null ? memory.getScope().name() : "未指定");
        params.put("userId", memory.getUserId() != null ? memory.getUserId() : "未指定");
        params.put("sessionId", memory.getSessionId() != null ? memory.getSessionId() : "未指定");
        return params;
    }

    /**
     * 组装多条记忆汇总的模板参数。
     *
     * @param memories 记忆列表
     * @return 参数映射
     */
    public static Map<String, String> buildSummarizeParams(List<Memory> memories) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < memories.size(); i++) {
            Memory m = memories.get(i);
            sb.append("【记忆 ").append(i + 1).append("】\n");
            sb.append("范围：").append(m.getScope()).append("\n");
            sb.append("内容：").append(m.getData()).append("\n\n");
        }
        Map<String, String> params = new HashMap<>();
        params.put("memoryList", sb.toString());
        return params;
    }

    /**
     * 填充模板中的占位符。
     *
     * @param template 模板字符串
     * @param params   参数映射
     * @return 填充后的字符串
     */
    public static String fillTemplate(String template, Map<String, String> params) {
        if (template == null || params == null) {
            return template;
        }
        String result = template;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue() != null ? entry.getValue() : "");
        }
        return result;
    }

    /**
     * 填充模板中的占位符（单个参数）。
     *
     * @param template 模板字符串
     * @param key     占位符名称
     * @param value   参数值
     * @return 填充后的字符串
     */
    public static String fillTemplate(String template, String key, String value) {
        if (template == null || key == null) {
            return template;
        }
        return template.replace("{" + key + "}", value != null ? value : "");
    }
}
