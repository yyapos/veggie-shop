package com.veggie.shop.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.veggie.shop.common.Result;
import com.veggie.shop.entity.DailySummary;
import com.veggie.shop.service.DailySummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 每日汇总控制器
 */
@Tag(name = "每日汇总", description = "每日经营数据汇总")
@RestController
@RequestMapping("/api/summary")
@RequiredArgsConstructor
public class DailySummaryController {

    private final DailySummaryService dailySummaryService;

    @Operation(summary = "生成/更新今日汇总")
    @PostMapping("/generate-today")
    public Result<DailySummary> generateToday() {
        return Result.success(dailySummaryService.generateTodaySummary());
    }

    @Operation(summary = "分页查询汇总记录")
    @GetMapping("/page")
    public Result<Page<DailySummary>> page(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "31") int size) {
        return Result.success(dailySummaryService.page(new Page<>(page, size),
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<DailySummary>()
                        .orderByDesc(DailySummary::getSummaryDate)));
    }

    @Operation(summary = "根据日期范围查询汇总")
    @GetMapping("/by-date")
    public Result<List<DailySummary>> getByDate(
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(dailySummaryService.getByDateRange(startDate, endDate));
    }

    @Operation(summary = "根据ID查询汇总")
    @GetMapping("/{id}")
    public Result<DailySummary> getById(@Parameter(description = "汇总ID") @PathVariable Long id) {
        return Result.success(dailySummaryService.getById(id));
    }
}
