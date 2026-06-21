package com.aheadshop.aichat.controller;

import com.aheadshop.aichat.domain.dto.ChatRequest;
import com.aheadshop.aichat.domain.dto.ChatResponse;
import com.aheadshop.aichat.domain.po.ChatMessage;
import com.aheadshop.aichat.domain.vo.ChatSessionVO;
import com.aheadshop.aichat.service.ChatService;
import com.aheadshop.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Tag(name = "AI客服", description = "智能对话接口")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/session")
    @Operation(summary = "创建会话", description = "创建一个新的 AI 客服对话会话")
    public Result<Long> createSession(
            @Parameter(description = "用户ID", required = true)
            @RequestHeader("X-User-Id") Long userId) {
        Long sessionId = chatService.createSession(userId);
        return Result.success(sessionId);
    }

    @PostMapping("/send")
    @Operation(summary = "发送消息", description = "向 AI 客服发送消息并获取回复")
    public Result<ChatResponse> sendMessage(
            @Parameter(description = "用户ID", required = true)
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody ChatRequest req) {
        ChatResponse response = chatService.sendMessage(userId, req);
        return Result.success(response);
    }

    @GetMapping("/sessions")
    @Operation(summary = "会话列表", description = "获取当前用户的会话列表，按更新时间倒序")
    public Result<List<ChatSessionVO>> getSessions(
            @Parameter(description = "用户ID", required = true)
            @RequestHeader("X-User-Id") Long userId) {
        List<ChatSessionVO> sessions = chatService.getSessions(userId);
        return Result.success(sessions);
    }

    @GetMapping("/session/{id}/messages")
    @Operation(summary = "消息列表", description = "获取指定会话的消息列表")
    public Result<List<ChatMessage>> getSessionMessages(
            @Parameter(description = "用户ID", required = true)
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "会话ID", required = true)
            @PathVariable("id") Long sessionId) {
        List<ChatMessage> messages = chatService.getSessionMessages(userId, sessionId);
        return Result.success(messages);
    }

    @DeleteMapping("/session/{id}")
    @Operation(summary = "删除会话", description = "逻辑删除会话及其所有消息")
    public Result<Void> deleteSession(
            @Parameter(description = "用户ID", required = true)
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "会话ID", required = true)
            @PathVariable("id") Long sessionId) {
        chatService.deleteSession(userId, sessionId);
        return Result.success();
    }
}
