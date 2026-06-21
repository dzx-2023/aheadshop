package com.aheadshop.aichat.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品摘要 —— 用于 RAG 知识上下文
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSummary {

    private Long id;
    private String name;
    private String subtitle;
    private String categoryName;
    private String brandName;
    private Double minPrice;
    private Integer sales;
}
