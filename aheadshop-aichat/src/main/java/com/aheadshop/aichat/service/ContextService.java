package com.aheadshop.aichat.service;

import org.springframework.ai.chat.messages.Message;

import java.util.List;

public interface ContextService {

    /**
     * 从 Redis 获取最近 N 轮对话历史
     */
    List<Message> buildContext(Long userId, Long sessionId, String userMessage);

    /**
     * 将本轮对话写入 Redis 上下文
     */
    void saveToContext(Long sessionId, String role, String content);

    /**
     * 清除会话的 Redis 上下文缓存
     */
    void clearContext(Long sessionId);
}
