package com.veggie.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品分类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("category")
@Schema(description = "商品分类")
public class Category extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "分类名称", example = "蔬菜")
    private String name;

    @Schema(description = "图标emoji", example = "🥬")
    private String icon;

    @Schema(description = "父级分类ID，0为顶级", example = "0")
    private Long parentId;

    @Schema(description = "排序值", example = "1")
    private Integer sort;

    @Schema(description = "状态：1启用 0禁用", example = "1")
    private Integer status;
}
