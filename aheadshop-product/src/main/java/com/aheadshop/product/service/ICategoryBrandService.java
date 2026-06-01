package com.aheadshop.product.service;

import com.aheadshop.product.domain.po.CategoryBrand;
import com.aheadshop.product.domain.vo.BrandVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ICategoryBrandService extends IService<CategoryBrand> {

    List<BrandVO> listBrandsByCategoryId(Long categoryId);

    void bind(Long categoryId, Long brandId);

    void unbind(Long categoryId, Long brandId);
}
