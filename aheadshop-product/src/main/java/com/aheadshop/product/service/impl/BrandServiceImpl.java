package com.aheadshop.product.service.impl;

import com.aheadshop.product.domain.po.Brand;
import com.aheadshop.product.domain.vo.BrandVO;
import com.aheadshop.product.mapper.BrandMapper;
import com.aheadshop.product.service.IBrandService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements IBrandService {

    @Override
    public IPage<BrandVO> page(int pageNum, int pageSize, String keyword) {
        LambdaQueryWrapper<Brand> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Brand::getName, keyword);
        }
        wrapper.orderByAsc(Brand::getSortOrder);

        IPage<Brand> page = this.page(new Page<>(pageNum, pageSize), wrapper);
        return page.convert(this::toVO);
    }

    @Override
    @Transactional
    public void addBrand(Brand brand) {
        brand.setStatus(1);
        this.save(brand);
    }

    @Override
    @Transactional
    public void updateBrand(Brand brand) {
        this.updateById(brand);
    }

    @Override
    @Transactional
    public void deleteBrand(Long id) {
        this.removeById(id);
    }

    private BrandVO toVO(Brand b) {
        return BrandVO.builder()
                .id(b.getId())
                .name(b.getName())
                .logo(b.getLogo())
                .description(b.getDescription())
                .sortOrder(b.getSortOrder())
                .build();
    }
}
