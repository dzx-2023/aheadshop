package com.aheadshop.product.service.impl;

import com.aheadshop.common.core.exception.BusinessException;
import com.aheadshop.common.core.exception.BusinessExceptionCode;
import com.aheadshop.product.domain.po.Background;
import com.aheadshop.product.mapper.BackgroundMapper;
import com.aheadshop.product.service.IBackgroundService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackgroundServiceImpl implements IBackgroundService {

    private final BackgroundMapper backgroundMapper;

    @Value("${upload.background-dir:D:/Intelli J IDEA/IDEA 项目/aheadshop-plus/upload/background}")
    private String uploadDir;

    @Value("${upload.background-url-prefix:/upload/background/}")
    private String urlPrefix;

    @Override
    public List<Background> listAll() {
        return backgroundMapper.selectList(
                new LambdaQueryWrapper<Background>().orderByAsc(Background::getSortOrder));
    }

    @Override
    public List<Background> listActive() {
        return backgroundMapper.selectList(
                new LambdaQueryWrapper<Background>()
                        .eq(Background::getEnabled, 1)
                        .orderByAsc(Background::getSortOrder));
    }

    @Override
    public Background upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(BusinessExceptionCode.PARAM_ERROR, "文件不能为空");
        }

        // 生成文件名
        String originalName = file.getOriginalFilename();
        String ext = ".jpg";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString().replace("-", "") + ext;

        // 保存文件
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            file.transferTo(new File(dir, filename));
        } catch (IOException e) {
            log.error("背景图上传失败: {}", e.getMessage());
            throw new BusinessException(BusinessExceptionCode.SYSTEM_ERROR, "文件上传失败");
        }

        // 保存记录
        Background bg = new Background();
        bg.setImageUrl(urlPrefix + filename);
        bg.setSortOrder(0);
        bg.setEnabled(1);
        backgroundMapper.insert(bg);

        log.info("背景图上传成功: {}", bg.getImageUrl());
        return bg;
    }

    @Override
    public void deleteById(Long id) {
        Background bg = backgroundMapper.selectById(id);
        if (bg == null) {
            throw new BusinessException(BusinessExceptionCode.PARAM_ERROR, "背景图不存在");
        }

        // 删除文件
        String imageUrl = bg.getImageUrl();
        if (imageUrl != null && imageUrl.startsWith(urlPrefix)) {
            String filename = imageUrl.substring(urlPrefix.length());
            File file = new File(uploadDir, filename);
            if (file.exists()) {
                file.delete();
            }
        }

        backgroundMapper.deleteById(id);
        log.info("背景图已删除: id={}, url={}", id, imageUrl);
    }

    @Override
    public void updateSort(Long id, Integer sortOrder) {
        Background bg = backgroundMapper.selectById(id);
        if (bg == null) {
            throw new BusinessException(BusinessExceptionCode.PARAM_ERROR, "背景图不存在");
        }
        bg.setSortOrder(sortOrder);
        backgroundMapper.updateById(bg);
    }

    @Override
    public void updateEnabled(Long id, Integer enabled) {
        Background bg = backgroundMapper.selectById(id);
        if (bg == null) {
            throw new BusinessException(BusinessExceptionCode.PARAM_ERROR, "背景图不存在");
        }
        bg.setEnabled(enabled);
        backgroundMapper.updateById(bg);
    }
}
