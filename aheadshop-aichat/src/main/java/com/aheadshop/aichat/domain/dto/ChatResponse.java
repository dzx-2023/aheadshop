package com.aheadshop.aichat.domain.dto;

import lombok.Data;

@Data
public class ChatResponse {

    private Long sessionId;

    private Long messageId;

    private String content;

    private Integer tokenCount;
}
