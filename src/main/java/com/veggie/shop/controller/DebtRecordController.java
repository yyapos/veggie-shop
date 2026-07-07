package com.veggie.shop.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.veggie.shop.common.Result;
import com.veggie.shop.entity.DebtRecord;
import com.veggie.shop.service.DebtRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 赊账记录控制器
 */
@Tag(name = "赊账记录", description = "赊账记录的增删改查及还款标记")
@RestController
@RequestMapping("/api/debt")
@RequiredArgsConstructor
public class DebtRecordController {

    private final DebtRecordService debtRecordService;

    @Operation(summary = "分页查询赊账记录")
    @GetMapping("/page")
    public Result<Page<DebtRecord>> page(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "赊账人ID") @RequestParam(required = false) Long debtorId,
            @Parameter(description = "状态：0未还 1已还") @RequestParam(required = false) Integer status) {
        return Result.success(debtRecordService.page(new Page<>(page, size),
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<DebtRecord>()
                        .eq(debtorId != null, DebtRecord::getDebtorId, debtorId)
                        .eq(status != null, DebtRecord::getStatus, status)
                        .orderByDesc(DebtRecord::getDebtDate, DebtRecord::getCreateTime)));
    }

    @Operation(summary = "创建赊账记录")
    @PostMapping
    public Result<Void> create(@RequestBody DebtRecord record) {
        debtRecordService.createDebt(record);
        return Result.success();
    }

    @Operation(summary = "标记赊账为已还")
    @PutMapping("/mark-paid/{id}")
    public Result<Void> markAsPaid(@Parameter(description = "赊账记录ID") @PathVariable Long id) {
        debtRecordService.markAsPaid(id);
        return Result.success();
    }

    @Operation(summary = "根据赊账人查询未还记录")
    @GetMapping("/unpaid/{debtorId}")
    public Result<List<DebtRecord>> getUnpaidByDebtorId(@Parameter(description = "赊账人ID") @PathVariable Long debtorId) {
        return Result.success(debtRecordService.getUnpaidByDebtorId(debtorId));
    }

    @Operation(summary = "根据日期范围查询赊账记录")
    @GetMapping("/by-date")
    public Result<List<DebtRecord>> getByDate(
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(debtRecordService.getByDateRange(startDate, endDate));
    }

    @Operation(summary = "根据ID查询赊账记录")
    @GetMapping("/{id}")
    public Result<DebtRecord> getById(@Parameter(description = "赊账记录ID") @PathVariable Long id) {
        return Result.success(debtRecordService.getById(id));
    }

    @Operation(summary = "删除赊账记录（逻辑删除）")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "赊账记录ID") @PathVariable Long id) {
        debtRecordService.removeById(id);
        return Result.success();
    }
}
