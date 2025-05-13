package com.xunqi.gulimall.product.service;

import com.xunqi.gulimall.product.vo.ProductListParam;
import com.xunqi.gulimall.product.vo.ProductListVo;

import javax.naming.directory.SearchResult;
import java.util.Map;

/**
 * @Description 服务接口
 * @Author cisz
 * @CreateTime 2025-04-23 15:30
 */
public interface MallSearchService {

    ProductListVo listProducts(ProductListParam param);
}
