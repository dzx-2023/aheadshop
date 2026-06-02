package com.aheadshop.order.domain.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemVO {

    private Long spuId;
    private Long skuId;
    private String skuName;
    private String skuImage;
    private String specs;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalPrice;
}
