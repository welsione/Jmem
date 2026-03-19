package com.jmem.embedder;

/**
 * Embedder interface for text vectorization.
 */
public interface Embedder {

    /**
     * Embed a single text into a vector.
     *
     * @param text the text to embed
     * @return the embedding vector
     */
    float[] embed(String text);

    /**
     * Get the dimension of the embedding vectors.
     *
     * @return the embedding dimension
     */
    int getDimension();
}
