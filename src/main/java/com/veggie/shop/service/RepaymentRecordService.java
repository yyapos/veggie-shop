package com.veggie.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.veggie.shop.entity.RepaymentRecord;

import java.time.LocalDate;
import java.util.List;

/**
 * 还款记录 Service
 */
public interface RepaymentRecordService extends IService<RepaymentRecord> {

    /**
     * 创建还款记录（同时更新赊账人欠款）
     */
    void createRepayment(RepaymentRecord record);

    /**
     * 根据赊账人查询还款记录
     */
    List<RepaymentRecord> getByDebtorId(Long debtorId);

    /**
     * 根据日期范围查询
     */
    List<RepaymentRecord> getByDateRange(LocalDate startDate, LocalDate endDate);
}
