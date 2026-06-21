package com.aheadshop.aichat.domain.po;

import com.aheadshop.common.mybatis.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("chat_message")
public class ChatMessage extends BaseEntity {

    private Long sessionId;

    private String role;

    private String content;

    private Integer tokenCount;
}
