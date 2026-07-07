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
 * 每日汇总
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("daily_summary")
@Schema(description = "每日汇总")
public class DailySummary extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "汇总日期")
    private LocalDate summaryDate;

    @Schema(description = "总销售额", example = "1500.00")
    private BigDecimal totalSales;

    @Schema(description = "总成本", example = "900.00")
    private BigDecimal totalCost;

    @Schema(description = "总利润", example = "600.00")
    private BigDecimal totalProfit;

    @Schema(description = "总赊账金额", example = "50.00")
    private BigDecimal totalDebt;

    @Schema(description = "订单数", example = "35")
    private Integer orderCount;
}
