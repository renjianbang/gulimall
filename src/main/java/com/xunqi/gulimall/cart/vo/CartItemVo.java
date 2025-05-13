package com.xunqi.gulimall.cart.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 购物项内容
 * @Created: with IntelliJ IDEA.
 * @author: 夏沫止水
 * @createTime: 2020-06-30 16:43
 **/
@Data
public class CartItemVo {

    private String id;

    private Long skuId;

    private Boolean checked = true;

    private String skuName;

    private String imgUrl;

    /**
     * 商品套餐属性
     */
    private List<String> skuAttrValues;

    private BigDecimal skuPrice;

    private Integer skuNum;

    private BigDecimal totalPrice;

//    public Long getSkuId() {
//        return skuId;
//    }
//
//    public void setSkuId(Long skuId) {
//        this.skuId = skuId;
//    }
//
//    public Boolean getCheck() {
//        return checked;
//    }
//
//    public void setCheck(Boolean check) {
//        this.checked = check;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public String getImage() {
//        return imgUrl;
//    }
//
//    public void setImage(String image) {
//        this.imgUrl = image;
//    }
//
//    public List<String> getSkuAttrValues() {
//        return skuAttrValues;
//    }
//
//    public void setSkuAttrValues(List<String> skuAttrValues) {
//        this.skuAttrValues = skuAttrValues;
//    }
//
//    public BigDecimal getPrice() {
//        return skuPrice;
//    }
//
//    public void setPrice(BigDecimal price) {
//        this.skuPrice = price;
//    }
//
//    public Integer getCount() {
//        return skuNum;
//    }
//
//    public void setCount(Integer count) {
//        this.skuNum = count;
//    }

    /**
     * 计算当前购物项总价
     * @return
     */
    @JsonIgnore
    public BigDecimal getTotalPriceMethod() {

        return this.skuPrice.multiply(new BigDecimal("" + this.skuNum));
    }

}
