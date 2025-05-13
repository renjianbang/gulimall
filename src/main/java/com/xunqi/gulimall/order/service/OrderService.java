package com.xunqi.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.github.pagehelper.PageInfo;
import com.xunqi.gulimall.common.enmus.OrderStatus;
import com.xunqi.gulimall.order.entity.OrderEntity;
import com.xunqi.gulimall.order.vo.*;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author 夏沫止水
 * @email HeJieLin@gulimall.com
 * @date 2020-05-22 19:49:53
 */
public interface OrderService extends IService<OrderEntity> {



    /**
     * 订单确认页返回需要用的数据
     * @return
     */
    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    /**
     * 创建订单
     * @param vo
     * @return
     */
    String submitOrder(OrderSubmitVo vo);

    /**
     * 按照订单号获取订单信息
     * @param orderSn
     * @return
     */
    OrderEntity getOrderByOrderSn(String orderSn);



    /**
     * 获取当前订单的支付信息
     * @param orderSn
     * @return
     */
    PayVo getOrderPay(String orderSn);



    /**
     *支付宝异步通知处理订单数据
     * @param asyncVo
     * @return
     */
    String handlePayResult(PayAsyncVo asyncVo);


    PageInfo<MyOrderListVo> listMyOrder(Integer page, Integer limit);

    OrderEntity getOrderByOrderNo(String orderNo);

    String getOrderStatus(String orderNo);

    void updateStatusByOrderNo(String orderNo, OrderStatus orderStatus);
}

