package com.xunqi.gulimall.paymentrecord.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xunqi.gulimall.common.enmus.OrderStatus;
import com.xunqi.gulimall.paymentrecord.entity.PaymentRecord;

/**
 * <p>
 * 支付宝、微信支付记录表 服务类
 * </p>
 *
 * @author renjianbang
 * @since 2025-05-07
 */
public interface WxPaymentRecordService extends IService<PaymentRecord> {

    PaymentRecord createPaymentRecord(String orderNo);

    void saveCodeUrl(String orderNo, String codeUrl);

    void updatePaymentInfo(String plainText);

    void updateStatusByOrderNo(String orderNo, OrderStatus orderStatus);

    Integer getOrderStatus(String orderNo);
}
