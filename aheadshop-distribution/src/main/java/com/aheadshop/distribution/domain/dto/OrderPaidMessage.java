package com.aheadshop.distribution.domain.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderPaidMessage {

    private String orderNo;
    private Long userId;
    private BigDecimal payAmount;
}
