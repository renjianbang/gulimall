package com.xunqi.gulimall.product.vo.ProductListchilrenVo;

import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Author cisz
 * @CreateTime 2025-04-23 15:44
 */
@Data
public class Attr {
    private Integer attrId;
    private String attrName;
    private List<String> attrValueList;
}
