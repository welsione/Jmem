package com.jmem.core.knowledge;

import com.jmem.llm.LLM;
import com.jmem.model.Memory;
import com.jmem.model.MemoryScope;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 基于 LLM 的知识提取器实现。
 */
public class LLMKnowledgeExtractor implements KnowledgeExtractor {

    private final LLM llm;

    public LLMKnowledgeExtractor(LLM llm) {
        this.llm = llm;
    }

    @Override
    public String extract(Memory memory) {
        if (memory == null || memory.getData() == null) {
            return "";
        }

        String prompt = buildExtractPrompt(memory);
        return llm.extractKnowledge(prompt);
    }

    @Override
    public String summarize(List<Memory> memories) {
        if (memories == null || memories.isEmpty()) {
            return "";
        }

        String prompt = buildSummarizePrompt(memories);
        return llm.generate(prompt);
    }

    private String buildExtractPrompt(Memory memory) {
        StringBuilder sb = new StringBuilder();
        sb.append("请从以下记忆内容中提取关键信息，生成简洁的结构化描述：\n\n");
        sb.append("记忆内容：").append(memory.getData()).append("\n\n");

        if (memory.getScope() != null) {
            sb.append("范围：").append(memory.getScope()).append("\n");
        }
        if (memory.getUserId() != null) {
            sb.append("用户：").append(memory.getUserId()).append("\n");
        }
        if (memory.getSessionId() != null) {
            sb.append("会话：").append(memory.getSessionId()).append("\n");
        }

        sb.append("\n请提取：\n");
        sb.append("1. 主要事件或动作\n");
        sb.append("2. 涉及的关键实体\n");
        sb.append("3. 时间或上下文信息\n");
        sb.append("4. 重要的细节或结论\n");

        return sb.toString();
    }

    private String buildSummarizePrompt(List<Memory> memories) {
        StringBuilder sb = new StringBuilder();
        sb.append("请分析以下记忆列表，生成一个简洁的汇总：\n\n");

        for (int i = 0; i < memories.size(); i++) {
            Memory m = memories.get(i);
            sb.append("【记忆 ").append(i + 1).append("】\n");
            sb.append("范围：").append(m.getScope()).append("\n");
            sb.append("内容：").append(m.getData()).append("\n\n");
        }

        sb.append("请生成一个简洁的汇总，包括：\n");
        sb.append("1. 这些记忆的核心主题\n");
        sb.append("2. 主要的事件或模式\n");
        sb.append("3. 重要的见解或结论\n");

        return sb.toString();
    }

    @Override
    public boolean isSupported() {
        return llm != null;
    }
}
