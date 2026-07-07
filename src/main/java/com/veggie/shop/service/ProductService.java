package com.veggie.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.veggie.shop.entity.Product;

import java.util.List;

/**
 * 商品 Service
 */
public interface ProductService extends IService<Product> {

    /**
     * 商品上架
     */
    void putOnSale(Long id);

    /**
     * 商品下架
     */
    void takeOffSale(Long id);

    /**
     * 获取低库存预警商品列表
     */
    List<Product> getWarningStockProducts();

    /**
     * 根据分类ID获取商品列表
     */
    List<Product> getByCategoryId(Long categoryId);

    /**
     * 根据状态获取商品列表
     */
    List<Product> getByStatus(Integer status);
}
