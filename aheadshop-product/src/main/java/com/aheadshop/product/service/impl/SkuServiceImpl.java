package com.aheadshop.product.service.impl;

import com.aheadshop.common.core.exception.BusinessException;
import com.aheadshop.common.core.exception.BusinessExceptionCode;
import com.aheadshop.product.domain.po.Sku;
import com.aheadshop.product.domain.po.Spu;
import com.aheadshop.product.domain.vo.SkuInfoVO;
import com.aheadshop.product.mapper.SkuMapper;
import com.aheadshop.product.mapper.SpuMapper;
import com.aheadshop.product.service.ISkuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SkuServiceImpl extends ServiceImpl<SkuMapper, Sku> implements ISkuService {

    private final SpuMapper spuMapper;

    @Override
    public SkuInfoVO getSkuInfo(Long skuId) {
        Sku sku = this.getById(skuId);
        if (sku == null) {
            throw new BusinessException(BusinessExceptionCode.PRODUCT_NOT_FOUND, "SKU 不存在");
        }
        Spu spu = spuMapper.selectById(sku.getSpuId());
        return SkuInfoVO.builder()
                .id(sku.getId())
                .spuId(sku.getSpuId())
                .skuName(sku.getSkuName())
                .specs(sku.getSpecs())
                .price(sku.getPrice())
                .originalPrice(sku.getOriginalPrice())
                .stock(sku.getStock())
                .image(sku.getImage())
                .status(sku.getStatus())
                .spuName(spu != null ? spu.getName() : null)
                .spuMainImage(spu != null ? spu.getMainImage() : null)
                .build();
    }

    @Override
    public int lockStock(Long skuId, int quantity) {
        return baseMapper.lockStock(skuId, quantity);
    }

    @Override
    public int unlockStock(Long skuId, int quantity) {
        return baseMapper.unlockStock(skuId, quantity);
    }

    @Override
    public int confirmDeduct(Long skuId, int quantity) {
        return baseMapper.confirmDeduct(skuId, quantity);
    }
}
