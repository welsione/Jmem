package com.jmem.embedder;

/**
 * 文本向量化接口。
 */
public interface Embedder {

    /**
     * 将单个文本转换为向量。
     *
     * @param text 要转换的文本
     * @return 文本向量
     */
    float[] embed(String text);

    /**
     * 获取向量维度。
     *
     * @return 向量维度
     */
    int getDimension();
}
