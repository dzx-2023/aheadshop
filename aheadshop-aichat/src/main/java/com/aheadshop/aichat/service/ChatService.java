package com.aheadshop.aichat.service;

import com.aheadshop.aichat.domain.dto.ChatRequest;
import com.aheadshop.aichat.domain.dto.ChatResponse;
import com.aheadshop.aichat.domain.po.ChatMessage;
import com.aheadshop.aichat.domain.po.ChatSession;
import com.aheadshop.aichat.domain.vo.ChatSessionVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ChatService extends IService<ChatSession> {

    /**
     * 创建新会话
     */
    Long createSession(Long userId);

    /**
     * 发送消息并获取 LLM 回复
     */
    ChatResponse sendMessage(Long userId, ChatRequest req);

    /**
     * 查询用户会话列表（按更新时间倒序）
     */
    List<ChatSessionVO> getSessions(Long userId);

    /**
     * 查询会话消息列表
     */
    List<ChatMessage> getSessionMessages(Long userId, Long sessionId);

    /**
     * 逻辑删除会话及消息
     */
    void deleteSession(Long userId, Long sessionId);
}
