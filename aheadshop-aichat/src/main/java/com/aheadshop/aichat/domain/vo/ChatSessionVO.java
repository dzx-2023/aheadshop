package com.aheadshop.aichat.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ChatSessionVO {

    private Long id;

    private String title;

    private Integer status;

    private LocalDateTime createTime;

    private String lastMessage;

    /** 会话累计 token 用量 */
    private Integer totalTokens;
}
