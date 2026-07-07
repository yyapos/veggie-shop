package com.veggie.shop.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.veggie.shop.common.Result;
import com.veggie.shop.entity.SaleRecord;
import com.veggie.shop.service.SaleRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 销售记录控制器
 */
@Tag(name = "销售记账", description = "销售记录的增删改查，创建时自动扣减库存（Redis分布式锁）")
@RestController
@RequestMapping("/api/sale")
@RequiredArgsConstructor
public class SaleRecordController {

    private final SaleRecordService saleRecordService;

    @Operation(summary = "分页查询销售记录")
    @GetMapping("/page")
    public Result<Page<SaleRecord>> page(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "销售类型：CASH/DEBT") @RequestParam(required = false) String saleType) {
        return Result.success(saleRecordService.page(new Page<>(page, size),
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SaleRecord>()
                        .ge(startDate != null, SaleRecord::getSaleDate, startDate)
                        .le(endDate != null, SaleRecord::getSaleDate, endDate)
                        .eq(saleType != null && !saleType.isEmpty(), SaleRecord::getSaleType, saleType)
                        .orderByDesc(SaleRecord::getSaleDate, SaleRecord::getCreateTime)));
    }

    @Operation(summary = "创建销售记录（自动扣减库存+Redis分布式锁）")
    @PostMapping
    public Result<Void> create(@RequestBody SaleRecord record) {
        saleRecordService.createSale(record);
        return Result.success();
    }

    @Operation(summary = "获取今日销售统计")
    @GetMapping("/today-stats")
    public Result<Map<String, BigDecimal>> getTodayStats() {
        return Result.success(saleRecordService.getTodayStats());
    }

    @Operation(summary = "根据日期范围查询销售记录")
    @GetMapping("/by-date")
    public Result<List<SaleRecord>> getByDate(
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(saleRecordService.getByDateRange(startDate, endDate));
    }

    @Operation(summary = "根据ID查询销售记录")
    @GetMapping("/{id}")
    public Result<SaleRecord> getById(@Parameter(description = "销售记录ID") @PathVariable Long id) {
        return Result.success(saleRecordService.getById(id));
    }

    @Operation(summary = "删除销售记录（逻辑删除）")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "销售记录ID") @PathVariable Long id) {
        saleRecordService.removeById(id);
        return Result.success();
    }
}
