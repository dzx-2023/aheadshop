package com.aheadshop.admin.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RefundPageVO {

    private Long id;
    private String refundNo;
    private String orderNo;
    private Long userId;
    private BigDecimal refundAmount;
    private String refundReason;
    private Integer refundType;
    private Integer status;
    private String auditRemark;
    private LocalDateTime createTime;
}
