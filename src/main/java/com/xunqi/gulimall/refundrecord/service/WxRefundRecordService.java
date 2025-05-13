package com.xunqi.gulimall.refundrecord.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xunqi.gulimall.common.enmus.OrderStatus;
import com.xunqi.gulimall.refundrecord.entity.RefundRecord;

/**
 * <p>
 * 支付宝、微信退款记录表 服务类
 * </p>
 *
 * @author 
 * @since 2025-05-09
 */
public interface WxRefundRecordService extends IService<RefundRecord> {

    void createRefundByOrderNo(String orderNo, String reason, String payId, String bodyAsString);


    void updateRefund(String plainText, String orderNo);

    Integer getOrderStatus(String orderNo);
}
