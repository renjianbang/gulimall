package com.xunqi.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: 夏沫止水
 * @createTime: 2020-07-02 19:07
 **/

@Data
public class OrderItemVo {

    private Long skuId;

    private Boolean checked;

    private String skuName;

    private String imgUrl;

    /**
     * 商品套餐属性
     */
    private List<String> skuAttrValues;

    private BigDecimal orderPrice;

    private Integer skuNum;

    private BigDecimal totalAmount;

    private Integer totalNum;

    /** 商品重量 **/
    private BigDecimal weight = new BigDecimal("0.085");
}
