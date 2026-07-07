package com.veggie.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.veggie.shop.entity.Category;

import java.util.List;

/**
 * 商品分类 Service
 */
public interface CategoryService extends IService<Category> {

    /**
     * 获取分类树（含子分类）
     */
    List<Category> getCategoryTree();

    /**
     * 根据父级ID获取子分类
     */
    List<Category> getByParentId(Long parentId);
}
