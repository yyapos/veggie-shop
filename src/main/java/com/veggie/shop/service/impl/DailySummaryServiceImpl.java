package com.veggie.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.veggie.shop.entity.DailySummary;
import com.veggie.shop.entity.SaleRecord;
import com.veggie.shop.mapper.DailySummaryMapper;
import com.veggie.shop.service.DailySummaryService;
import com.veggie.shop.service.SaleRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 每日汇总 Service 实现
 */
@Service
@RequiredArgsConstructor
public class DailySummaryServiceImpl extends ServiceImpl<DailySummaryMapper, DailySummary> implements DailySummaryService {

    private final SaleRecordService saleRecordService;

    @Override
    public DailySummary generateTodaySummary() {
        LocalDate today = LocalDate.now();

        // 查询今日汇总，存在则更新
        DailySummary summary = this.getOne(new LambdaQueryWrapper<DailySummary>()
                .eq(DailySummary::getSummaryDate, today));

        // 计算今日销售数据
        List<SaleRecord> todaySales = saleRecordService.getByDateRange(today, today);
        BigDecimal totalSales = BigDecimal.ZERO;
        BigDecimal totalDebt = BigDecimal.ZERO;
        for (SaleRecord sale : todaySales) {
            totalSales = totalSales.add(sale.getTotalAmount());
            if (SaleRecord.SALE_DEBT.equals(sale.getSaleType())) {
                totalDebt = totalDebt.add(sale.getTotalAmount());
            }
        }

        if (summary == null) {
            summary = new DailySummary();
            summary.setSummaryDate(today);
        }

        summary.setTotalSales(totalSales);
        summary.setTotalDebt(totalDebt);
        summary.setTotalCost(BigDecimal.ZERO); // 需要额外计算成本，暂设0
        summary.setTotalProfit(totalSales.subtract(summary.getTotalCost()));
        summary.setOrderCount(todaySales.size());

        this.saveOrUpdate(summary);
        return summary;
    }

    @Override
    public Map<String, Object> getDashboardData() {
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(6);
        LocalDate monthStart = today.withDayOfMonth(1);

        Map<String, Object> data = new HashMap<>();

        // 今日汇总
        DailySummary todaySummary = generateTodaySummary();
        data.put("today", todaySummary);

        // 近7天趋势
        List<DailySummary> weekList = this.getByDateRange(weekAgo, today);
        data.put("weekTrend", weekList);

        // 今日销售额
        Map<String, BigDecimal> todayStats = saleRecordService.getTodayStats();
        data.put("todayStats", todayStats);

        return data;
    }

    @Override
    public List<DailySummary> getByDateRange(LocalDate startDate, LocalDate endDate) {
        return this.list(new LambdaQueryWrapper<DailySummary>()
                .ge(DailySummary::getSummaryDate, startDate)
                .le(DailySummary::getSummaryDate, endDate)
                .orderByAsc(DailySummary::getSummaryDate));
    }
}
