package com.aheadshop.order.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemVO {

    private Long skuId;
    private Long spuId;
    private String skuName;
    private String image;
    private BigDecimal price;
    private String specs;
    private Integer quantity;
    private Integer checked;
}
