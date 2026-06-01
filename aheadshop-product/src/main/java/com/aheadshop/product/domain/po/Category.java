package com.aheadshop.product.domain.po;

import com.aheadshop.common.mybatis.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("category")
public class Category extends BaseEntity {

    private Long parentId;
    private String name;
    private String icon;
    private Integer level;
    private Integer sortOrder;
    private Integer status;
}
