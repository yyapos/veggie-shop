package com.veggie.shop.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.veggie.shop.common.GlobalExceptionHandler.BusinessException;
import com.veggie.shop.entity.Product;
import com.veggie.shop.entity.SaleRecord;
import com.veggie.shop.mapper.SaleRecordMapper;
import com.veggie.shop.service.ProductService;
import com.veggie.shop.service.SaleRecordService;
import com.veggie.shop.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 销售记录 Service 实现
 * <p>
 * 核心逻辑：创建销售记录时使用 Redis 分布式锁防止并发扣减库存
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SaleRecordServiceImpl extends ServiceImpl<SaleRecordMapper, SaleRecord> implements SaleRecordService {

    private final ProductService productService;
    private final RedisUtil redisUtil;

    private static final String STOCK_LOCK_PREFIX = "stock:lock:";
    private static final long LOCK_TIMEOUT = 10; // 锁超时10秒

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createSale(SaleRecord record) {
        Long productId = record.getProductId();
        String lockKey = STOCK_LOCK_PREFIX + productId;
        String lockValue = IdUtil.fastSimpleUUID();

        // 1. 尝试获取 Redis 分布式锁
        boolean locked = false;
        try {
            locked = redisUtil.tryLock(lockKey, lockValue, LOCK_TIMEOUT);
            if (!locked) {
                throw new BusinessException("系统繁忙，请稍后重试");
            }

            // 2. 检查商品
            Product product = productService.getById(productId);
            if (product == null) {
                throw new BusinessException("商品不存在");
            }
            if (product.getStatus() != 1) {
                throw new BusinessException("商品已下架，无法销售");
            }

            // 3. 检查库存
            BigDecimal currentStock = product.getStock();
            if (currentStock.compareTo(record.getQuantity()) < 0) {
                throw new BusinessException("库存不足，当前库存: " + currentStock + product.getUnit());
            }

            // 4. 计算总金额
            record.setTotalAmount(record.getPrice().multiply(record.getQuantity()).setScale(2, RoundingMode.HALF_UP));
            if (record.getSaleDate() == null) {
                record.setSaleDate(LocalDate.now());
            }

            // 5. 保存销售记录
            this.save(record);

            // 6. 扣减库存
            product.setStock(currentStock.subtract(record.getQuantity()));
            productService.updateById(product);

            log.info("销售成功: 商品={}, 数量={}{}, 金额={}元", product.getName(), record.getQuantity(), product.getUnit(), record.getTotalAmount());

        } finally {
            // 7. 释放分布式锁
            if (locked) {
                redisUtil.releaseLock(lockKey, lockValue);
            }
        }
    }

    @Override
    public List<SaleRecord> getByDateRange(LocalDate startDate, LocalDate endDate) {
        return this.list(new LambdaQueryWrapper<SaleRecord>()
                .ge(SaleRecord::getSaleDate, startDate)
                .le(SaleRecord::getSaleDate, endDate)
                .orderByDesc(SaleRecord::getSaleDate, SaleRecord::getCreateTime));
    }

    @Override
    public Map<String, BigDecimal> getTodayStats() {
        LocalDate today = LocalDate.now();
        List<SaleRecord> todaySales = this.list(new LambdaQueryWrapper<SaleRecord>()
                .eq(SaleRecord::getSaleDate, today));

        BigDecimal totalSales = todaySales.stream()
                .map(SaleRecord::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long orderCount = todaySales.size();

        BigDecimal cashSales = todaySales.stream()
                .filter(s -> SaleRecord.SALE_CASH.equals(s.getSaleType()))
                .map(SaleRecord::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal debtSales = todaySales.stream()
                .filter(s -> SaleRecord.SALE_DEBT.equals(s.getSaleType()))
                .map(SaleRecord::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> stats = new HashMap<>();
        stats.put("totalSales", totalSales);
        stats.put("cashSales", cashSales);
        stats.put("debtSales", debtSales);
        stats.put("orderCount", BigDecimal.valueOf(orderCount));
        return stats;
    }
}
