package com.aheadshop.cart.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    private Long skuId;
    private Long spuId;
    private String skuName;
    private String image;
    private BigDecimal price;
    private String specs;
    private Integer quantity;
    private Integer checked;
}
