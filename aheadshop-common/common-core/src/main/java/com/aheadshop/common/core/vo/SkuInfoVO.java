package com.aheadshop.common.core.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkuInfoVO {

    private Long id;
    private Long spuId;
    private String skuName;
    private String specs;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private String image;
    private Integer status;
    private String spuName;
    private String spuMainImage;
}
