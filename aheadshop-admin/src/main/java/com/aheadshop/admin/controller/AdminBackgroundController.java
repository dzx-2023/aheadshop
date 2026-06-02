package com.aheadshop.admin.controller;

import com.aheadshop.admin.feign.BackgroundFeignClient;
import com.aheadshop.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/background")
@RequiredArgsConstructor
@Tag(name = "后台-背景图管理")
public class AdminBackgroundController {

    private final BackgroundFeignClient backgroundFeignClient;

    @Operation(summary = "获取全部背景图列表")
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> listAll() {
        return backgroundFeignClient.listAll();
    }

    @Operation(summary = "删除背景图")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Long id) {
        return backgroundFeignClient.delete(id);
    }

    @Operation(summary = "更新排序")
    @PutMapping("/{id}/sort")
    public Result<Void> updateSort(@PathVariable("id") Long id,
                                   @RequestParam("sortOrder") Integer sortOrder) {
        return backgroundFeignClient.updateSort(id, sortOrder);
    }

    @Operation(summary = "启用/禁用")
    @PutMapping("/{id}/enabled")
    public Result<Void> updateEnabled(@PathVariable("id") Long id,
                                      @RequestParam("enabled") Integer enabled) {
        return backgroundFeignClient.updateEnabled(id, enabled);
    }
}
