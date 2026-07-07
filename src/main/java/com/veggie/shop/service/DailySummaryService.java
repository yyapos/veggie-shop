package com.veggie.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.veggie.shop.entity.DailySummary;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 每日汇总 Service
 */
public interface DailySummaryService extends IService<DailySummary> {

    /**
     * 生成或更新当日汇总
     */
    DailySummary generateTodaySummary();

    /**
     * 获取仪表盘数据（今日概览 + 近7天趋势）
     */
    Map<String, Object> getDashboardData();

    /**
     * 根据日期范围查询汇总
     */
    List<DailySummary> getByDateRange(LocalDate startDate, LocalDate endDate);
}
