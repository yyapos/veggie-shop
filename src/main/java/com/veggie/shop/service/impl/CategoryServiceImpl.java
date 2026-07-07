package com.veggie.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.veggie.shop.entity.Category;
import com.veggie.shop.mapper.CategoryMapper;
import com.veggie.shop.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品分类 Service 实现
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<Category> getCategoryTree() {
        // 查询所有启用的分类
        List<Category> all = this.list(new LambdaQueryWrapper<Category>()
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort));

        // 按 parentId 分组
        Map<Long, List<Category>> childrenMap = all.stream()
                .filter(c -> c.getParentId() != null && c.getParentId() > 0)
                .collect(Collectors.groupingBy(Category::getParentId));

        // 构造树结构：顶级分类包含子分类（通过扩展字段，此处简化返回平铺结构，前端自行处理层级）
        List<Category> tree = new ArrayList<>();
        for (Category category : all) {
            if (category.getParentId() == null || category.getParentId() == 0) {
                tree.add(category);
            }
        }
        return tree;
    }

    @Override
    public List<Category> getByParentId(Long parentId) {
        return this.list(new LambdaQueryWrapper<Category>()
                .eq(Category::getParentId, parentId)
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort));
    }
}
