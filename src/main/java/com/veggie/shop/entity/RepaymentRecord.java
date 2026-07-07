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
 * 还款记录
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("repayment_record")
@Schema(description = "还款记录")
public class RepaymentRecord extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "赊账人ID")
    private Long debtorId;

    @Schema(description = "还款金额", example = "50.00")
    private BigDecimal amount;

    @Schema(description = "还款日期")
    private LocalDate repayDate;

    @Schema(description = "备注")
    private String remark;
}
