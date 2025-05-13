package com.xunqi.gulimall.wxpay.service;

import java.security.GeneralSecurityException;
import java.util.Map;

/**
 * @Description 服务接口
 * @Author cisz
 * @CreateTime 2025-05-08 14:52
 */
public interface WxPayService {

    Map<String, Object> nativePay(String orderNo) throws Exception;

    void processOrder(Map<String, Object> bodyMap) throws GeneralSecurityException;

    void cancelOrder(String orderNo) throws Exception;

    String queryOrder(String orderNo) throws Exception;

    void checkOrderStatus(String orderNo) throws Exception;

    void refund(String orderNo, String reason) throws Exception;

    String queryRefund(String orderNo) throws Exception;

    void checkRefundStatus(String refundNo) throws Exception;

    void processRefund(Map<String, Object> bodyMap) throws Exception;

    String queryBill(String billDate, String type) throws Exception;

    String downloadBill(String billDate, String type) throws Exception;


    Integer getOrderStatus(String orderNo);
}