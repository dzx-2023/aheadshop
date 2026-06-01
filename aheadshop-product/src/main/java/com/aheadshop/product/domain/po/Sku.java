package com.aheadshop.product.domain.po;

import com.aheadshop.common.mybatis.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sku")
public class Sku extends BaseEntity {

    private Long spuId;
    private String skuName;
    private String specs;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private Integer lockedStock;
    private String image;
    private Integer status;
}
