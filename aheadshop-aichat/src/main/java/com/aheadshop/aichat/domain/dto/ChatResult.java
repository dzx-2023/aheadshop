package com.aheadshop.aichat.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LLM 调用结果 —— 包含回复内容和 token 用量
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResult {

    /** AI 回复内容 */
    private String content;

    /** 本次调用消耗的 token 数（prompt + completion） */
    private int tokenCount;
}
