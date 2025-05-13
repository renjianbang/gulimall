package com.xunqi.gulimall.refundrecord.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xunqi.gulimall.refundrecord.entity.RefundRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 支付宝、微信退款记录表 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2025-05-09
 */
@Mapper
public interface RefundRecordMapper extends BaseMapper<RefundRecord> {

}
