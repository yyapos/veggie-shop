package com.veggie.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 赊账人
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("debtor")
@Schema(description = "赊账人")
public class Debtor extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "姓名", example = "张三")
    private String name;

    @Schema(description = "联系电话", example = "13800001111")
    private String phone;

    @Schema(description = "累计欠款", example = "150.00")
    private BigDecimal totalDebt;

    @Schema(description = "已还金额", example = "100.00")
    private BigDecimal paidAmount;

    @Schema(description = "未还金额", example = "50.00")
    private BigDecimal unpaidAmount;

    @Schema(description = "状态：1正常 0已结清", example = "1")
    private Integer status;
}
