package com.veggie.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.veggie.shop.common.GlobalExceptionHandler.BusinessException;
import com.veggie.shop.entity.Product;
import com.veggie.shop.mapper.ProductMapper;
import com.veggie.shop.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品 Service 实现
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Override
    public void putOnSale(Long id) {
        Product product = this.getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        product.setStatus(1);
        this.updateById(product);
    }

    @Override
    public void takeOffSale(Long id) {
        Product product = this.getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        product.setStatus(0);
        this.updateById(product);
    }

    @Override
    public List<Product> getWarningStockProducts() {
        // 库存 <= 预警阈值 且 已上架
        return this.list(new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 1)
                .apply("stock <= warning_stock"));
    }

    @Override
    public List<Product> getByCategoryId(Long categoryId) {
        return this.list(new LambdaQueryWrapper<Product>()
                .eq(Product::getCategoryId, categoryId)
                .eq(Product::getStatus, 1)
                .orderByAsc(Product::getName));
    }

    @Override
    public List<Product> getByStatus(Integer status) {
        return this.list(new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, status)
                .orderByAsc(Product::getName));
    }
}
