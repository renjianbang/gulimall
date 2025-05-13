package com.xunqi.gulimall.product.vo;

import com.xunqi.gulimall.product.vo.ProductListchilrenVo.Attr;
import com.xunqi.gulimall.product.vo.ProductListchilrenVo.PageInfo;
import com.xunqi.gulimall.product.vo.ProductListchilrenVo.Product;
import com.xunqi.gulimall.product.vo.ProductListchilrenVo.Trademark;
import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Author cisz
 * @CreateTime 2025-04-23 15:34
 */
@Data
public class ProductListVo {
    private List<Trademark> trademarkList;
    private List<Attr> attrsList;
    private List<Product> goodsList;
    private PageInfo pageInfo;

    private long total;
    private int pageSize;
    private int pageNo;
    private int totalPages;
}
