package com.xunqi.gulimall.paymentrecord.dao;

import com.xunqi.gulimall.paymentrecord.entity.PaymentRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 支付宝、微信支付记录表 Mapper 接口
 * </p>
 *
 * @author renjianbang
 * @since 2025-05-07
 */
@Mapper
public interface PaymentRecordMapper extends BaseMapper<PaymentRecord> {

}
