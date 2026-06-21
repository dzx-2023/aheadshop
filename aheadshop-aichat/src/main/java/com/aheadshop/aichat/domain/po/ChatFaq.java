package com.aheadshop.aichat.domain.po;

import com.aheadshop.common.mybatis.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AI客服常见问题FAQ
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("chat_faq")
public class ChatFaq extends BaseEntity {

    /** 问题/关键词 */
    private String question;

    /** 标准回答 */
    private String answer;

    /** 匹配关键词，逗号分隔 */
    private String keywords;

    /** 排序权重，数值越大优先级越高 */
    private Integer sortOrder;

    /** 1-启用 0-禁用 */
    private Integer status;
}
