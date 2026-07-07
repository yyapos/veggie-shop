package com.veggie.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.veggie.shop.common.GlobalExceptionHandler.BusinessException;
import com.veggie.shop.entity.Product;
import com.veggie.shop.entity.PurchaseRecord;
import com.veggie.shop.mapper.PurchaseRecordMapper;
import com.veggie.shop.service.ProductService;
import com.veggie.shop.service.PurchaseRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 进货记录 Service 实现
 */
@Service
@RequiredArgsConstructor
public class PurchaseRecordServiceImpl extends ServiceImpl<PurchaseRecordMapper, PurchaseRecord> implements PurchaseRecordService {

    private final ProductService productService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createPurchase(PurchaseRecord record) {
        Product product = productService.getById(record.getProductId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        // 计算总成本
        record.setTotalCost(record.getCostPrice().multiply(record.getQuantity()));
        // 设置进货日期
        if (record.getPurchaseDate() == null) {
            record.setPurchaseDate(LocalDate.now());
        }
        // 保存进货记录
        this.save(record);
        // 更新库存
        product.setStock(product.getStock().add(record.getQuantity()));
        // 更新成本价（取最近进货价）
        product.setCostPrice(record.getCostPrice());
        productService.updateById(product);
    }

    @Override
    public List<PurchaseRecord> getByDateRange(LocalDate startDate, LocalDate endDate) {
        return this.list(new LambdaQueryWrapper<PurchaseRecord>()
                .ge(PurchaseRecord::getPurchaseDate, startDate)
                .le(PurchaseRecord::getPurchaseDate, endDate)
                .orderByDesc(PurchaseRecord::getPurchaseDate));
    }

    @Override
    public List<PurchaseRecord> getByProductId(Long productId) {
        return this.list(new LambdaQueryWrapper<PurchaseRecord>()
                .eq(PurchaseRecord::getProductId, productId)
                .orderByDesc(PurchaseRecord::getPurchaseDate));
    }
}
