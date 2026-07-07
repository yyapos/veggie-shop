package com.veggie.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.veggie.shop.common.Result;
import com.veggie.shop.entity.Product;
import com.veggie.shop.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品控制器
 */
@Tag(name = "商品管理", description = "商品的增删改查、上下架、库存预警")
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "分页查询商品")
    @GetMapping("/page")
    public Result<Page<Product>> page(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "商品名称（模糊搜索）") @RequestParam(required = false) String name,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "状态：1上架 0下架") @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(name)) {
            wrapper.like(Product::getName, name);
        }
        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        if (status != null) {
            wrapper.eq(Product::getStatus, status);
        }
        wrapper.orderByDesc(Product::getCreateTime);
        return Result.success(productService.page(new Page<>(page, size), wrapper));
    }

    @Operation(summary = "获取全部上架商品（用于销售选商品）")
    @GetMapping("/on-sale")
    public Result<List<Product>> getOnSale() {
        return Result.success(productService.getByStatus(1));
    }

    @Operation(summary = "获取低库存预警商品")
    @GetMapping("/warning-stock")
    public Result<List<Product>> getWarningStock() {
        return Result.success(productService.getWarningStockProducts());
    }

    @Operation(summary = "根据分类获取商品")
    @GetMapping("/by-category/{categoryId}")
    public Result<List<Product>> getByCategory(@Parameter(description = "分类ID") @PathVariable Long categoryId) {
        return Result.success(productService.getByCategoryId(categoryId));
    }

    @Operation(summary = "根据ID查询商品")
    @GetMapping("/{id}")
    public Result<Product> getById(@Parameter(description = "商品ID") @PathVariable Long id) {
        return Result.success(productService.getById(id));
    }

    @Operation(summary = "新增商品")
    @PostMapping
    public Result<Void> add(@RequestBody Product product) {
        productService.save(product);
        return Result.success();
    }

    @Operation(summary = "更新商品")
    @PutMapping
    public Result<Void> update(@RequestBody Product product) {
        productService.updateById(product);
        return Result.success();
    }

    @Operation(summary = "删除商品（逻辑删除）")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "商品ID") @PathVariable Long id) {
        productService.removeById(id);
        return Result.success();
    }

    @Operation(summary = "商品上架")
    @PutMapping("/put-on-sale/{id}")
    public Result<Void> putOnSale(@Parameter(description = "商品ID") @PathVariable Long id) {
        productService.putOnSale(id);
        return Result.success();
    }

    @Operation(summary = "商品下架")
    @PutMapping("/take-off-sale/{id}")
    public Result<Void> takeOffSale(@Parameter(description = "商品ID") @PathVariable Long id) {
        productService.takeOffSale(id);
        return Result.success();
    }
}
