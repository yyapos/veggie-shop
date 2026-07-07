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
 * 进货记录
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("purchase_record")
@Schema(description = "进货记录")
public class PurchaseRecord extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "进货数量", example = "100.00")
    private BigDecimal quantity;

    @Schema(description = "进货单价", example = "1.50")
    private BigDecimal costPrice;

    @Schema(description = "总成本", example = "150.00")
    private BigDecimal totalCost;

    @Schema(description = "批发市场名称", example = "城南批发市场")
    private String market;

    @Schema(description = "进货日期")
    private LocalDate purchaseDate;

    @Schema(description = "备注")
    private String remark;
}
