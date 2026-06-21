package com.aheadshop.aichat.service;

import com.aheadshop.aichat.domain.dto.ChatResult;
import org.springframework.ai.chat.messages.Message;
import reactor.core.publisher.Flux;

import java.util.List;

public interface LlmService {

    /**
     * 同步对话，返回完整回复 + token 用量
     */
    ChatResult chat(String systemPrompt, List<Message> history, String userMessage);

    /**
     * 流式对话，逐 token 返回
     */
    Flux<String> streamChat(String systemPrompt, List<Message> history, String userMessage);
}
