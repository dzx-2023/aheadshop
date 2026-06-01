package com.aheadshop.product.domain.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class SpuDetailVO {

    private Long id;
    private String name;
    private String subtitle;
    private Long categoryId;
    private String categoryName;
    private Long brandId;
    private String brandName;
    private String mainImage;
    private String images;
    private String detail;
    private Integer status;
    private Integer sales;
    private List<SkuVO> skus;

    @Data
    @Builder
    public static class SkuVO {
        private Long id;
        private String skuName;
        private String specs;
        private BigDecimal price;
        private BigDecimal originalPrice;
        private Integer stock;
        private Integer lockedStock;
        private String image;
        private Integer status;
    }
}
