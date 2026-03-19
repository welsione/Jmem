package com.jmem.core.knowledge;

/**
 * 知识提取提示词模板。
 */
public class PromptTemplate {

    /**
     * 单条记忆知识提取的提示词模板。
     * 占位符：{content} {scope} {userId} {sessionId}
     */
    public static final String EXTRACT_TEMPLATE = """
            请从以下记忆内容中提取关键信息，生成简洁的结构化描述。

            记忆内容：{content}

            元信息：
            范围：{scope}
            用户：{userId}
            会话：{sessionId}

            请按以下格式提取信息：
            1. 主要事件或动作：
            2. 涉及的关键实体：
            3. 时间或上下文信息：
            4. 重要的细节或结论：
            """;

    /**
     * 多条记忆汇总的提示词模板。
     * 占位符：{memoryList}
     */
    public static final String SUMMARIZE_TEMPLATE = """
            请分析以下记忆列表，生成一个简洁的汇总。

            {memoryList}

            请生成一个简洁的汇总，包括：
            1. 这些记忆的核心主题：
            2. 主要的事件或模式：
            3. 重要的见解或结论：
            """;
}
