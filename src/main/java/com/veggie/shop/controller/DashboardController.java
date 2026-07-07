package com.veggie.shop.controller;

import com.veggie.shop.common.Result;
import com.veggie.shop.service.DailySummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 仪表盘 / 数据看板控制器
 */
@Tag(name = "数据看板", description = "首页经营概览和销售趋势")
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DailySummaryService dailySummaryService;

    @Operation(summary = "获取仪表盘数据（今日概览+近7天趋势+库存预警）")
    @GetMapping
    public Result<Map<String, Object>> getDashboard() {
        return Result.success(dailySummaryService.getDashboardData());
    }
}
