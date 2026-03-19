package com.jmem.llm;

/**
 * 大语言模型交互接口。
 */
public interface LLM {

    /**
     * 根据提示生成响应。
     *
     * @param prompt 输入提示
     * @return 生成的响应
     */
    String generate(String prompt);

    /**
     * 带聊天历史上下文的响应生成。
     *
     * @param messages 表示对话历史的字符串列表
     * @return 生成的响应
     */
    String generateWithContext(java.util.List<String> messages);

    /**
     * 从文本中提取知识。
     *
     * @param text 要提取知识的文本
     * @return 结构化文本形式的提取知识
     */
    String extractKnowledge(String text);
}
