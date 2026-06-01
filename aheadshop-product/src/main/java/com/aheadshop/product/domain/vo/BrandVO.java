package com.aheadshop.product.domain.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BrandVO {

    private Long id;
    private String name;
    private String logo;
    private String description;
    private Integer sortOrder;
}
