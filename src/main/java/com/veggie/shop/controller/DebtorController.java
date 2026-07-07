package com.veggie.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.veggie.shop.common.Result;
import com.veggie.shop.entity.Debtor;
import com.veggie.shop.service.DebtorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 赊账人控制器
 */
@Tag(name = "赊账人管理", description = "赊账人的增删改查")
@RestController
@RequestMapping("/api/debtor")
@RequiredArgsConstructor
public class DebtorController {

    private final DebtorService debtorService;

    @Operation(summary = "分页查询赊账人")
    @GetMapping("/page")
    public Result<Page<Debtor>> page(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "姓名（模糊搜索）") @RequestParam(required = false) String name) {
        LambdaQueryWrapper<Debtor> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like(Debtor::getName, name);
        }
        wrapper.orderByDesc(Debtor::getCreateTime);
        return Result.success(debtorService.page(new Page<>(page, size), wrapper));
    }

    @Operation(summary = "获取全部赊账人")
    @GetMapping("/all")
    public Result<java.util.List<Debtor>> getAll() {
        return Result.success(debtorService.list(new LambdaQueryWrapper<Debtor>()
                .eq(Debtor::getStatus, 1)
                .orderByAsc(Debtor::getName)));
    }

    @Operation(summary = "根据ID查询赊账人")
    @GetMapping("/{id}")
    public Result<Debtor> getById(@Parameter(description = "赊账人ID") @PathVariable Long id) {
        return Result.success(debtorService.getById(id));
    }

    @Operation(summary = "新增赊账人")
    @PostMapping
    public Result<Void> add(@RequestBody Debtor debtor) {
        debtorService.save(debtor);
        return Result.success();
    }

    @Operation(summary = "更新赊账人")
    @PutMapping
    public Result<Void> update(@RequestBody Debtor debtor) {
        debtorService.updateById(debtor);
        return Result.success();
    }

    @Operation(summary = "删除赊账人（逻辑删除）")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "赊账人ID") @PathVariable Long id) {
        debtorService.removeById(id);
        return Result.success();
    }
}
