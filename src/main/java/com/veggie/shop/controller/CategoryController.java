package com.veggie.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.veggie.shop.common.Result;
import com.veggie.shop.entity.Category;
import com.veggie.shop.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品分类控制器
 */
@Tag(name = "商品分类", description = "商品分类的增删改查")
@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "获取分类树")
    @GetMapping("/tree")
    public Result<List<Category>> getTree() {
        return Result.success(categoryService.getCategoryTree());
    }

    @Operation(summary = "根据父级ID获取子分类")
    @GetMapping("/children/{parentId}")
    public Result<List<Category>> getChildren(@Parameter(description = "父级分类ID") @PathVariable Long parentId) {
        return Result.success(categoryService.getByParentId(parentId));
    }

    @Operation(summary = "分页查询分类")
    @GetMapping("/page")
    public Result<Page<Category>> page(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") int size) {
        return Result.success(categoryService.page(new Page<>(page, size),
                new LambdaQueryWrapper<Category>().orderByAsc(Category::getSort)));
    }

    @Operation(summary = "根据ID查询分类")
    @GetMapping("/{id}")
    public Result<Category> getById(@Parameter(description = "分类ID") @PathVariable Long id) {
        return Result.success(categoryService.getById(id));
    }

    @Operation(summary = "新增分类")
    @PostMapping
    public Result<Void> add(@RequestBody Category category) {
        categoryService.save(category);
        return Result.success();
    }

    @Operation(summary = "更新分类")
    @PutMapping
    public Result<Void> update(@RequestBody Category category) {
        categoryService.updateById(category);
        return Result.success();
    }

    @Operation(summary = "删除分类（逻辑删除）")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "分类ID") @PathVariable Long id) {
        categoryService.removeById(id);
        return Result.success();
    }
}
