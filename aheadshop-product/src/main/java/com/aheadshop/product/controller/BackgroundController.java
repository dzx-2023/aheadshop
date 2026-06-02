package com.aheadshop.product.controller;

import com.aheadshop.common.core.result.Result;
import com.aheadshop.product.domain.po.Background;
import com.aheadshop.product.service.IBackgroundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/product/background")
@RequiredArgsConstructor
@Tag(name = "背景图管理")
public class BackgroundController {

    private final IBackgroundService backgroundService;

    @Operation(summary = "获取启用的背景图列表（公开）")
    @GetMapping("/active")
    public Result<List<Background>> listActive() {
        return Result.success(backgroundService.listActive());
    }

    @Operation(summary = "获取全部背景图列表")
    @GetMapping("/list")
    public Result<List<Background>> listAll() {
        return Result.success(backgroundService.listAll());
    }

    @Operation(summary = "上传背景图")
    @PostMapping("/upload")
    public Result<Background> upload(@RequestParam("file") MultipartFile file) {
        return Result.success(backgroundService.upload(file));
    }

    @Operation(summary = "删除背景图")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Long id) {
        backgroundService.deleteById(id);
        return Result.success(null);
    }

    @Operation(summary = "更新排序")
    @PutMapping("/{id}/sort")
    public Result<Void> updateSort(@PathVariable("id") Long id,
                                   @RequestParam("sortOrder") Integer sortOrder) {
        backgroundService.updateSort(id, sortOrder);
        return Result.success(null);
    }

    @Operation(summary = "启用/禁用")
    @PutMapping("/{id}/enabled")
    public Result<Void> updateEnabled(@PathVariable("id") Long id,
                                      @RequestParam("enabled") Integer enabled) {
        backgroundService.updateEnabled(id, enabled);
        return Result.success(null);
    }
}
