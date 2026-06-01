package com.aheadshop.product.service;

import com.aheadshop.product.domain.dto.SpuSaveDTO;
import com.aheadshop.product.domain.po.Spu;
import com.aheadshop.product.domain.vo.SpuDetailVO;
import com.aheadshop.product.domain.vo.SpuPageVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface ISpuService extends IService<Spu> {

    void saveSpu(SpuSaveDTO dto);

    void updateSpu(Long spuId, SpuSaveDTO dto);

    void onShelf(Long spuId);

    void offShelf(Long spuId);

    IPage<SpuPageVO> queryPage(String keyword, Long categoryId, Long brandId, int pageNum, int pageSize);

    SpuDetailVO getDetail(Long spuId);

    List<Map<String, Object>> hotProducts(int limit);
}
