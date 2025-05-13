package com.xunqi.gulimall.product.vo.ProductListchilrenVo;

import lombok.Data;

/**
 * @Description
 * @Author cisz
 * @CreateTime 2025-04-23 15:45
 */
@Data
public class PageInfo {
    private long total;
    private int pageSize;
    private int pageNo;
    private int totalPages;
}
