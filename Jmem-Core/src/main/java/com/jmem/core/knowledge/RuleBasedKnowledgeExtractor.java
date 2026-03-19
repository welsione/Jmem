package com.jmem.core.knowledge;

import com.jmem.model.Memory;
import com.jmem.model.MemoryScope;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 基于规则的知识提取器实现。
 * 不依赖 LLM，使用简单的规则和启发式方法提取知识。
 */
public class RuleBasedKnowledgeExtractor implements KnowledgeExtractor {

    private static final Pattern SENTENCE_END = Pattern.compile("[。！？.!?]+");
    private static final int MAX_KEYWORDS = 5;

    @Override
    public String extract(Memory memory) {
        if (memory == null || memory.getData() == null) {
            return "";
        }

        String content = memory.getData().toString();
        List<String> sentences = extractSentences(content);
        String summary = summarizeSentences(sentences);
        List<String> keywords = extractKeywords(content);

        StringBuilder sb = new StringBuilder();
        sb.append("【摘要】").append(summary).append("\n");
        sb.append("【关键词】").append(String.join(", ", keywords)).append("\n");

        if (memory.getScope() != null) {
            sb.append("【范围】").append(memory.getScope()).append("\n");
        }

        return sb.toString();
    }

    @Override
    public String summarize(List<Memory> memories) {
        if (memories == null || memories.isEmpty()) {
            return "";
        }

        // 收集所有内容
        List<String> allSentences = new ArrayList<>();
        Set<String> allKeywords = new HashSet<>();
        Map<MemoryScope, Integer> scopeCount = new HashMap<>();

        for (Memory m : memories) {
            if (m.getData() == null) continue;

            String content = m.getData().toString();
            allSentences.addAll(extractSentences(content));
            allKeywords.addAll(extractKeywords(content));

            if (m.getScope() != null) {
                scopeCount.merge(m.getScope(), 1, Integer::sum);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("【汇总】\n");

        // 找出最常见的范围
        MemoryScope mostCommonScope = scopeCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        if (mostCommonScope != null) {
            sb.append("主要范围：").append(mostCommonScope)
                    .append(" (").append(scopeCount.get(mostCommonScope)).append("条记忆)\n");
        }

        // 提取前几句作为摘要
        List<String> keySentences = allSentences.stream()
                .filter(s -> s.length() > 10)
                .distinct()
                .limit(3)
                .collect(Collectors.toList());

        if (!keySentences.isEmpty()) {
            sb.append("【要点】\n");
            for (int i = 0; i < keySentences.size(); i++) {
                sb.append(i + 1).append(". ").append(keySentences.get(i)).append("\n");
            }
        }

        // 关键词
        List<String> topKeywords = allKeywords.stream().limit(MAX_KEYWORDS).collect(Collectors.toList());
        if (!topKeywords.isEmpty()) {
            sb.append("【关键词】").append(String.join(", ", topKeywords)).append("\n");
        }

        sb.append("【统计】共").append(memories.size()).append("条记忆");

        return sb.toString();
    }

    /**
     * 提取句子列表。
     */
    private List<String> extractSentences(String content) {
        String[] parts = SENTENCE_END.split(content);
        return Arrays.stream(parts)
                .map(String::trim)
                .filter(s -> s.length() > 5)
                .collect(Collectors.toList());
    }

    /**
     * 简单摘要：取前几句。
     */
    private String summarizeSentences(List<String> sentences) {
        if (sentences.isEmpty()) {
            return "";
        }
        return String.join("；", sentences.stream().limit(2).collect(Collectors.toList()));
    }

    /**
     * 简单关键词提取：基于词频和长度。
     */
    private List<String> extractKeywords(String content) {
        // 简单实现：提取连续的中文字符序列作为候选词
        List<String> candidates = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (char c : content.toCharArray()) {
            if (Character.isLetterOrDigit(c) || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
                current.append(c);
            } else {
                if (current.length() >= 2) {
                    candidates.add(current.toString());
                }
                current.setLength(0);
            }
        }

        if (current.length() >= 2) {
            candidates.add(current.toString());
        }

        // 过滤停用词并返回
        return candidates.stream()
                .filter(this::isNotStopWord)
                .filter(s -> s.length() >= 2 && s.length() <= 6)
                .distinct()
                .limit(MAX_KEYWORDS)
                .collect(Collectors.toList());
    }

    private boolean isNotStopWord(String word) {
        // 简单的停用词过滤
        Set<String> stopWords = Set.of(
                "的", "了", "是", "在", "我", "有", "和", "就",
                "不", "人", "都", "一", "一个", "上", "也", "很",
                "到", "说", "要", "去", "你", "会", "着", "没有",
                "看", "好", "自己", "这", "那", "他", "她", "它",
                "我们", "你们", "他们", "什么", "这个", "那个"
        );
        return !stopWords.contains(word);
    }
}
