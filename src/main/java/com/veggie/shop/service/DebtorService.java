package com.veggie.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.veggie.shop.entity.Debtor;

/**
 * 赊账人 Service
 */
public interface DebtorService extends IService<Debtor> {

    /**
     * 更新赊账金额（增加欠款）
     */
    void addDebt(Long debtorId, java.math.BigDecimal amount);

    /**
     * 记录还款（减少欠款）
     */
    void repay(Long debtorId, java.math.BigDecimal amount);
}
