package com.aheadshop.product.service;

import com.aheadshop.product.domain.po.Category;
import com.aheadshop.product.domain.vo.CategoryTreeVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ICategoryService extends IService<Category> {

    List<CategoryTreeVO> getCategoryTree();

    void addCategory(Category category);

    void updateCategory(Category category);

    void deleteCategory(Long id);
}
