package com.jmem.storage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 向量搜索操作的过滤器。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchFilter {

    private String scope;
    private String userId;
    private String sessionId;
    private String agentId;
    private String content;  // 用于文本匹配
}
