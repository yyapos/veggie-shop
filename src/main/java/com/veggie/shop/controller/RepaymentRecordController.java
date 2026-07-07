package com.veggie.shop.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.veggie.shop.common.Result;
import com.veggie.shop.entity.RepaymentRecord;
import com.veggie.shop.service.RepaymentRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 还款记录控制器
 */
@Tag(name = "还款记录", description = "还款记录的增删改查")
@RestController
@RequestMapping("/api/repayment")
@RequiredArgsConstructor
public class RepaymentRecordController {

    private final RepaymentRecordService repaymentRecordService;

    @Operation(summary = "分页查询还款记录")
    @GetMapping("/page")
    public Result<Page<RepaymentRecord>> page(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "赊账人ID") @RequestParam(required = false) Long debtorId) {
        return Result.success(repaymentRecordService.page(new Page<>(page, size),
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<RepaymentRecord>()
                        .eq(debtorId != null, RepaymentRecord::getDebtorId, debtorId)
                        .orderByDesc(RepaymentRecord::getRepayDate, RepaymentRecord::getCreateTime)));
    }

    @Operation(summary = "创建还款记录（自动更新赊账人欠款）")
    @PostMapping
    public Result<Void> create(@RequestBody RepaymentRecord record) {
        repaymentRecordService.createRepayment(record);
        return Result.success();
    }

    @Operation(summary = "根据赊账人查询还款记录")
    @GetMapping("/by-debtor/{debtorId}")
    public Result<List<RepaymentRecord>> getByDebtorId(@Parameter(description = "赊账人ID") @PathVariable Long debtorId) {
        return Result.success(repaymentRecordService.getByDebtorId(debtorId));
    }

    @Operation(summary = "根据日期范围查询还款记录")
    @GetMapping("/by-date")
    public Result<List<RepaymentRecord>> getByDate(
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(repaymentRecordService.getByDateRange(startDate, endDate));
    }

    @Operation(summary = "根据ID查询还款记录")
    @GetMapping("/{id}")
    public Result<RepaymentRecord> getById(@Parameter(description = "还款记录ID") @PathVariable Long id) {
        return Result.success(repaymentRecordService.getById(id));
    }

    @Operation(summary = "删除还款记录（逻辑删除）")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "还款记录ID") @PathVariable Long id) {
        repaymentRecordService.removeById(id);
        return Result.success();
    }
}
