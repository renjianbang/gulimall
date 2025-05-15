package com.xunqi.gulimall.product.vo;

import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Author cisz
 * @CreateTime 2025-04-23 15:31
 *
 *       searchParams: {
 *         //产品相应的id
 *         category1Id: "",
 *         category2Id: "",
 *         category3Id: "",
 *         //产品的名字
 *         categoryName: "",
 *         //搜索的关键字
 *         keyword: "",
 *         //排序:初始状态应该是综合且降序
 *         order: "1:desc",
 *         //第几页
 *         pageNo: 1,
 *         //每一页展示条数
 *         pageSize: 3,
 *         //平台属性的操作
 *         props: [],
 *         //品牌
 *         trademark: "",
 *       },
 *
 */
@Data
public class ProductListParam {
    private String category1Id;
    private String category2Id;
    private String category3Id;
    private String categoryName;//分类的面包屑
    private String keyword;     //关键字的面包屑
    private String order;
    private Integer pageNo;
    private Integer pageSize;
    private List<String> props; //平台的售卖的属性值展示
    private String trademark;   //品牌的面包屑
}
