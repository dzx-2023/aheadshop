package com.aheadshop.product.mapper;

import com.aheadshop.product.domain.po.Spu;
import com.aheadshop.product.domain.vo.SpuPageVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SpuMapper extends BaseMapper<Spu> {

    IPage<SpuPageVO> selectSpuPage(Page<SpuPageVO> page, @Param("keyword") String keyword,
                                   @Param("categoryId") Long categoryId, @Param("brandId") Long brandId);
}
