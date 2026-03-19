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

        String prompt = PromptTemplate.replace(
                PromptTemplate.EXTRACT_TEMPLATE,
                memory.getData().toString(),
                memory.getScope() != null ? memory.getScope().name() : "未指定",
                memory.getUserId() != null ? memory.getUserId() : "未指定",
                memory.getSessionId() != null ? memory.getSessionId() : "未指定"
        );

        return llm.extractKnowledge(prompt);
    }

    @Override
    public String summarize(List<Memory> memories) {
        if (memories == null || memories.isEmpty()) {
            return "";
        }

        String memoryList = PromptTemplate.formatMemoryList(memories);
        String prompt = PromptTemplate.replace(
                PromptTemplate.SUMMARIZE_TEMPLATE,
                memoryList
        );

        return llm.generate(prompt);
    }

    @Override
    public boolean isSupported() {
        return llm != null;
    }
}
