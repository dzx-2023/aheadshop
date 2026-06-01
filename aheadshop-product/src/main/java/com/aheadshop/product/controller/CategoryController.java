package com.aheadshop.product.controller;

import com.aheadshop.common.core.result.Result;
import com.aheadshop.product.domain.po.Category;
import com.aheadshop.product.domain.vo.CategoryTreeVO;
import com.aheadshop.product.service.ICategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product/category")
@RequiredArgsConstructor
@Tag(name = "分类管理接口")
public class CategoryController {

    private final ICategoryService categoryService;

    @Operation(summary = "获取分类树")
    @GetMapping("/tree")
    public Result<List<CategoryTreeVO>> tree() {
        return Result.success(categoryService.getCategoryTree());
    }

    @Operation(summary = "新增分类")
    @PostMapping
    public Result<Void> add(@RequestBody Category category) {
        categoryService.addCategory(category);
        return Result.success(null);
    }

    @Operation(summary = "修改分类")
    @PutMapping
    public Result<Void> update(@RequestBody Category category) {
        categoryService.updateCategory(category);
        return Result.success(null);
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success(null);
    }
}
