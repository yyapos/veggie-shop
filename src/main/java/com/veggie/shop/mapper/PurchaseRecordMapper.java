package com.veggie.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.veggie.shop.entity.PurchaseRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 进货记录 Mapper
 */
@Mapper
public interface PurchaseRecordMapper extends BaseMapper<PurchaseRecord> {
}
