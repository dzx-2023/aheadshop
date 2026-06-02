package com.aheadshop.admin.controller;

import com.aheadshop.admin.feign.ProductFeignClient;
import com.aheadshop.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/product")
@RequiredArgsConstructor
@Tag(name = "管理端-商品管理")
public class AdminProductController {

    private final ProductFeignClient productFeignClient;

    // ========== SPU ==========

    @Operation(summary = "SPU 列表")
    @GetMapping("/spu/list")
    public Result<?> listSpu(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "brandId", required = false) Long brandId,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return productFeignClient.listSpu(keyword, categoryId, brandId, pageNum, pageSize);
    }

    @Operation(summary = "SPU 详情")
    @GetMapping("/spu/{id}")
    public Result<?> spuDetail(@PathVariable("id") Long id) {
        return productFeignClient.getSpuDetail(id);
    }

    @Operation(summary = "新增商品")
    @PostMapping("/spu")
    public Result<Void> addSpu(@RequestBody Map<String, Object> dto) {
        return productFeignClient.addSpu(dto);
    }

    @Operation(summary = "修改商品")
    @PutMapping("/spu/{id}")
    public Result<Void> updateSpu(@PathVariable("id") Long id, @RequestBody Map<String, Object> dto) {
        return productFeignClient.updateSpu(id, dto);
    }

    @Operation(summary = "上架")
    @PutMapping("/spu/on-shelf/{id}")
    public Result<Void> onShelf(@PathVariable("id") Long id) {
        return productFeignClient.onShelf(id);
    }

    @Operation(summary = "下架")
    @PutMapping("/spu/off-shelf/{id}")
    public Result<Void> offShelf(@PathVariable("id") Long id) {
        return productFeignClient.offShelf(id);
    }

    // ========== Category ==========

    @Operation(summary = "分类树")
    @GetMapping("/category/tree")
    public Result<?> categoryTree() {
        return productFeignClient.categoryTree();
    }

    @Operation(summary = "新增分类")
    @PostMapping("/category")
    public Result<Void> addCategory(@RequestBody Map<String, Object> dto) {
        return productFeignClient.addCategory(dto);
    }

    @Operation(summary = "修改分类")
    @PutMapping("/category")
    public Result<Void> updateCategory(@RequestBody Map<String, Object> dto) {
        return productFeignClient.updateCategory(dto);
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/category/{id}")
    public Result<Void> deleteCategory(@PathVariable("id") Long id) {
        return productFeignClient.deleteCategory(id);
    }

    // ========== Brand ==========

    @Operation(summary = "品牌列表")
    @GetMapping("/brand/list")
    public Result<?> listBrand(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return productFeignClient.listBrand(keyword, pageNum, pageSize);
    }

    @Operation(summary = "新增品牌")
    @PostMapping("/brand")
    public Result<Void> addBrand(@RequestBody Map<String, Object> dto) {
        return productFeignClient.addBrand(dto);
    }

    @Operation(summary = "修改品牌")
    @PutMapping("/brand")
    public Result<Void> updateBrand(@RequestBody Map<String, Object> dto) {
        return productFeignClient.updateBrand(dto);
    }

    @Operation(summary = "删除品牌")
    @DeleteMapping("/brand/{id}")
    public Result<Void> deleteBrand(@PathVariable("id") Long id) {
        return productFeignClient.deleteBrand(id);
    }
}
