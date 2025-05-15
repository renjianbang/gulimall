package com.xunqi.gulimall.product.vo;

import lombok.Data;

import javax.xml.soap.Detail;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Description
 * @Author cisz
 * @CreateTime 2025-05-14 09:12
 */
@Data
public class DetailSkuInfoVo {

    private String id;

    private String skuName;

    private String skuDesc;

    private BigDecimal price;

    private List<DetailSkuImageVo> skuImageList;

}
