package com.aheadshop.aichat.domain.po;

import com.aheadshop.common.mybatis.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("chat_session")
public class ChatSession extends BaseEntity {

    private Long userId;

    private String title;

    private Integer status;

    /** 会话累计 token 用量 */
    private Integer totalTokens;
}
