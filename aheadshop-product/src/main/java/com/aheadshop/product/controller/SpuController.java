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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/product/spu")
@RequiredArgsConstructor
@Tag(name = "SPU 管理接口")
public class SpuController {

    private final ISpuService spuService;

    @Operation(summary = "分页查询 SPU")
    @GetMapping("/list")
    public Result<IPage<SpuPageVO>> list(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "brandId", required = false) Long brandId,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return Result.success(spuService.queryPage(keyword, categoryId, brandId, status, pageNum, pageSize));
    }

    @Operation(summary = "SPU 详情（含 SKU 列表）")
    @GetMapping("/{id}")
    public Result<SpuDetailVO> detail(@PathVariable("id") Long id) {
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
    public Result<Void> update(@PathVariable("id") Long id, @Valid @RequestBody SpuSaveDTO dto) {
        spuService.updateSpu(id, dto);
        return Result.success(null);
    }

    @Operation(summary = "上架")
    @PutMapping("/on-shelf/{id}")
    public Result<Void> onShelf(@PathVariable("id") Long id) {
        spuService.onShelf(id);
        return Result.success(null);
    }

    @Operation(summary = "下架")
    @PutMapping("/off-shelf/{id}")
    public Result<Void> offShelf(@PathVariable("id") Long id) {
        spuService.offShelf(id);
        return Result.success(null);
    }

    @Value("${upload.product-dir:D:/Intelli J IDEA/IDEA 项目/aheadshop-plus/upload/product}")
    private String uploadDir;

    @Operation(summary = "上传商品图片")
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";
        String filename = UUID.randomUUID().toString().replace("-", "") + ext;

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        file.transferTo(new File(dir, filename));

        return Result.success("/upload/product/" + filename);
    }
}
