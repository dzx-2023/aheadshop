package com.aheadshop.product.domain.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SpuPageVO {

    private Long id;
    private String name;
    private String subtitle;
    private String mainImage;
    private String categoryName;
    private String brandName;
    private BigDecimal minPrice;
    private Integer sales;
    private Integer status;
}
