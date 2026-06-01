package com.aheadshop.product.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SpuSaveDTO {

    @NotBlank(message = "商品名称不能为空")
    private String name;

    private String subtitle;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    private Long brandId;

    private String mainImage;

    private String images;

    private String detail;

    @NotEmpty(message = "至少需要一个 SKU")
    private List<SkuDTO> skus;

    @Data
    public static class SkuDTO {

        @NotBlank(message = "SKU 名称不能为空")
        private String skuName;

        private String specs;

        @NotNull(message = "价格不能为空")
        private BigDecimal price;

        private BigDecimal originalPrice;

        @NotNull(message = "库存不能为空")
        private Integer stock;

        private String image;
    }
}
