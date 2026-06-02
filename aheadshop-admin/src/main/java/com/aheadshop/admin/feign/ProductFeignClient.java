package com.aheadshop.admin.feign;

import com.aheadshop.common.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@FeignClient(value = "aheadshop-product", path = "/product")
public interface ProductFeignClient {

    // ========== SPU ==========
    @GetMapping("/spu/list")
    Result<?> listSpu(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "brandId", required = false) Long brandId,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize);

    @GetMapping("/spu/{id}")
    Result<?> getSpuDetail(@PathVariable("id") Long id);

    @PostMapping("/spu")
    Result<Void> addSpu(@RequestBody Map<String, Object> dto);

    @PutMapping("/spu/{id}")
    Result<Void> updateSpu(@PathVariable("id") Long id, @RequestBody Map<String, Object> dto);

    @PutMapping("/spu/on-shelf/{id}")
    Result<Void> onShelf(@PathVariable("id") Long id);

    @PutMapping("/spu/off-shelf/{id}")
    Result<Void> offShelf(@PathVariable("id") Long id);

    // ========== Category ==========
    @GetMapping("/category/tree")
    Result<?> categoryTree();

    @PostMapping("/category")
    Result<Void> addCategory(@RequestBody Map<String, Object> dto);

    @PutMapping("/category")
    Result<Void> updateCategory(@RequestBody Map<String, Object> dto);

    @DeleteMapping("/category/{id}")
    Result<Void> deleteCategory(@PathVariable("id") Long id);

    // ========== Brand ==========
    @GetMapping("/brand/list")
    Result<?> listBrand(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize);

    @PostMapping("/brand")
    Result<Void> addBrand(@RequestBody Map<String, Object> dto);

    @PutMapping("/brand")
    Result<Void> updateBrand(@RequestBody Map<String, Object> dto);

    @DeleteMapping("/brand/{id}")
    Result<Void> deleteBrand(@PathVariable("id") Long id);

    // ========== 内部统计 ==========
    @GetMapping("/inner/product/hot-products")
    Result<List<Map<String, Object>>> hotProducts(@RequestParam(value = "limit", defaultValue = "10") Integer limit);
}
