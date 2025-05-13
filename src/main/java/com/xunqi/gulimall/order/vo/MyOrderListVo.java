package com.xunqi.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Description
 * @Author cisz
 * @CreateTime 2025-05-08 08:53
 */
@Data
public class MyOrderListVo {

    private String id;

    private Date createTime;

    /**订单编号：**/
    private String outTradeNo;

    private List<MyOrderDetailListVo> orderDetailList;

    /** **/
    private String consignee;

    private BigDecimal totalAmount;

    private String orderStatusName;
}
