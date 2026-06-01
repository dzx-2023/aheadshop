package com.aheadshop.product.controller;

import com.aheadshop.common.core.result.Result;
import com.aheadshop.product.domain.vo.SkuInfoVO;
import com.aheadshop.product.service.ISkuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product/sku")
@RequiredArgsConstructor
@Tag(name = "SKU 管理接口")
public class SkuController {

    private final ISkuService skuService;

    @Operation(summary = "SKU 详情")
    @GetMapping("/{id}")
    public Result<SkuInfoVO> detail(@PathVariable Long id) {
        return Result.success(skuService.getSkuInfo(id));
    }
}
