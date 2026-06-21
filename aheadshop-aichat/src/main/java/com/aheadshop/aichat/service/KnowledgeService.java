package com.aheadshop.aichat.service;

/**
 * RAG 知识检索服务
 * 从商品库和 FAQ 中检索相关信息，注入 LLM 上下文
 */
public interface KnowledgeService {

    /**
     * 检索知识上下文
     * @param userMessage 用户消息
     * @return 格式化的知识文本，可直接注入 system prompt；无匹配时返回空字符串
     */
    String retrieve(String userMessage);
}
