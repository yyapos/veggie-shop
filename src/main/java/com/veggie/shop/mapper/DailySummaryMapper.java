package com.veggie.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.veggie.shop.entity.DailySummary;
import org.apache.ibatis.annotations.Mapper;

/**
 * 每日汇总 Mapper
 */
@Mapper
public interface DailySummaryMapper extends BaseMapper<DailySummary> {
}
