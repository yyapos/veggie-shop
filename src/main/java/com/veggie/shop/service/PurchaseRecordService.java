package com.veggie.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.veggie.shop.entity.PurchaseRecord;

import java.time.LocalDate;
import java.util.List;

/**
 * 进货记录 Service
 */
public interface PurchaseRecordService extends IService<PurchaseRecord> {

    /**
     * 创建进货记录（同时更新库存）
     */
    void createPurchase(PurchaseRecord record);

    /**
     * 根据日期范围查询进货记录
     */
    List<PurchaseRecord> getByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * 根据商品ID查询进货记录
     */
    List<PurchaseRecord> getByProductId(Long productId);
}
