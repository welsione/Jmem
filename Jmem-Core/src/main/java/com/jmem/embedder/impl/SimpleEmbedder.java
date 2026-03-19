package com.jmem.embedder.impl;

import com.jmem.embedder.Embedder;

/**
 * 基于字符哈希的简单向量化实现。
 * 为文本生成确定性向量。
 */
public class SimpleEmbedder implements Embedder {

    private static final int DEFAULT_DIMENSION = 128;

    @Override
    public float[] embed(String text) {
        float[] vector = new float[DEFAULT_DIMENSION];
        if (text == null || text.isEmpty()) {
            return vector;
        }

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            int bucket = (c * 31 + i) % DEFAULT_DIMENSION;
            vector[bucket] += (float) c / 255.0f;
        }

        // 归一化
        double sum = 0;
        for (float v : vector) {
            sum += v * v;
        }
        double norm = Math.sqrt(sum);
        if (norm > 0) {
            for (int i = 0; i < vector.length; i++) {
                vector[i] = (float) (vector[i] / norm);
            }
        }

        return vector;
    }

    @Override
    public int getDimension() {
        return DEFAULT_DIMENSION;
    }
}
