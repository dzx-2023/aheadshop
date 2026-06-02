package com.aheadshop.product.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewDTO {

    @NotNull(message = "SPU ID 不能为空")
    private Long spuId;

    @NotNull(message = "SKU ID 不能为空")
    private Long skuId;

    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分 1-5")
    @Max(value = 5, message = "评分 1-5")
    private Integer score;

    private String content;

    private String images;

    private Integer isAnonymous;
}
