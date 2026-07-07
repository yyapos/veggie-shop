package com.veggie.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 商品
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product")
@Schema(description = "商品")
public class Product extends BaseEntity {

    /** 按斤计价 */
    public static final String PRICE_WEIGHT = "PRICE_WEIGHT";
    /** 按份计价 */
    public static final String PRICE_PER = "PRICE_PER";
    /** 按个计价 */
    public static final String PRICE_UNIT = "PRICE_UNIT";

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "所属分类ID")
    private Long categoryId;

    @Schema(description = "商品名称", example = "大白菜")
    private String name;

    @Schema(description = "计价方式：PRICE_WEIGHT按斤 PRICE_PER按份 PRICE_UNIT按个", example = "PRICE_WEIGHT")
    private String priceType;

    @Schema(description = "售价", example = "1.50")
    private BigDecimal price;

    @Schema(description = "进价/成本价", example = "0.80")
    private BigDecimal costPrice;

    @Schema(description = "库存", example = "50.00")
    private BigDecimal stock;

    @Schema(description = "单位", example = "斤")
    private String unit;

    @Schema(description = "商品图片URL")
    private String image;

    @Schema(description = "状态：1上架 0下架", example = "1")
    private Integer status;

    @Schema(description = "低库存预警阈值", example = "20.00")
    private BigDecimal warningStock;
}
