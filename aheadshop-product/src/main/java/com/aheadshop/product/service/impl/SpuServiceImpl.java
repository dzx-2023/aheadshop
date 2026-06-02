package com.aheadshop.product.service.impl;

import cn.hutool.json.JSONUtil;
import com.aheadshop.common.core.exception.BusinessException;
import com.aheadshop.common.core.exception.BusinessExceptionCode;
import com.aheadshop.common.redis.util.CacheService;
import com.aheadshop.product.domain.dto.SpuSaveDTO;
import com.aheadshop.product.domain.po.Brand;
import com.aheadshop.product.domain.po.Category;
import com.aheadshop.product.domain.po.Sku;
import com.aheadshop.product.domain.po.Spu;
import com.aheadshop.product.domain.vo.SpuDetailVO;
import com.aheadshop.product.domain.vo.SpuPageVO;
import com.aheadshop.product.mapper.BrandMapper;
import com.aheadshop.product.mapper.CategoryMapper;
import com.aheadshop.product.mapper.SkuMapper;
import com.aheadshop.product.mapper.SpuMapper;
import com.aheadshop.product.service.ISpuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpuServiceImpl extends ServiceImpl<SpuMapper, Spu> implements ISpuService {

    private final SkuMapper skuMapper;
    private final CategoryMapper categoryMapper;
    private final BrandMapper brandMapper;
    private final CacheService cacheService;

    private static final String SPU_DETAIL_KEY = "product:spu:";
    private static final long CACHE_BASE_TTL = 10;
    private static final long CACHE_RANDOM_TTL = 5;
    private final Random random = new Random();

    @Override
    @Transactional
    public void saveSpu(SpuSaveDTO dto) {
        // 保存 SPU
        Spu spu = new Spu();
        spu.setName(dto.getName());
        spu.setSubtitle(dto.getSubtitle());
        spu.setCategoryId(dto.getCategoryId());
        spu.setBrandId(dto.getBrandId());
        spu.setMainImage(dto.getMainImage());
        spu.setImages(dto.getImages());
        spu.setDetail(dto.getDetail());
        spu.setStatus(0);
        spu.setSales(0);
        this.save(spu);

        // 批量保存 SKU
        for (SpuSaveDTO.SkuDTO skuDTO : dto.getSkus()) {
            Sku sku = new Sku();
            sku.setSpuId(spu.getId());
            sku.setSkuName(skuDTO.getSkuName());
            sku.setSpecs(skuDTO.getSpecs());
            sku.setPrice(skuDTO.getPrice());
            sku.setOriginalPrice(skuDTO.getOriginalPrice());
            sku.setStock(skuDTO.getStock());
            sku.setLockedStock(0);
            sku.setImage(skuDTO.getImage());
            sku.setStatus(1);
            skuMapper.insert(sku);
        }
    }

    @Override
    @Transactional
    public void updateSpu(Long spuId, SpuSaveDTO dto) {
        cacheService.delete(SPU_DETAIL_KEY + spuId);
        Spu spu = this.getById(spuId);
        if (spu == null) {
            throw new BusinessException(BusinessExceptionCode.PRODUCT_NOT_FOUND, "商品不存在");
        }
        spu.setName(dto.getName());
        spu.setSubtitle(dto.getSubtitle());
        spu.setCategoryId(dto.getCategoryId());
        spu.setBrandId(dto.getBrandId());
        spu.setMainImage(dto.getMainImage());
        spu.setImages(dto.getImages());
        spu.setDetail(dto.getDetail());
        this.updateById(spu);

        // 删除旧 SKU，重新插入
        skuMapper.delete(new LambdaQueryWrapper<Sku>().eq(Sku::getSpuId, spuId));
        for (SpuSaveDTO.SkuDTO skuDTO : dto.getSkus()) {
            Sku sku = new Sku();
            sku.setSpuId(spuId);
            sku.setSkuName(skuDTO.getSkuName());
            sku.setSpecs(skuDTO.getSpecs());
            sku.setPrice(skuDTO.getPrice());
            sku.setOriginalPrice(skuDTO.getOriginalPrice());
            sku.setStock(skuDTO.getStock());
            sku.setLockedStock(0);
            sku.setImage(skuDTO.getImage());
            sku.setStatus(1);
            skuMapper.insert(sku);
        }
    }

    @Override
    public void onShelf(Long spuId) {
        Spu spu = this.getById(spuId);
        if (spu == null) {
            throw new BusinessException(BusinessExceptionCode.PRODUCT_NOT_FOUND, "商品不存在");
        }
        spu.setStatus(1);
        this.updateById(spu);
        cacheService.delete(SPU_DETAIL_KEY + spuId);
    }

    @Override
    public void offShelf(Long spuId) {
        Spu spu = this.getById(spuId);
        if (spu == null) {
            throw new BusinessException(BusinessExceptionCode.PRODUCT_NOT_FOUND, "商品不存在");
        }
        spu.setStatus(0);
        this.updateById(spu);
        cacheService.delete(SPU_DETAIL_KEY + spuId);
    }

    @Override
    public IPage<SpuPageVO> queryPage(String keyword, Long categoryId, Long brandId, Integer status, int pageNum, int pageSize) {
        Page<SpuPageVO> page = new Page<>(pageNum, pageSize);
        if (categoryId != null) {
            // 收集该分类及其所有子孙分类的 ID
            List<Long> categoryIds = collectDescendantIds(categoryId);
            return baseMapper.selectSpuPageInCategories(page, keyword, categoryIds, brandId, status);
        }
        return baseMapper.selectSpuPage(page, keyword, null, brandId, status);
    }

    /**
     * 递归收集 categoryId 及其所有子孙分类的 ID
     */
    private List<Long> collectDescendantIds(Long categoryId) {
        List<Long> result = new ArrayList<>();
        result.add(categoryId);
        List<Category> children = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>().eq(Category::getParentId, categoryId)
        );
        for (Category child : children) {
            result.addAll(collectDescendantIds(child.getId()));
        }
        return result;
    }

    @Override
    public SpuDetailVO getDetail(Long spuId) {
        // 读缓存
        String cached = cacheService.get(SPU_DETAIL_KEY + spuId);
        if (StringUtils.hasText(cached)) {
            return JSONUtil.toBean(cached, SpuDetailVO.class);
        }

        Spu spu = this.getById(spuId);
        if (spu == null) {
            throw new BusinessException(BusinessExceptionCode.PRODUCT_NOT_FOUND, "商品不存在");
        }

        Category category = categoryMapper.selectById(spu.getCategoryId());
        Brand brand = spu.getBrandId() != null ? brandMapper.selectById(spu.getBrandId()) : null;

        List<Sku> skus = skuMapper.selectList(
                new LambdaQueryWrapper<Sku>().eq(Sku::getSpuId, spuId).orderByAsc(Sku::getId)
        );

        List<SpuDetailVO.SkuVO> skuVOs = skus.stream().map(s -> SpuDetailVO.SkuVO.builder()
                .id(s.getId())
                .skuName(s.getSkuName())
                .specs(s.getSpecs())
                .price(s.getPrice())
                .originalPrice(s.getOriginalPrice())
                .stock(s.getStock())
                .lockedStock(s.getLockedStock())
                .image(s.getImage())
                .status(s.getStatus())
                .build()
        ).collect(Collectors.toList());

        SpuDetailVO detail = SpuDetailVO.builder()
                .id(spu.getId())
                .name(spu.getName())
                .subtitle(spu.getSubtitle())
                .categoryId(spu.getCategoryId())
                .categoryName(category != null ? category.getName() : null)
                .brandId(spu.getBrandId())
                .brandName(brand != null ? brand.getName() : null)
                .mainImage(spu.getMainImage())
                .images(spu.getImages())
                .detail(spu.getDetail())
                .status(spu.getStatus())
                .sales(spu.getSales())
                .skus(skuVOs)
                .build();

        // 写缓存：10min + 随机 0-5min，防雪崩
        long ttl = CACHE_BASE_TTL + random.nextInt((int) CACHE_RANDOM_TTL);
        cacheService.setJson(SPU_DETAIL_KEY + spuId, detail, ttl, TimeUnit.MINUTES);
        return detail;
    }

    @Override
    public List<Map<String, Object>> hotProducts(int limit) {
        List<Spu> spus = this.list(new LambdaQueryWrapper<Spu>()
                .eq(Spu::getStatus, 1)
                .orderByDesc(Spu::getSales)
                .last("LIMIT " + limit));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Spu spu : spus) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", spu.getId());
            map.put("name", spu.getName());
            map.put("mainImage", spu.getMainImage());
            map.put("sales", spu.getSales());
            result.add(map);
        }
        return result;
    }
}
