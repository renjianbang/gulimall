package com.xunqi.gulimall.product.vo;

import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Author cisz
 * @CreateTime 2025-05-14 09:10
 */
@Data
public class DetailVo {

    DetailCategoryViewVo categoryView;

    DetailSkuInfoVo skuInfo;

    //[
    //  {
    //    saleAttrName: "颜色",
    //    spuSaleAttrValueList: [
    //      {id:1, saleAttrValueName:"曜石黑", isChecked:1},
    //      {id:2, saleAttrValueName:"冰川蓝", isChecked:0}
    //    ]
    //  },
    //  {
    //    saleAttrName: "版本",
    //    spuSaleAttrValueList: [
    //      {id:3, saleAttrValueName:"8GB+128GB", isChecked:1},
    //      {id:4, saleAttrValueName:"8GB+256GB", isChecked:0}
    //    ]
    //  }
    //]
    List<DetailSpuSaleAttrVo> spuSaleAttrList;

}
