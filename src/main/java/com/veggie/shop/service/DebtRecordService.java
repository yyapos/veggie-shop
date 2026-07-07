package com.veggie.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.veggie.shop.entity.DebtRecord;

import java.time.LocalDate;
import java.util.List;

/**
 * 赊账记录 Service
 */
public interface DebtRecordService extends IService<DebtRecord> {

    /**
     * 创建赊账记录
     */
    void createDebt(DebtRecord record);

    /**
     * 标记赊账为已还
     */
    void markAsPaid(Long id);

    /**
     * 根据赊账人查询未还记录
     */
    List<DebtRecord> getUnpaidByDebtorId(Long debtorId);

    /**
     * 根据日期范围查询
     */
    List<DebtRecord> getByDateRange(LocalDate startDate, LocalDate endDate);
}
