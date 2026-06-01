package com.aheadshop.product.mapper;

import com.aheadshop.product.domain.po.Sku;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SkuMapper extends BaseMapper<Sku> {

    int lockStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);

    int unlockStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);

    int confirmDeduct(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);
}
