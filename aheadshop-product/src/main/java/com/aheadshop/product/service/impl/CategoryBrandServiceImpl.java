package com.aheadshop.product.service.impl;

import com.aheadshop.common.core.exception.BusinessException;
import com.aheadshop.common.core.exception.BusinessExceptionCode;
import com.aheadshop.product.domain.po.Brand;
import com.aheadshop.product.domain.po.CategoryBrand;
import com.aheadshop.product.domain.vo.BrandVO;
import com.aheadshop.product.mapper.BrandMapper;
import com.aheadshop.product.mapper.CategoryBrandMapper;
import com.aheadshop.product.service.ICategoryBrandService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryBrandServiceImpl extends ServiceImpl<CategoryBrandMapper, CategoryBrand> implements ICategoryBrandService {

    private final BrandMapper brandMapper;

    @Override
    public List<BrandVO> listBrandsByCategoryId(Long categoryId) {
        List<CategoryBrand> links = this.list(
                new LambdaQueryWrapper<CategoryBrand>().eq(CategoryBrand::getCategoryId, categoryId)
        );
        if (links.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> brandIds = links.stream().map(CategoryBrand::getBrandId).collect(Collectors.toList());
        List<Brand> brands = brandMapper.selectBatchIds(brandIds);
        return brands.stream().map(b -> BrandVO.builder()
                .id(b.getId())
                .name(b.getName())
                .logo(b.getLogo())
                .description(b.getDescription())
                .sortOrder(b.getSortOrder())
                .build()
        ).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void bind(Long categoryId, Long brandId) {
        long count = this.count(new LambdaQueryWrapper<CategoryBrand>()
                .eq(CategoryBrand::getCategoryId, categoryId)
                .eq(CategoryBrand::getBrandId, brandId));
        if (count > 0) {
            return; // 已绑定，幂等
        }
        CategoryBrand cb = new CategoryBrand();
        cb.setCategoryId(categoryId);
        cb.setBrandId(brandId);
        this.save(cb);
    }

    @Override
    @Transactional
    public void unbind(Long categoryId, Long brandId) {
        this.remove(new LambdaQueryWrapper<CategoryBrand>()
                .eq(CategoryBrand::getCategoryId, categoryId)
                .eq(CategoryBrand::getBrandId, brandId));
    }
}
