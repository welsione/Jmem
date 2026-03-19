package com.jmem.core.knowledge;

import com.jmem.llm.LLM;
import com.jmem.model.Memory;
import com.jmem.util.PromptUtils;

import java.util.List;
import java.util.Map;

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

        Map<String, String> params = PromptUtils.buildExtractParams(memory);
        String prompt = PromptUtils.fillTemplate(PromptTemplate.EXTRACT_TEMPLATE, params);

        return llm.extractKnowledge(prompt);
    }

    @Override
    public String summarize(List<Memory> memories) {
        if (memories == null || memories.isEmpty()) {
            return "";
        }

        Map<String, String> params = PromptUtils.buildSummarizeParams(memories);
        String prompt = PromptUtils.fillTemplate(PromptTemplate.SUMMARIZE_TEMPLATE, params);

        return llm.generate(prompt);
    }

    @Override
    public boolean isSupported() {
        return llm != null;
    }
}
