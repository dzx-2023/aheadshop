package com.aheadshop.product.service.impl;

import cn.hutool.json.JSONUtil;
import com.aheadshop.common.core.exception.BusinessException;
import com.aheadshop.common.core.exception.BusinessExceptionCode;
import com.aheadshop.common.redis.util.CacheService;
import com.aheadshop.product.domain.po.Category;
import com.aheadshop.product.domain.vo.CategoryTreeVO;
import com.aheadshop.product.mapper.CategoryMapper;
import com.aheadshop.product.service.ICategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

    private final CacheService cacheService;

    private static final String CATEGORY_TREE_KEY = "product:category:tree";
    private static final long CACHE_TTL = 1;

    @Override
    public List<CategoryTreeVO> getCategoryTree() {
        // 读缓存
        String cached = cacheService.get(CATEGORY_TREE_KEY);
        if (StringUtils.hasText(cached)) {
            return JSONUtil.toList(cached, CategoryTreeVO.class);
        }

        // 查数据库
        List<Category> all = this.list(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getStatus, 1)
                        .orderByAsc(Category::getSortOrder)
        );

        // 构建树
        List<CategoryTreeVO> tree = buildTree(all);

        // 写缓存
        cacheService.setJson(CATEGORY_TREE_KEY, tree, CACHE_TTL, TimeUnit.HOURS);
        return tree;
    }

    @Override
    @Transactional
    public void addCategory(Category category) {
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }
        // 根据 parentId 自动计算 level
        if (category.getParentId() == 0) {
            category.setLevel(1);
        } else {
            Category parent = this.getById(category.getParentId());
            if (parent == null) {
                throw new BusinessException(BusinessExceptionCode.PARAM_ERROR, "父分类不存在");
            }
            category.setLevel(parent.getLevel() + 1);
        }
        category.setStatus(1);
        this.save(category);
        cacheService.delete(CATEGORY_TREE_KEY);
    }

    @Override
    @Transactional
    public void updateCategory(Category category) {
        this.updateById(category);
        cacheService.delete(CATEGORY_TREE_KEY);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        // 检查是否有子分类
        long childCount = this.count(
                new LambdaQueryWrapper<Category>().eq(Category::getParentId, id)
        );
        if (childCount > 0) {
            throw new BusinessException(BusinessExceptionCode.PARAM_ERROR, "存在子分类，无法删除");
        }
        this.removeById(id);
        cacheService.delete(CATEGORY_TREE_KEY);
    }

    private List<CategoryTreeVO> buildTree(List<Category> all) {
        Map<Long, List<Category>> grouped = all.stream()
                .collect(Collectors.groupingBy(Category::getParentId));

        List<Category> roots = grouped.getOrDefault(0L, new ArrayList<>());
        return roots.stream()
                .map(root -> toTreeVO(root, grouped))
                .collect(Collectors.toList());
    }

    private CategoryTreeVO toTreeVO(Category cat, Map<Long, List<Category>> grouped) {
        List<Category> children = grouped.getOrDefault(cat.getId(), new ArrayList<>());
        List<CategoryTreeVO> childVOs = children.stream()
                .map(child -> toTreeVO(child, grouped))
                .collect(Collectors.toList());
        return CategoryTreeVO.builder()
                .id(cat.getId())
                .parentId(cat.getParentId())
                .name(cat.getName())
                .icon(cat.getIcon())
                .level(cat.getLevel())
                .sortOrder(cat.getSortOrder())
                .children(childVOs)
                .build();
    }
}
