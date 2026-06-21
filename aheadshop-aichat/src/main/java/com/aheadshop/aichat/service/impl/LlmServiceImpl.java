package com.aheadshop.aichat.service.impl;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.aheadshop.aichat.domain.dto.ChatResult;
import com.aheadshop.common.core.exception.BusinessException;
import com.aheadshop.common.core.exception.BusinessExceptionCode;
import com.aheadshop.aichat.service.LlmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LlmServiceImpl implements LlmService {

    private final ChatClient chatClient;

    @Override
    public ChatResult chat(String systemPrompt, List<Message> history, String userMessage) {
        Entry entry = null;
        try {
            entry = SphU.entry("llmChat");

            List<Message> messages = buildMessages(systemPrompt, history, userMessage);

            ChatResponse response = chatClient.prompt()
                    .messages(messages)
                    .call()
                    .chatResponse();

            String content = response.getResult().getOutput().getText();
            int tokenCount = 0;
            if (response.getMetadata() != null && response.getMetadata().getUsage() != null) {
                tokenCount = (int) response.getMetadata().getUsage().getTotalTokens();
            }

            log.info("LLM 回复成功，长度: {}, tokens: {}", content != null ? content.length() : 0, tokenCount);
            return ChatResult.builder()
                    .content(content)
                    .tokenCount(tokenCount)
                    .build();
        } catch (BlockException e) {
            // Sentinel 熔断降级
            log.warn("LLM 调用被 Sentinel 熔断");
            throw new BusinessException(BusinessExceptionCode.LLM_CALL_FAILED, "系统繁忙，请稍后再试");
        } catch (Exception e) {
            log.error("LLM 调用失败: {}", e.getMessage(), e);
            throw mapLlmException(e);
        } finally {
            if (entry != null) {
                entry.exit();
            }
        }
    }

    @Override
    public Flux<String> streamChat(String systemPrompt, List<Message> history, String userMessage) {
        Entry entry = null;
        try {
            entry = SphU.entry("llmStreamChat");

            List<Message> messages = buildMessages(systemPrompt, history, userMessage);

            return chatClient.prompt()
                    .messages(messages)
                    .stream()
                    .content();
        } catch (BlockException e) {
            log.warn("LLM 流式调用被 Sentinel 熔断");
            throw new BusinessException(BusinessExceptionCode.LLM_CALL_FAILED, "系统繁忙，请稍后再试");
        } catch (Exception e) {
            log.error("LLM 流式调用失败: {}", e.getMessage(), e);
            throw mapLlmException(e);
        } finally {
            if (entry != null) {
                entry.exit();
            }
        }
    }

    private List<Message> buildMessages(String systemPrompt, List<Message> history, String userMessage) {
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(systemPrompt));
        if (history != null) {
            messages.addAll(history);
        }
        messages.add(new UserMessage(userMessage));
        return messages;
    }

    private BusinessException mapLlmException(Exception e) {
        String msg = e.getMessage();
        if (msg != null && msg.contains("timeout")) {
            return new BusinessException(BusinessExceptionCode.LLM_TIMEOUT, "AI 服务响应超时，请稍后重试");
        }
        if (msg != null && (msg.contains("401") || msg.contains("invalid") || msg.contains("api-key"))) {
            return new BusinessException(BusinessExceptionCode.LLM_INVALID_KEY, "AI 服务认证失败");
        }
        return new BusinessException(BusinessExceptionCode.LLM_CALL_FAILED, "AI 服务暂时不可用，请稍后重试");
    }
}
