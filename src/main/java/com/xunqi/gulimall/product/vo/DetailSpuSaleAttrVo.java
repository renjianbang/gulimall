package com.xunqi.gulimall.product.vo;

import lombok.Data;

import javax.xml.soap.Detail;
import java.util.List;

/**
 * @Description
 * @Author cisz
 * @CreateTime 2025-05-14 09:13
 */
@Data
public class DetailSpuSaleAttrVo {

    private String id;

    private String saleAttrName;

    private List<DetailSpuSaleAttrValueVo> spuSaleAttrValueList;

}
