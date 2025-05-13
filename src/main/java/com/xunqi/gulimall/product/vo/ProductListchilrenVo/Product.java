package com.xunqi.gulimall.product.vo.ProductListchilrenVo;

import lombok.Data;

import java.util.Date;

/**
 * @Description
 * @Author cisz
 * @CreateTime 2025-04-23 15:44
 */
@Data
public class Product {
    private Long id;
    private String defaultImg;
    private String title;
    private Double price;
    private Date createTime;
    private Integer tmId;
    private String tmName;
    private Long category1Id;
    private String category1Name;
    private Long category2Id;
    private String category2Name;
    private Long category3Id;
    private String category3Name;
    private Integer hotScore;
    private Object attrs; // 这里使用 Object 类型，实际应用中可以根据具体需求调整

}
