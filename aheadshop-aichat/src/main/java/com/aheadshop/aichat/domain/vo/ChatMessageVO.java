package com.aheadshop.aichat.domain.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatMessageVO {

    private Long id;

    private Long sessionId;

    private String role;

    private String content;

    private Integer tokenCount;

    private LocalDateTime createTime;
}
