package com.jmem.core.fusion;

import com.jmem.model.Memory;
import com.jmem.model.MemoryScope;

import java.util.*;

/**
 * 互惠排名融合（RRF）策略实现。
 * 融合多个搜索结果列表，使用公式: score = Σ 1/(k+rank)
 *
 * @see <a href="https://github.com/welsione/Jmem/blob/master/doc/fusion/RRF.md">RRF 算法文档</a>
 */
public class ReciprocalRankFusionStrategy implements FusionStrategy {

    private static final int DEFAULT_K = 60;

    private final int k;

    public ReciprocalRankFusionStrategy() {
        this(DEFAULT_K);
    }

    public ReciprocalRankFusionStrategy(int k) {
        this.k = k;
    }

    @Override
    public List<Memory> fuse(List<? extends List<? extends ScoredResult>> resultLists, int topK, MemoryScope scope) {
        Map<String, Double> rrfScores = new HashMap<>();
        Map<String, Memory> memoryMap = new HashMap<>();

        for (List<? extends ScoredResult> results : resultLists) {
            scoreResults(results, rrfScores, memoryMap);
        }

        return rrfScores.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(topK)
                .map(entry -> memoryMap.get(entry.getKey()))
                .filter(Objects::nonNull)
                .filter(m -> scope == null || m.getScope() == scope)
                .toList();
    }

    private void scoreResults(List<? extends ScoredResult> results, Map<String, Double> rrfScores, Map<String, Memory> memoryMap) {
        for (int rank = 0; rank < results.size(); rank++) {
            ScoredResult result = results.get(rank);
            String id = result.getId();
            double score = 1.0 / (k + rank + 1);
            rrfScores.merge(id, score, Double::sum);

            if (result.getMemory() != null) {
                memoryMap.put(id, result.getMemory());
            }
        }
    }
}
