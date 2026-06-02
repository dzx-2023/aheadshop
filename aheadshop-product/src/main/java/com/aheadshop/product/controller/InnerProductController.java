package com.aheadshop.product.controller;

import com.aheadshop.common.core.exception.BusinessException;
import com.aheadshop.common.core.exception.BusinessExceptionCode;
import com.aheadshop.common.core.result.Result;
import com.aheadshop.product.domain.dto.StockDTO;
import com.aheadshop.product.domain.po.Spu;
import com.aheadshop.product.domain.vo.SkuInfoVO;
import com.aheadshop.product.domain.vo.SpuDetailVO;
import com.aheadshop.product.service.ISkuService;
import com.aheadshop.product.service.ISpuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inner/product")
@RequiredArgsConstructor
@Tag(name = "商品内部接口（供其他微服务调用）")
public class InnerProductController {

    private final ISkuService skuService;
    private final ISpuService spuService;

    @Operation(summary = "SKU 详情")
    @GetMapping("/sku/{id}")
    public Result<SkuInfoVO> skuInfo(@PathVariable("id") Long id) {
        return Result.success(skuService.getSkuInfo(id));
    }

    @Operation(summary = "批量锁库存")
    @PostMapping("/sku/lock-stock")
    public Result<Void> lockStock(@RequestBody List<StockDTO> list) {
        for (StockDTO dto : list) {
            int rows = skuService.lockStock(dto.getSkuId(), dto.getQuantity());
            if (rows == 0) {
                throw new BusinessException(BusinessExceptionCode.STOCK_NOT_ENOUGH,
                        "SKU " + dto.getSkuId() + " 库存不足");
            }
        }
        return Result.success(null);
    }

    @Operation(summary = "批量释放库存")
    @PostMapping("/sku/unlock-stock")
    public Result<Void> unlockStock(@RequestBody List<StockDTO> list) {
        for (StockDTO dto : list) {
            skuService.unlockStock(dto.getSkuId(), dto.getQuantity());
        }
        return Result.success(null);
    }

    @Operation(summary = "确认扣减")
    @PostMapping("/sku/confirm-deduct")
    public Result<Void> confirmDeduct(@RequestBody List<StockDTO> list) {
        for (StockDTO dto : list) {
            skuService.confirmDeduct(dto.getSkuId(), dto.getQuantity());
        }
        return Result.success(null);
    }

    @Operation(summary = "SPU 信息")
    @GetMapping("/spu/{id}")
    public Result<SpuDetailVO> spuInfo(@PathVariable("id") Long id) {
        return Result.success(spuService.getDetail(id));
    }

    @Operation(summary = "热销商品排行")
    @GetMapping("/hot-products")
    public Result<List<Map<String, Object>>> hotProducts(
            @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        return Result.success(spuService.hotProducts(limit));
    }
}
