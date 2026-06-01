package com.aheadshop.product.service;

import com.aheadshop.product.domain.po.Sku;
import com.aheadshop.product.domain.vo.SkuInfoVO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ISkuService extends IService<Sku> {

    SkuInfoVO getSkuInfo(Long skuId);

    int lockStock(Long skuId, int quantity);

    int unlockStock(Long skuId, int quantity);

    int confirmDeduct(Long skuId, int quantity);
}
