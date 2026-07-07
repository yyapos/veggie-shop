package com.veggie.shop.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.veggie.shop.common.Result;
import com.veggie.shop.entity.PurchaseRecord;
import com.veggie.shop.service.PurchaseRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 进货记录控制器
 */
@Tag(name = "进货管理", description = "进货记录的增删改查")
@RestController
@RequestMapping("/api/purchase")
@RequiredArgsConstructor
public class PurchaseRecordController {

    private final PurchaseRecordService purchaseRecordService;

    @Operation(summary = "分页查询进货记录")
    @GetMapping("/page")
    public Result<Page<PurchaseRecord>> page(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(purchaseRecordService.page(new Page<>(page, size),
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PurchaseRecord>()
                        .ge(startDate != null, PurchaseRecord::getPurchaseDate, startDate)
                        .le(endDate != null, PurchaseRecord::getPurchaseDate, endDate)
                        .orderByDesc(PurchaseRecord::getPurchaseDate)));
    }

    @Operation(summary = "创建进货记录（自动更新库存）")
    @PostMapping
    public Result<Void> create(@RequestBody PurchaseRecord record) {
        purchaseRecordService.createPurchase(record);
        return Result.success();
    }

    @Operation(summary = "根据ID查询进货记录")
    @GetMapping("/{id}")
    public Result<PurchaseRecord> getById(@Parameter(description = "进货记录ID") @PathVariable Long id) {
        return Result.success(purchaseRecordService.getById(id));
    }

    @Operation(summary = "根据商品ID查询进货记录")
    @GetMapping("/by-product/{productId}")
    public Result<List<PurchaseRecord>> getByProductId(@Parameter(description = "商品ID") @PathVariable Long productId) {
        return Result.success(purchaseRecordService.getByProductId(productId));
    }

    @Operation(summary = "更新进货记录")
    @PutMapping
    public Result<Void> update(@RequestBody PurchaseRecord record) {
        purchaseRecordService.updateById(record);
        return Result.success();
    }

    @Operation(summary = "删除进货记录（逻辑删除）")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "进货记录ID") @PathVariable Long id) {
        purchaseRecordService.removeById(id);
        return Result.success();
    }
}
