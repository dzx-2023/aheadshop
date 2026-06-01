package com.aheadshop.product.controller;

import com.aheadshop.common.core.result.Result;
import com.aheadshop.product.domain.dto.SpuSaveDTO;
import com.aheadshop.product.domain.vo.SpuDetailVO;
import com.aheadshop.product.domain.vo.SpuPageVO;
import com.aheadshop.product.service.ISpuService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product/spu")
@RequiredArgsConstructor
@Tag(name = "SPU 管理接口")
public class SpuController {

    private final ISpuService spuService;

    @Operation(summary = "分页查询 SPU")
    @GetMapping("/list")
    public Result<IPage<SpuPageVO>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(spuService.queryPage(keyword, categoryId, brandId, pageNum, pageSize));
    }

    @Operation(summary = "SPU 详情（含 SKU 列表）")
    @GetMapping("/{id}")
    public Result<SpuDetailVO> detail(@PathVariable Long id) {
        return Result.success(spuService.getDetail(id));
    }

    @Operation(summary = "新增商品（SPU + SKU）")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody SpuSaveDTO dto) {
        spuService.saveSpu(dto);
        return Result.success(null);
    }

    @Operation(summary = "修改商品")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody SpuSaveDTO dto) {
        spuService.updateSpu(id, dto);
        return Result.success(null);
    }

    @Operation(summary = "上架")
    @PutMapping("/on-shelf/{id}")
    public Result<Void> onShelf(@PathVariable Long id) {
        spuService.onShelf(id);
        return Result.success(null);
    }

    @Operation(summary = "下架")
    @PutMapping("/off-shelf/{id}")
    public Result<Void> offShelf(@PathVariable Long id) {
        spuService.offShelf(id);
        return Result.success(null);
    }
}
