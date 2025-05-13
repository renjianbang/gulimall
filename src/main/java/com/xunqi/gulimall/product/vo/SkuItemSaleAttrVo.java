package com.xunqi.gulimall.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: 夏沫止水
 * @createTime: 2020-06-19 18:17
 **/

@Data
@ToString
public class SkuItemSaleAttrVo {

    private Long /*attrId*/id;

    private String /*attrName*/saleAttrName;

    private List<AttrValueWithSkuIdVo> /*attrValues*/spuSaleAttrValueList;

}
