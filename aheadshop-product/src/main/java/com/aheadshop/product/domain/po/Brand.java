package com.aheadshop.product.domain.po;

import com.aheadshop.common.mybatis.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("brand")
public class Brand extends BaseEntity {

    private String name;
    private String logo;
    private String description;
    private Integer sortOrder;
    private Integer status;
}
