package com.veggie.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.veggie.shop.common.GlobalExceptionHandler.BusinessException;
import com.veggie.shop.entity.Debtor;
import com.veggie.shop.mapper.DebtorMapper;
import com.veggie.shop.service.DebtorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 赊账人 Service 实现
 */
@Service
public class DebtorServiceImpl extends ServiceImpl<DebtorMapper, Debtor> implements DebtorService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addDebt(Long debtorId, BigDecimal amount) {
        Debtor debtor = this.getById(debtorId);
        if (debtor == null) {
            throw new BusinessException("赊账人不存在");
        }
        debtor.setTotalDebt(debtor.getTotalDebt().add(amount));
        debtor.setUnpaidAmount(debtor.getUnpaidAmount().add(amount));
        debtor.setStatus(1);
        this.updateById(debtor);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void repay(Long debtorId, BigDecimal amount) {
        Debtor debtor = this.getById(debtorId);
        if (debtor == null) {
            throw new BusinessException("赊账人不存在");
        }
        if (amount.compareTo(debtor.getUnpaidAmount()) > 0) {
            throw new BusinessException("还款金额不能超过未还金额");
        }
        debtor.setPaidAmount(debtor.getPaidAmount().add(amount));
        debtor.setUnpaidAmount(debtor.getUnpaidAmount().subtract(amount));
        // 如果全部还清，更新状态
        if (debtor.getUnpaidAmount().compareTo(BigDecimal.ZERO) == 0) {
            debtor.setStatus(0);
        }
        this.updateById(debtor);
    }
}
