package com.xunqi.gulimall.product.service.impl;

import com.xunqi.gulimall.product.service.MallSearchService;
import com.xunqi.gulimall.product.vo.ProductListParam;
import com.xunqi.gulimall.product.vo.ProductListVo;
import com.xunqi.gulimall.product.vo.ProductListchilrenVo.Attr;
import com.xunqi.gulimall.product.vo.ProductListchilrenVo.Product;
import com.xunqi.gulimall.product.vo.ProductListchilrenVo.Trademark;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.naming.directory.SearchResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author cisz
 * @CreateTime 2025-04-23 15:30
 */
@Service
public class MallSearchServiceImpl implements MallSearchService {



//        "trademarkList": [
//            // 品牌
//            {
//                "tmId": 1,
//                "tmName": "苹果"
//            }
//        ],
//        "attrsList": [
//            {
//                "attrId": 1,
//                "attrName": "价格",
//                "attrValueList": [
//                    "4500-11999",
//                    "2800-4499"
//                ]
//            }
//        ],
//        "goodsList": [
//            {
//                "id": 1,
//                "defaultImg": "http://example.com/image1.jpg",
//                "title": "Apple iPhone 11",
//                "price": 5499,
//                "createTime": null,
//                "tmId": 1,
//                "tmName": "苹果",
//                "category1Id": 1,
//                "category1Name": "手机",
//                "category2Id": 2,
//                "category2Name": "智能手机",
//                "category3Id": 3,
//                "category3Name": "苹果手机",
//                "hotScore": 100,
//                "attrs": null
//            }
//        ],
//        "pageInfo": null,
//        "total": 2,
//        "pageSize": 3,
//        "pageNo": 1,
//        "totalPages": 1

    @Override
    public ProductListVo listProducts(ProductListParam param) {
        // 创建 ProductListVo 实例
        ProductListVo productListVo = new ProductListVo();

        // 创建并填充 trademarkList
        List<Trademark> trademarkList = new ArrayList<>();
        Trademark trademark1 = new Trademark();
        trademark1.setTmId(1);
        trademark1.setTmName("苹果");
        trademarkList.add(trademark1);

        Trademark trademark2 = new Trademark();
        trademark2.setTmId(2);
        trademark2.setTmName("华为");
        trademarkList.add(trademark2);

        // 创建并填充 attrsList
        List<Attr> attrsList = new ArrayList<>();
        Attr attr1 = new Attr();
        attr1.setAttrId(1);
        attr1.setAttrName("价格");
        List<String> attrValueList1 = new ArrayList<>();
        attrValueList1.add("4500-11999");
        attrValueList1.add("2800-4499");
        attr1.setAttrValueList(attrValueList1);
        attrsList.add(attr1);

        Attr attr2 = new Attr();
        attr2.setAttrId(2);
        attr2.setAttrName("屏幕尺寸");
        List<String> attrValueList2 = new ArrayList<>();
        attrValueList2.add("6.1-6.2英寸");
        attrValueList2.add("6.3-6.4英寸");
        attr2.setAttrValueList(attrValueList2);
        attrsList.add(attr2);

        // 创建并填充 goodsList
        List<Product> goodsList = new ArrayList<>();
        Product product1 = new Product();
        product1.setId(1L);
        product1.setDefaultImg("https://pic1.zhimg.com/v2-4aee31c563f2ec4467c4a7a296a75e48_r.jpg?source=2c26e567");
        product1.setTitle("Apple iPhone 11");
        product1.setPrice(5499.0);
        product1.setTmId(1);
        product1.setTmName("苹果");
        product1.setCategory1Id(1L);
        product1.setCategory1Name("手机");
        product1.setCategory2Id(2L);
        product1.setCategory2Name("智能手机");
        product1.setCategory3Id(3L);
        product1.setCategory3Name("苹果手机");
        product1.setHotScore(100);
        goodsList.add(product1);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setDefaultImg("https://pic1.zhimg.com/v2-4aee31c563f2ec4467c4a7a296a75e48_r.jpg?source=2c26e567");
        product2.setTitle("Huawei Mate 30");
        product2.setPrice(4999.0);
        product2.setTmId(2);
        product2.setTmName("华为");
        product2.setCategory1Id(1L);
        product2.setCategory1Name("手机");
        product2.setCategory2Id(2L);
        product2.setCategory2Name("智能手机");
        product2.setCategory3Id(3L);
        product2.setCategory3Name("华为手机");
        product2.setHotScore(90);
        goodsList.add(product2);

        // 设置 ProductListVo 的属性
        productListVo.setTrademarkList(trademarkList);
        productListVo.setAttrsList(attrsList);
        productListVo.setGoodsList(goodsList);
        productListVo.setTotal(2); // 总记录数
        productListVo.setPageSize(param.getPageSize()); // 每页显示的记录数
        productListVo.setPageNo(param.getPageNo()); // 当前页码
        productListVo.setTotalPages(1); // 总页数

        return productListVo;
    }
}
