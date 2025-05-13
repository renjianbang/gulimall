package com.xunqi.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 封装订单提交数据的vo
 * @Created: with IntelliJ IDEA.
 * @author: 夏沫止水
 * @createTime: 2020-07-04 11:54
 **/

//      let data = {
//        consignee: this.userDefaultAddress.consignee, //最终收件人的名字
//        consigneeTel: this.userDefaultAddress.phoneNum, //最终收件人的手机号
//        deliveryAddress: this.userDefaultAddress.fullAddress, //收件人的地址
//        paymentWay: "ONLINE", //支付方式
//        orderComment: this.msg, //买家的留言信息
//        orderDetailList: this.orderInfo.detailArrayList, //商品清单
//      };
@Data
public class OrderSubmitVo {

/*    *//** 收获地址的id **//*
    private Long addrId;

    *//** 支付方式 **//*
    private Integer payType;
    //无需提交要购买的商品，去购物车再获取一遍
    //优惠、发票

    *//** 防重令牌 **//*
    private String orderToken;

    *//** 应付价格 **//*
    private BigDecimal payPrice;

    *//** 订单备注 **//*
    private String remarks;*/





    private String consignee; //最终收件人的名字
    private String consigneeTel; //最终收件人的手机号
    private String deliveryAddress; //收件人的地址
    private String paymentWay; //支付方式
    private String orderComment; //买家的留言信息
    List<OrderItemVo> detailArrayList;//商品清单
    //用户相关的信息，直接去session中取出即可
}
