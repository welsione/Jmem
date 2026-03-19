package com.jmem.core.knowledge;

import com.jmem.llm.LLM;
import com.jmem.model.Memory;

import java.util.List;

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

        String prompt = PromptTemplate.EXTRACT_TEMPLATE
                .replace("{content}", memory.getData().toString())
                .replace("{scope}", memory.getScope() != null ? memory.getScope().name() : "未指定")
                .replace("{userId}", memory.getUserId() != null ? memory.getUserId() : "未指定")
                .replace("{sessionId}", memory.getSessionId() != null ? memory.getSessionId() : "未指定");

        return llm.extractKnowledge(prompt);
    }

    @Override
    public String summarize(List<Memory> memories) {
        if (memories == null || memories.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < memories.size(); i++) {
            Memory m = memories.get(i);
            sb.append("【记忆 ").append(i + 1).append("】\n");
            sb.append("范围：").append(m.getScope()).append("\n");
            sb.append("内容：").append(m.getData()).append("\n\n");
        }

        String prompt = PromptTemplate.SUMMARIZE_TEMPLATE.replace("{memoryList}", sb.toString());

        return llm.generate(prompt);
    }

    @Override
    public boolean isSupported() {
        return llm != null;
    }
}
