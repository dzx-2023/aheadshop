package com.aheadshop.order.domain.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SubmitOrderVO {

    private String orderNo;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
}
