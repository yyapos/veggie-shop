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
 * 赊账记录
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("debt_record")
@Schema(description = "赊账记录")
public class DebtRecord extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "赊账人ID")
    private Long debtorId;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "数量", example = "5.00")
    private BigDecimal quantity;

    @Schema(description = "赊账金额", example = "15.00")
    private BigDecimal amount;

    @Schema(description = "赊账日期")
    private LocalDate debtDate;

    @Schema(description = "状态：0未还 1已还", example = "0")
    private Integer status;
}
