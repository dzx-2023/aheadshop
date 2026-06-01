package com.aheadshop.cart.feign;

import com.aheadshop.common.core.result.Result;
import com.aheadshop.common.core.vo.SkuInfoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "aheadshop-product", path = "/inner/product")
public interface ProductFeignClient {

    @GetMapping("/sku/{id}")
    Result<SkuInfoVO> getSkuInfo(@PathVariable("id") Long skuId);
}
