package com.veggie.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.veggie.shop.entity.RepaymentRecord;
import com.veggie.shop.mapper.RepaymentRecordMapper;
import com.veggie.shop.service.DebtorService;
import com.veggie.shop.service.RepaymentRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 还款记录 Service 实现
 */
@Service
@RequiredArgsConstructor
public class RepaymentRecordServiceImpl extends ServiceImpl<RepaymentRecordMapper, RepaymentRecord> implements RepaymentRecordService {

    private final DebtorService debtorService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createRepayment(RepaymentRecord record) {
        if (record.getRepayDate() == null) {
            record.setRepayDate(LocalDate.now());
        }
        // 保存还款记录
        this.save(record);
        // 更新赊账人欠款
        debtorService.repay(record.getDebtorId(), record.getAmount());
    }

    @Override
    public List<RepaymentRecord> getByDebtorId(Long debtorId) {
        return this.list(new LambdaQueryWrapper<RepaymentRecord>()
                .eq(RepaymentRecord::getDebtorId, debtorId)
                .orderByDesc(RepaymentRecord::getRepayDate));
    }

    @Override
    public List<RepaymentRecord> getByDateRange(LocalDate startDate, LocalDate endDate) {
        return this.list(new LambdaQueryWrapper<RepaymentRecord>()
                .ge(RepaymentRecord::getRepayDate, startDate)
                .le(RepaymentRecord::getRepayDate, endDate)
                .orderByDesc(RepaymentRecord::getRepayDate));
    }
}
