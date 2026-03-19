package com.jmem.llm;

/**
 * LLM interface for large language model interactions.
 */
public interface LLM {

    /**
     * Generate a response from a prompt.
     *
     * @param prompt the input prompt
     * @return the generated response
     */
    String generate(String prompt);

    /**
     * Generate a response with chat history context.
     *
     * @param messages list of message strings representing conversation history
     * @return the generated response
     */
    String generateWithContext(java.util.List<String> messages);

    /**
     * Extract knowledge from text.
     *
     * @param text the text to extract knowledge from
     * @return extracted knowledge as structured text
     */
    String extractKnowledge(String text);
}
