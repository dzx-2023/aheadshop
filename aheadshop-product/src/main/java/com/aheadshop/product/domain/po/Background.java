package com.aheadshop.product.domain.po;

import com.aheadshop.common.mybatis.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("background")
public class Background extends BaseEntity {

    private String imageUrl;
    private Integer sortOrder;
    private Integer enabled;
}
