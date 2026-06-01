package com.aheadshop.product.service;

import com.aheadshop.product.domain.po.Brand;
import com.aheadshop.product.domain.vo.BrandVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IBrandService extends IService<Brand> {

    IPage<BrandVO> page(int pageNum, int pageSize, String keyword);

    void addBrand(Brand brand);

    void updateBrand(Brand brand);

    void deleteBrand(Long id);
}
