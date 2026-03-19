# 互惠排名融合（Reciprocal Rank Fusion, RRF）

## 概述

互惠排名融合（RRF）是一种用于融合多个检索系统结果的算法，最初由光线研究实验室（In-quentik/Google）在 2011 年提出。该算法通过多个排序列表的排名信息来计算每个物品的最终得分，避免了传统得分归一化的问题。

## 算法原理

### 核心公式

对于 K 个不同的排序列表，RRF 使用以下公式计算每个物品的融合得分：

```
RRF_score(d) = Σ 1/(k + rank(d))
```

其中：
- `d` 是待评估的文档/物品
- `k` 是平滑参数（通常为 60）
- `rank(d)` 是文档 d 在当前排序列表中的排名（从 1 开始）

### 为什么使用 RRF？

1. **无需得分归一化**：不同检索系统（如向量搜索、关键词搜索）返回的相似度得分范围可能差异很大，RRF 只依赖排名，不受得分尺度影响。

2. **简单高效**：算法复杂度为 O(n)，其中 n 是所有列表中的文档总数。

3. **效果好**：在多项检索任务中，RRF 被证明优于复杂的得分归一化方法（如 z-score 归一化、min-max 归一化）。

4. **可解释性强**：结果由排名决定，易于理解和调试。

## 参数说明

### k 值

参数 `k` 起到平滑作用：
- **k = 0**：退化为取排名最小值（只取第一个列表的排名）
- **k 越大**：各列表的影响力越平均
- **k → ∞**：所有文档得分趋近于 0，失去区分度

通常推荐 `k = 60`，这是大量实验验证得出的默认值。

## 代码实现

```java
public class ReciprocalRankFusionStrategy implements FusionStrategy {

    private static final int DEFAULT_K = 60;
    private final int k;

    @Override
    public List<Memory> fuse(List<? extends List<? extends ScoredResult>> resultLists, int topK, MemoryScope scope) {
        Map<String, Double> rrfScores = new HashMap<>();
        Map<String, Memory> memoryMap = new HashMap<>();

        // 对每个结果列表计分
        for (List<? extends ScoredResult> results : resultLists) {
            for (int rank = 0; rank < results.size(); rank++) {
                ScoredResult result = results.get(rank);
                String id = result.getId();
                // RRF 公式：1/(k + rank)
                double score = 1.0 / (k + rank + 1);
                rrfScores.merge(id, score, Double::sum);

                if (result.getMemory() != null) {
                    memoryMap.put(id, result.getMemory());
                }
            }
        }

        // 按 RRF 分数降序排序并取前 K 个
        return rrfScores.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(topK)
                .map(entry -> memoryMap.get(entry.getKey()))
                .filter(Objects::nonNull)
                .filter(m -> scope == null || m.getScope() == scope)
                .toList();
    }
}
```

## 在 Jmem 中的应用

Jmem 使用 RRF 融合向量搜索和文档搜索的结果：

1. **向量搜索**：基于语义相似度检索记忆
2. **文档搜索**：基于关键词匹配检索记忆

两种搜索方式各有优缺点：
- 向量搜索：语义理解强，但可能遗漏精确关键词匹配
- 文档搜索：关键词匹配精确，但无法理解语义

通过 RRF 融合，可以兼顾两种搜索方式的优势，提升检索效果。

## 参考资料

- [Reciprocal Rank Fusion outperforms Condorcet and individual ranking methods](https://plg.uwaterloo.ca/~gvcormac/cormacksigir09-rrf.pdf) - 原始论文
