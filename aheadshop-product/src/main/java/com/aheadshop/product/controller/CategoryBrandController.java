package com.aheadshop.product.controller;

import com.aheadshop.common.core.result.Result;
import com.aheadshop.product.domain.vo.BrandVO;
import com.aheadshop.product.service.ICategoryBrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product/category-brand")
@RequiredArgsConstructor
@Tag(name = "分类品牌关联接口")
public class CategoryBrandController {

    private final ICategoryBrandService categoryBrandService;

    @Operation(summary = "查询分类下的品牌列表")
    @GetMapping("/brands/{categoryId}")
    public Result<List<BrandVO>> brandsByCategory(@PathVariable("categoryId") Long categoryId) {
        return Result.success(categoryBrandService.listBrandsByCategoryId(categoryId));
    }

    @Operation(summary = "绑定分类与品牌")
    @PostMapping("/bind")
    public Result<Void> bind(@RequestParam Long categoryId, @RequestParam Long brandId) {
        categoryBrandService.bind(categoryId, brandId);
        return Result.success(null);
    }

    @Operation(summary = "解绑分类与品牌")
    @DeleteMapping("/unbind")
    public Result<Void> unbind(@RequestParam Long categoryId, @RequestParam Long brandId) {
        categoryBrandService.unbind(categoryId, brandId);
        return Result.success(null);
    }
}
