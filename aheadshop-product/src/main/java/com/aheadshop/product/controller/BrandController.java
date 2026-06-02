package com.aheadshop.product.controller;

import com.aheadshop.common.core.result.Result;
import com.aheadshop.product.domain.po.Brand;
import com.aheadshop.product.domain.vo.BrandVO;
import com.aheadshop.product.service.IBrandService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product/brand")
@RequiredArgsConstructor
@Tag(name = "品牌管理接口")
public class BrandController {

    private final IBrandService brandService;

    @Operation(summary = "品牌分页列表")
    @GetMapping("/list")
    public Result<IPage<BrandVO>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        return Result.success(brandService.page(pageNum, pageSize, keyword));
    }

    @Operation(summary = "新增品牌")
    @PostMapping
    public Result<Void> add(@RequestBody Brand brand) {
        brandService.addBrand(brand);
        return Result.success(null);
    }

    @Operation(summary = "修改品牌")
    @PutMapping
    public Result<Void> update(@RequestBody Brand brand) {
        brandService.updateBrand(brand);
        return Result.success(null);
    }

    @Operation(summary = "删除品牌")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Long id) {
        brandService.deleteBrand(id);
        return Result.success(null);
    }
}
