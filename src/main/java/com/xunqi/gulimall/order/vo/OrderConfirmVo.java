package com.xunqi.gulimall.order.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Description: 订单确认页需要用的数据
 * @Created: with IntelliJ IDEA.
 * @author: 夏沫止水
 * @createTime: 2020-07-02 18:59
 **/
@Data
public class OrderConfirmVo {


    /** 会员收获地址列表 **/
    List<MemberAddressVo> memberAddressVos;


    /** 所有选中的购物项 **/
    List<OrderItemVo> detailArrayList;

    /** 发票记录 **/

    /** 优惠券（会员积分） **/
    private Integer integration;

    /** 防止重复提交的令牌 **/

    private String orderToken;


    Map<Long,Boolean> stocks;

    public Integer getCount() {
        Integer count = 0;
        if (detailArrayList != null && detailArrayList.size() > 0) {
            for (OrderItemVo item : detailArrayList) {
                count += item.getSkuNum();
            }
        }
        return count;
    }


    /** 订单总额 **/
    //BigDecimal total;
    //计算订单总额
    public BigDecimal getTotal() {
        BigDecimal totalNum = BigDecimal.ZERO;
        if (detailArrayList != null && detailArrayList.size() > 0) {
            for (OrderItemVo item : detailArrayList) {
                //计算当前商品的总价格
                BigDecimal itemPrice = item.getOrderPrice().multiply(new BigDecimal(item.getSkuNum().toString()));
                //再计算全部商品的总价格
                totalNum = totalNum.add(itemPrice);
            }
        }
        return totalNum;
    }


    /** 应付价格 **/
    //BigDecimal payPrice;
    public BigDecimal getPayPrice() {
        return getTotal();
    }
}
