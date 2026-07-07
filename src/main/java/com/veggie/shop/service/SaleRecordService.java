package com.veggie.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.veggie.shop.entity.SaleRecord;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 销售记录 Service
 */
public interface SaleRecordService extends IService<SaleRecord> {

    /**
     * 创建销售记录（自动扣减库存，使用 Redis 分布式锁防并发）
     */
    void createSale(SaleRecord record);

    /**
     * 根据日期范围查询销售记录
     */
    List<SaleRecord> getByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * 获取今日销售统计
     */
    Map<String, BigDecimal> getTodayStats();
}
