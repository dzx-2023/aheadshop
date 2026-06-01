package com.aheadshop.product.mapper;

import com.aheadshop.product.domain.po.Review;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReviewMapper extends BaseMapper<Review> {

    Double selectAvgScore(@Param("spuId") Long spuId);
}
