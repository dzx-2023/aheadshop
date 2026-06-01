package com.aheadshop.pay.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderInfoVO {

    private String orderNo;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private Integer status;
}
