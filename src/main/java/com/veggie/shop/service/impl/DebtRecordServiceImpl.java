package com.veggie.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.veggie.shop.common.GlobalExceptionHandler.BusinessException;
import com.veggie.shop.entity.DebtRecord;
import com.veggie.shop.mapper.DebtRecordMapper;
import com.veggie.shop.service.DebtRecordService;
import com.veggie.shop.service.DebtorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 赊账记录 Service 实现
 */
@Service
@RequiredArgsConstructor
public class DebtRecordServiceImpl extends ServiceImpl<DebtRecordMapper, DebtRecord> implements DebtRecordService {

    private final DebtorService debtorService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createDebt(DebtRecord record) {
        if (record.getDebtDate() == null) {
            record.setDebtDate(LocalDate.now());
        }
        record.setStatus(0); // 未还
        this.save(record);
        // 更新赊账人的欠款总额
        debtorService.addDebt(record.getDebtorId(), record.getAmount());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsPaid(Long id) {
        DebtRecord record = this.getById(id);
        if (record == null) {
            throw new BusinessException("赊账记录不存在");
        }
        if (record.getStatus() == 1) {
            throw new BusinessException("该记录已还款");
        }
        record.setStatus(1); // 已还
        this.updateById(record);
    }

    @Override
    public List<DebtRecord> getUnpaidByDebtorId(Long debtorId) {
        return this.list(new LambdaQueryWrapper<DebtRecord>()
                .eq(DebtRecord::getDebtorId, debtorId)
                .eq(DebtRecord::getStatus, 0)
                .orderByDesc(DebtRecord::getDebtDate));
    }

    @Override
    public List<DebtRecord> getByDateRange(LocalDate startDate, LocalDate endDate) {
        return this.list(new LambdaQueryWrapper<DebtRecord>()
                .ge(DebtRecord::getDebtDate, startDate)
                .le(DebtRecord::getDebtDate, endDate)
                .orderByDesc(DebtRecord::getDebtDate));
    }
}
