package com.aheadshop.aichat.handler;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.aheadshop.aichat.domain.po.ChatMessage;
import com.aheadshop.aichat.domain.po.ChatSession;
import com.aheadshop.aichat.mapper.ChatMessageMapper;
import com.aheadshop.aichat.mapper.ChatSessionMapper;
import com.aheadshop.aichat.service.ContextService;
import com.aheadshop.aichat.service.KnowledgeService;
import com.aheadshop.aichat.service.LlmService;
import com.aheadshop.aichat.service.RateLimitService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.aheadshop.common.core.exception.BusinessException;
import com.aheadshop.common.core.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ChatSessionMapper chatSessionMapper;
    private final ChatMessageMapper chatMessageMapper;
    private final ContextService contextService;
    private final LlmService llmService;
    private final KnowledgeService knowledgeService;
    private final RateLimitService rateLimitService;

    /** sessionId → 当前活跃的 WebSocketSession */
    private final ConcurrentHashMap<Long, WebSocketSession> activeSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long sessionId = extractSessionId(session);
        if (sessionId == null) {
            closeWithError(session, "无效的会话ID");
            return;
        }

        Long userId = authenticate(session);
        if (userId == null) {
            closeWithError(session, "认证失败");
            return;
        }

        ChatSession chatSession = chatSessionMapper.selectOne(
                new LambdaQueryWrapper<ChatSession>()
                        .eq(ChatSession::getId, sessionId)
                        .eq(ChatSession::getUserId, userId)
        );
        if (chatSession == null) {
            closeWithError(session, "会话不存在");
            return;
        }

        WebSocketSession oldSession = activeSessions.put(sessionId, session);
        if (oldSession != null && oldSession.isOpen()) {
            oldSession.close(CloseStatus.NORMAL);
            log.info("踢掉旧连接: sessionId={}", sessionId);
        }

        session.getAttributes().put("userId", userId);
        session.getAttributes().put("sessionId", sessionId);

        log.info("WebSocket 连接建立: sessionId={}, userId={}", sessionId, userId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        Long sessionId = (Long) session.getAttributes().get("sessionId");
        Long userId = (Long) session.getAttributes().get("userId");

        if (sessionId == null || userId == null) {
            sendError(session, "连接未认证");
            return;
        }

        try {
            // 1. 解析客户端消息
            JSONObject json = JSONUtil.parseObj(message.getPayload());
            String content = json.getStr("content");
            if (content == null || content.isBlank()) {
                sendError(session, "消息内容不能为空");
                return;
            }

            // 2. 限流检查
            try {
                rateLimitService.checkRateLimit(userId);
            } catch (BusinessException e) {
                sendError(session, e.getMessage());
                return;
            }

            // 3. 保存用户消息到 DB
            ChatMessage userMsg = new ChatMessage();
            userMsg.setSessionId(sessionId);
            userMsg.setRole("user");
            userMsg.setContent(content);
            userMsg.setTokenCount(0);
            chatMessageMapper.insert(userMsg);

            // 4. 写入 Redis 上下文
            contextService.saveToContext(sessionId, "user", content);

            // 5. RAG 知识检索 + 构建上下文
            List<Message> history = contextService.buildContext(userId, sessionId, content);
            String knowledge = knowledgeService.retrieve(content);
            String systemPrompt = loadSystemPrompt(knowledge);

            // 6. 流式调用 LLM，逐 chunk 推送
            StringBuilder fullReply = new StringBuilder();
            llmService.streamChat(systemPrompt, history, content)
                    .doOnNext(chunk -> {
                        fullReply.append(chunk);
                        sendChunk(session, chunk);
                    })
                    .doOnComplete(() -> {
                        String replyText = fullReply.toString();

                        // 估算 token 用量（中文约 1.5 字符/token）
                        int estimatedTokens = estimateTokens(content, replyText);

                        // 推送完成标记（含 token 用量）
                        sendDone(session, estimatedTokens);

                        // 保存 assistant 消息到 DB
                        ChatMessage assistantMsg = new ChatMessage();
                        assistantMsg.setSessionId(sessionId);
                        assistantMsg.setRole("assistant");
                        assistantMsg.setContent(replyText);
                        assistantMsg.setTokenCount(estimatedTokens);
                        chatMessageMapper.insert(assistantMsg);

                        // 累加会话 token 总量
                        if (estimatedTokens > 0) {
                            chatSessionMapper.update(null,
                                    new LambdaUpdateWrapper<ChatSession>()
                                            .eq(ChatSession::getId, sessionId)
                                            .setSql("total_tokens = total_tokens + " + estimatedTokens)
                            );
                        }

                        // 写入 Redis 上下文
                        contextService.saveToContext(sessionId, "assistant", replyText);

                        // 更新会话标题（首条消息时）
                        updateSessionTitleIfNeeded(sessionId, content);

                        log.info("流式回复完成: sessionId={}, 长度={}, tokens={}", sessionId, replyText.length(), estimatedTokens);
                    })
                    .doOnError(e -> {
                        log.error("流式调用失败: sessionId={}, error={}", sessionId, e.getMessage(), e);
                        if (e instanceof BusinessException be) {
                            sendError(session, be.getMessage());
                        } else {
                            sendError(session, "AI 服务暂时不可用，请稍后重试");
                        }
                    })
                    .subscribe();

        } catch (Exception e) {
            log.error("处理消息失败: sessionId={}, error={}", sessionId, e.getMessage(), e);
            sendError(session, "消息处理失败");
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long sessionId = (Long) session.getAttributes().get("sessionId");
        if (sessionId != null) {
            activeSessions.remove(sessionId, session);
            log.info("WebSocket 连接关闭: sessionId={}, status={}", sessionId, status);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("WebSocket 传输错误: {}", exception.getMessage(), exception);
        sendError(session, "连接异常");
    }

    // ==================== 辅助方法 ====================

    /**
     * 估算 token 用量（prompt + completion）
     * 中文约 1.5 字符/token，英文约 4 字符/token
     */
    private int estimateTokens(String userMessage, String reply) {
        int userTokens = Math.max(1, userMessage.length() * 2 / 3);
        int replyTokens = Math.max(1, reply.length() * 2 / 3);
        return userTokens + replyTokens;
    }

    private Long extractSessionId(WebSocketSession session) {
        try {
            String path = session.getUri().getPath();
            String[] parts = path.split("/");
            return Long.parseLong(parts[parts.length - 1]);
        } catch (Exception e) {
            return null;
        }
    }

    private Long authenticate(WebSocketSession session) {
        try {
            String query = session.getUri().getQuery();
            if (query == null) return null;

            var params = UriComponentsBuilder.fromUriString("?" + query).build().getQueryParams();
            String token = params.getFirst("token");
            if (token == null || token.isBlank()) return null;

            Claims claims = JwtUtil.parseToken(token);
            return Long.parseLong(claims.getSubject());
        } catch (Exception e) {
            log.warn("WebSocket 认证失败: {}", e.getMessage());
            return null;
        }
    }

    private void sendChunk(WebSocketSession session, String chunk) {
        JSONObject json = new JSONObject();
        json.set("type", "chunk");
        json.set("content", chunk);
        sendMessage(session, json.toString());
    }

    private void sendDone(WebSocketSession session, int tokenCount) {
        JSONObject json = new JSONObject();
        json.set("type", "done");
        json.set("tokenCount", tokenCount);
        sendMessage(session, json.toString());
    }

    private void sendError(WebSocketSession session, String errorMsg) {
        JSONObject json = new JSONObject();
        json.set("type", "error");
        json.set("message", errorMsg);
        sendMessage(session, json.toString());
    }

    private void sendMessage(WebSocketSession session, String text) {
        try {
            if (session.isOpen()) {
                synchronized (session) {
                    session.sendMessage(new TextMessage(text));
                }
            }
        } catch (IOException e) {
            log.error("发送 WebSocket 消息失败: {}", e.getMessage());
        }
    }

    private void closeWithError(WebSocketSession session, String errorMsg) {
        sendError(session, errorMsg);
        try {
            session.close(CloseStatus.POLICY_VIOLATION);
        } catch (IOException e) {
            log.error("关闭 WebSocket 连接失败: {}", e.getMessage());
        }
    }

    private void updateSessionTitleIfNeeded(Long sessionId, String firstMessage) {
        ChatSession chatSession = chatSessionMapper.selectById(sessionId);
        if (chatSession != null && "新对话".equals(chatSession.getTitle())) {
            String title = firstMessage.length() > 50
                    ? firstMessage.substring(0, 50) + "..."
                    : firstMessage;
            chatSession.setTitle(title);
            chatSessionMapper.updateById(chatSession);
        }
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
