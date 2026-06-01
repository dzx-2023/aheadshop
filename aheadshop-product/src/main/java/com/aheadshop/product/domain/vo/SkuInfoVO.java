package com.aheadshop.product.domain.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
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
