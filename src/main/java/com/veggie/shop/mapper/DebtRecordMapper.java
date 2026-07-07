package com.veggie.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.veggie.shop.entity.DebtRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 赊账记录 Mapper
 */
@Mapper
public interface DebtRecordMapper extends BaseMapper<DebtRecord> {
}
