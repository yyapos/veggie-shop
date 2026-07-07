package com.veggie.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 销售记录
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sale_record")
@Schema(description = "销售记录")
public class SaleRecord extends BaseEntity {

    /** 现金 */
    public static final String SALE_CASH = "CASH";
    /** 赊账 */
    public static final String SALE_DEBT = "DEBT";

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "销售数量", example = "2.50")
    private BigDecimal quantity;

    @Schema(description = "销售单价", example = "3.50")
    private BigDecimal price;

    @Schema(description = "总金额", example = "8.75")
    private BigDecimal totalAmount;

    @Schema(description = "销售日期")
    private LocalDate saleDate;

    @Schema(description = "销售类型：CASH现金 DEBT赊账", example = "CASH")
    private String saleType;
}
