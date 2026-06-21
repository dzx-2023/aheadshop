package com.aheadshop.aichat.service.impl;

import com.aheadshop.aichat.domain.dto.ChatRequest;
import com.aheadshop.aichat.domain.dto.ChatResponse;
import com.aheadshop.aichat.domain.dto.ChatResult;
import com.aheadshop.aichat.domain.po.ChatMessage;
import com.aheadshop.aichat.domain.po.ChatSession;
import com.aheadshop.aichat.domain.vo.ChatSessionVO;
import com.aheadshop.aichat.mapper.ChatMessageMapper;
import com.aheadshop.aichat.mapper.ChatSessionMapper;
import com.aheadshop.aichat.service.ChatService;
import com.aheadshop.aichat.service.ContextService;
import com.aheadshop.aichat.service.KnowledgeService;
import com.aheadshop.aichat.service.LlmService;
import com.aheadshop.aichat.service.RateLimitService;
import com.aheadshop.common.core.exception.BusinessException;
import com.aheadshop.common.core.exception.BusinessExceptionCode;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl extends ServiceImpl<ChatSessionMapper, ChatSession> implements ChatService {

    private final ChatSessionMapper chatSessionMapper;
    private final ChatMessageMapper chatMessageMapper;
    private final ContextService contextService;
    private final LlmService llmService;
    private final KnowledgeService knowledgeService;
    private final RateLimitService rateLimitService;

    @Override
    @Transactional
    public Long createSession(Long userId) {
        ChatSession session = new ChatSession();
        session.setUserId(userId);
        session.setTitle("新对话");
        session.setStatus(1);
        session.setTotalTokens(0);
        chatSessionMapper.insert(session);
        log.info("创建会话: sessionId={}, userId={}", session.getId(), userId);
        return session.getId();
    }

    @Override
    @Transactional
    public ChatResponse sendMessage(Long userId, ChatRequest req) {
        Long sessionId = req.getSessionId();

        // 0. 限流检查
        rateLimitService.checkRateLimit(userId);

        // 1. 校验会话存在且属于当前用户
        ChatSession session = getAndValidateSession(userId, sessionId);

        // 2. 保存用户消息到 DB
        ChatMessage userMsg = new ChatMessage();
        userMsg.setSessionId(sessionId);
        userMsg.setRole("user");
        userMsg.setContent(req.getContent());
        userMsg.setTokenCount(0);
        chatMessageMapper.insert(userMsg);

        // 3. 写入 Redis 上下文
        contextService.saveToContext(sessionId, "user", req.getContent());

        // 4. 构建上下文（历史消息）
        List<Message> history = contextService.buildContext(userId, sessionId, req.getContent());

        // 5. RAG 知识检索 + 构建 system prompt
        String knowledge = knowledgeService.retrieve(req.getContent());
        String systemPrompt = loadSystemPrompt(knowledge);

        // 6. 调用 LLM（返回 token 用量）
        ChatResult llmResult = llmService.chat(systemPrompt, history, req.getContent());
        String reply = llmResult.getContent();
        int tokenCount = llmResult.getTokenCount();

        // 7. 保存 assistant 消息到 DB（记录 token 用量）
        ChatMessage assistantMsg = new ChatMessage();
        assistantMsg.setSessionId(sessionId);
        assistantMsg.setRole("assistant");
        assistantMsg.setContent(reply);
        assistantMsg.setTokenCount(tokenCount);
        chatMessageMapper.insert(assistantMsg);

        // 8. 累加会话 token 总量
        if (tokenCount > 0) {
            chatSessionMapper.update(null,
                    new LambdaUpdateWrapper<ChatSession>()
                            .eq(ChatSession::getId, sessionId)
                            .setSql("total_tokens = total_tokens + " + tokenCount)
            );
        }

        // 9. 写入 Redis 上下文
        contextService.saveToContext(sessionId, "assistant", reply);

        // 10. 更新会话标题（首条消息时用用户内容截取）
        if ("新对话".equals(session.getTitle())) {
            String title = req.getContent().length() > 50
                    ? req.getContent().substring(0, 50) + "..."
                    : req.getContent();
            session.setTitle(title);
            chatSessionMapper.updateById(session);
        }

        // 11. 返回响应
        ChatResponse response = new ChatResponse();
        response.setSessionId(sessionId);
        response.setMessageId(assistantMsg.getId());
        response.setContent(reply);
        response.setTokenCount(tokenCount);

        log.info("消息处理完成: sessionId={}, messageId={}, tokens={}", sessionId, assistantMsg.getId(), tokenCount);
        return response;
    }

    @Override
    public List<ChatSessionVO> getSessions(Long userId) {
        List<ChatSession> sessions = chatSessionMapper.selectList(
                new LambdaQueryWrapper<ChatSession>()
                        .eq(ChatSession::getUserId, userId)
                        .orderByDesc(ChatSession::getUpdateTime)
        );

        return sessions.stream().map(this::toSessionVO).collect(Collectors.toList());
    }

    @Override
    public List<ChatMessage> getSessionMessages(Long userId, Long sessionId) {
        getAndValidateSession(userId, sessionId);

        return chatMessageMapper.selectList(
                new LambdaQueryWrapper<ChatMessage>()
                        .eq(ChatMessage::getSessionId, sessionId)
                        .orderByAsc(ChatMessage::getCreateTime)
        );
    }

    @Override
    @Transactional
    public void deleteSession(Long userId, Long sessionId) {
        getAndValidateSession(userId, sessionId);

        chatMessageMapper.update(
                new LambdaUpdateWrapper<ChatMessage>()
                        .eq(ChatMessage::getSessionId, sessionId)
                        .set(ChatMessage::getDeleted, 1)
        );

        chatSessionMapper.update(
                new LambdaUpdateWrapper<ChatSession>()
                        .eq(ChatSession::getId, sessionId)
                        .eq(ChatSession::getUserId, userId)
                        .set(ChatSession::getDeleted, 1)
        );

        contextService.clearContext(sessionId);
        log.info("删除会话: sessionId={}, userId={}", sessionId, userId);
    }

    private ChatSession getAndValidateSession(Long userId, Long sessionId) {
        ChatSession session = chatSessionMapper.selectOne(
                new LambdaQueryWrapper<ChatSession>()
                        .eq(ChatSession::getId, sessionId)
                        .eq(ChatSession::getUserId, userId)
        );
        if (session == null) {
            throw new BusinessException(BusinessExceptionCode.CHAT_SESSION_NOT_FOUND, "会话不存在");
        }
        return session;
    }

    private ChatSessionVO toSessionVO(ChatSession session) {
        ChatMessage lastMsg = chatMessageMapper.selectOne(
                new LambdaQueryWrapper<ChatMessage>()
                        .eq(ChatMessage::getSessionId, session.getId())
                        .orderByDesc(ChatMessage::getCreateTime)
                        .last("LIMIT 1")
        );

        return ChatSessionVO.builder()
                .id(session.getId())
                .title(session.getTitle())
                .status(session.getStatus())
                .createTime(session.getCreateTime())
                .lastMessage(lastMsg != null ? lastMsg.getContent() : null)
                .totalTokens(session.getTotalTokens())
                .build();
    }

    private String loadSystemPrompt(String knowledge) {
        try {
            ClassPathResource resource = new ClassPathResource("prompts/system-prompt.st");
            String template = resource.getContentAsString(StandardCharsets.UTF_8);
            return template
                    .replace("{storeName}", "AheadShop")
                    .replace("{returnPolicy}", "7天无理由退换货，商品需保持原包装完好")
                    .replace("{knowledge}", knowledge != null ? knowledge : "");
        } catch (IOException e) {
            log.warn("读取 system prompt 模板失败，使用默认值", e);
            return "你是 AheadShop 的智能客服助手，友好专业地回答用户问题。";
        }
    }
}
