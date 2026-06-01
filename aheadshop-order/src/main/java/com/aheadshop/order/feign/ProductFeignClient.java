package com.aheadshop.order.feign;

import com.aheadshop.common.core.result.Result;
import com.aheadshop.common.core.vo.SkuInfoVO;
import com.aheadshop.order.domain.dto.StockDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "aheadshop-product", path = "/inner/product")
public interface ProductFeignClient {

    @GetMapping("/sku/{id}")
    Result<SkuInfoVO> getSkuInfo(@PathVariable("id") Long skuId);

    @PostMapping("/sku/lock-stock")
    Result<Void> lockStock(@RequestBody List<StockDTO> list);

    @PostMapping("/sku/unlock-stock")
    Result<Void> unlockStock(@RequestBody List<StockDTO> list);

    @PostMapping("/sku/confirm-deduct")
    Result<Void> confirmDeduct(@RequestBody List<StockDTO> list);
}
