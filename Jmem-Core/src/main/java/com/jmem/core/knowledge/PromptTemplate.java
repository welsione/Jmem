package com.jmem.core.knowledge;

/**
 * 知识提取提示词模板。
 */
public class PromptTemplate {

    /**
     * 单条记忆知识提取的提示词模板。
     * 占位符：
     * {0} - 记忆内容
     * {1} - 记忆范围
     * {2} - 用户ID
     * {3} - 会话ID
     */
    public static final String EXTRACT_TEMPLATE = """
            请从以下记忆内容中提取关键信息，生成简洁的结构化描述。

            记忆内容：{0}

            元信息：
            范围：{1}
            用户：{2}
            会话：{3}

            请按以下格式提取信息：
            1. 主要事件或动作：
            2. 涉及的关键实体：
            3. 时间或上下文信息：
            4. 重要的细节或结论：
            """;

    /**
     * 多条记忆汇总的提示词模板。
     * 占位符：
     * {0} - 记忆列表（格式化的记忆内容）
     */
    public static final String SUMMARIZE_TEMPLATE = """
            请分析以下记忆列表，生成一个简洁的汇总。

            {0}

            请生成一个简洁的汇总，包括：
            1. 这些记忆的核心主题：
            2. 主要的事件或模式：
            3. 重要的见解或结论：
            """;

    /**
     * 格式化单条记忆的元信息。
     */
    public static String formatMemoryMetadata(String scope, String userId, String sessionId) {
        return String.format("范围：%s；用户：%s；会话：%s",
                scope != null ? scope : "未指定",
                userId != null ? userId : "未指定",
                sessionId != null ? sessionId : "未指定");
    }

    /**
     * 格式化记忆列表用于汇总。
     */
    public static String formatMemoryList(java.util.List<? extends com.jmem.model.Memory> memories) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < memories.size(); i++) {
            com.jmem.model.Memory m = memories.get(i);
            sb.append("【记忆 ").append(i + 1).append("】\n");
            sb.append("范围：").append(m.getScope()).append("\n");
            sb.append("内容：").append(m.getData()).append("\n\n");
        }
        return sb.toString();
    }

    /**
     * 替换模板中的占位符。
     *
     * @param template 模板字符串
     * @param args    参数列表
     * @return 替换后的字符串
     */
    public static String replace(String template, Object... args) {
        String result = template;
        for (int i = 0; i < args.length; i++) {
            result = result.replace("{" + i + "}", args[i] != null ? args[i].toString() : "");
        }
        return result;
    }
}
