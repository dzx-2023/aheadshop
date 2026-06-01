package com.aheadshop.product.domain.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CategoryTreeVO {

    private Long id;
    private Long parentId;
    private String name;
    private String icon;
    private Integer level;
    private Integer sortOrder;
    private List<CategoryTreeVO> children;
}
