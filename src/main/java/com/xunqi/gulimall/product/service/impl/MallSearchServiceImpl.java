package com.xunqi.gulimall.product.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xunqi.gulimall.product.entity.*;
import com.xunqi.gulimall.product.service.*;
import com.xunqi.gulimall.product.vo.ProductListParam;
import com.xunqi.gulimall.product.vo.ProductListVo;
import com.xunqi.gulimall.product.vo.ProductListchilrenVo.Attr;
import com.xunqi.gulimall.product.vo.ProductListchilrenVo.Product;
import com.xunqi.gulimall.product.vo.ProductListchilrenVo.Trademark;
import lombok.Data;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.naming.directory.SearchResult;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author cisz
 * @CreateTime 2025-04-23 15:30
 */
@Service
public class MallSearchServiceImpl implements MallSearchService {

    @Resource
    private BrandService brandService;

    @Resource
    private AttrService attrService; // 需要实现属性查询服务

    @Resource
    private ProductAttrValueService productAttrValueService;

    @Resource
    private SkuSaleAttrValueService skuSaleAttrValueService;

//    pms_spu_info
    @Resource
    private SpuInfoService spuInfoService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private SkuInfoService skuInfoService;
    // @Override
    // public Map<String, Object> listProducts(ProductListParam param) {
    //     return null;
    // }

    // {


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
//        @Data
//        public class ProductListParam {
//            private String category1Id;
//            private String category2Id;
//            private String category3Id;
//            private String categoryName;//分类的面包屑
//            private String keyword;     //关键字的面包屑
//            private String order;
//            private Integer pageNo;
//            private Integer pageSize;
//            private List<String> props; //平台的售卖的属性值展示
//            private String trademark;   //品牌的面包屑
//        }
        // 创建 ProductListVo 实例
        ProductListVo productListVo = new ProductListVo();
        List<Trademark> trademarkList = brandService.brandListByParam(param);
        // 创建并填充 trademarkList
//        List<Trademark> trademarkList = new ArrayList<>();
//        Trademark trademark1 = new Trademark();
//        trademark1.setTmId(1);
//        trademark1.setTmName("苹果");
//        trademarkList.add(trademark1);

//        Trademark trademark2 = new Trademark();
//        trademark2.setTmId(2);
//        trademark2.setTmName("华为");
//        trademarkList.add(trademark2);

        // 根据AttrAttrgroupRelationEntity AttrEntity AttrGroupEntity ProductAttrValueEntity SkuSaleAttrValueEntity 创建并填充 attrsList
        List<Attr> attrsList = new ArrayList<>();
//        Attr attr1 = new Attr();
//        attr1.setAttrId(1);
//        attr1.setAttrName("价格");
//        List<String> attrValueList1 = new ArrayList<>();
//        attrValueList1.add("4500-11999");
//        attrValueList1.add("2800-4499");
//        attr1.setAttrValueList(attrValueList1);
//        attrsList.add(attr1);
//
//        Attr attr2 = new Attr();
//        attr2.setAttrId(2);
//        attr2.setAttrName("屏幕尺寸");
//        List<String> attrValueList2 = new ArrayList<>();
//        attrValueList2.add("6.1-6.2英寸");
//        attrValueList2.add("6.3-6.4英寸");
//        attr2.setAttrValueList(attrValueList2);
//        attrsList.add(attr2);
        // 1. 获取分类下所有基本属性（根据分类ID查询）
        List<AttrEntity> baseAttrs = attrService.list(new QueryWrapper<AttrEntity>()
                .eq("catelog_id", param.getCategory3Id()) // 根据三级分类ID查询
                .eq("attr_type", 1) // 基本属性
        );
        Set<Long> attrIds = baseAttrs.stream()
                .map(AttrEntity::getAttrId)
                .collect(Collectors.toSet());
        if (CollUtil.isEmpty(attrIds)) {
            return productListVo;
        }
        // 批量查询所有属性值（单个查询替代循环查询）
        List<ProductAttrValueEntity> allValues = productAttrValueService.list(
                new QueryWrapper<ProductAttrValueEntity>()
                        .select("attr_id", "attr_value")
                        .in("attr_id", attrIds)
                        .groupBy("attr_id", "attr_value") // SQL层去重
        );
        Map<Long, List<String>> valueMap = allValues.stream()
                .collect(Collectors.groupingBy(
                        ProductAttrValueEntity::getAttrId,
                        Collectors.mapping(ProductAttrValueEntity::getAttrValue, Collectors.toList())
                ));
        // 处理属性转换
        baseAttrs.forEach(attrEntity -> {
            Attr vo = new Attr();
            vo.setAttrId(Math.toIntExact(attrEntity.getAttrId())); // 直接使用Long类型
            vo.setAttrName(attrEntity.getAttrName());

            // 从内存映射获取值（避免数据库查询）
            List<String> values = valueMap.getOrDefault(attrEntity.getAttrId(), Collections.emptyList());
            vo.setAttrValueList(values);

            attrsList.add(vo);
        });

        // 2. 获取销售属性（根据SPU查询）
        List<SkuSaleAttrValueEntity> saleAttrs = skuSaleAttrValueService.list(
                new QueryWrapper<SkuSaleAttrValueEntity>()
                        .select("DISTINCT attr_id, attr_name, attr_value")
                        .in("attr_id", attrIds)
        );

        saleAttrs.forEach(saleAttr -> {
            Attr vo = new Attr();
            vo.setAttrId(Math.toIntExact(saleAttr.getAttrId()));
            vo.setAttrName(saleAttr.getAttrName());

            // 销售属性值直接取唯一值
            vo.setAttrValueList(new ArrayList<String>(){{ add(saleAttr.getAttrValue()); }});
            attrsList.add(vo);
        });



        // 创建并填充 goodsList   根据传递进来的 param 参数 spuInfoService查询 数据表
        List<Product> goodsList = new ArrayList<>();
//        Product product1 = new Product();
//        product1.setId(1L);
//        product1.setDefaultImg("https://pic1.zhimg.com/v2-4aee31c563f2ec4467c4a7a296a75e48_r.jpg?source=2c26e567");
//        product1.setTitle("Apple iPhone 11");
//        product1.setPrice(5499.0);
//        product1.setTmId(1);
//        product1.setTmName("苹果");
//        product1.setCategory1Id(1L);
//        product1.setCategory1Name("手机");
//        product1.setCategory2Id(2L);
//        product1.setCategory2Name("智能手机");
//        product1.setCategory3Id(3L);
//        product1.setCategory3Name("苹果手机");
//        product1.setHotScore(100);
//        goodsList.add(product1);
//
//        Product product2 = new Product();
//        product2.setId(2L);
//        product2.setDefaultImg("https://pic1.zhimg.com/v2-4aee31c563f2ec4467c4a7a296a75e48_r.jpg?source=2c26e567");
//        product2.setTitle("Huawei Mate 30");
//        product2.setPrice(4999.0);
//        product2.setTmId(2);
//        product2.setTmName("华为");
//        product2.setCategory1Id(1L);
//        product2.setCategory1Name("手机");
//        product2.setCategory2Id(2L);
//        product2.setCategory2Name("智能手机");
//        product2.setCategory3Id(3L);
//        product2.setCategory3Name("华为手机");
//        product2.setHotScore(90);
//        goodsList.add(product2);
        // 构建SPU查询条件
        QueryWrapper<SpuInfoEntity> spuWrapper = new QueryWrapper<>();
        spuWrapper.eq("publish_status", 1); // 只查询已上架商品

        // 根据分类查询
        if (StrUtil.isNotBlank(param.getCategory3Id())) {
            spuWrapper.eq("catalog_id", param.getCategory3Id());
        }

        // 根据关键词模糊查询
        if (StrUtil.isNotBlank(param.getKeyword())) {
            spuWrapper.like("spu_name", param.getKeyword());
        }

        // 分页查询
        List<SpuInfoEntity> spuList = spuInfoService.list(spuWrapper);

        // 转换SPU为Product
        spuList.forEach(spu -> {
            Product product = new Product();

            // 基础信息
            product.setId(spu.getId());
            product.setTitle(spu.getSpuName());

            // 获取默认SKU信息
            SkuInfoEntity defaultSku = skuInfoService.getOne(
                    new QueryWrapper<SkuInfoEntity>()
                            .eq("spu_id", spu.getId())
                            .orderByAsc("price")
                            .last("LIMIT 1")
            );

            // 设置价格和图片
            if (defaultSku != null) {
                product.setPrice(defaultSku.getPrice().doubleValue());
                product.setDefaultImg(defaultSku.getSkuDefaultImg());
            }

            // 设置分类信息
            product.setCategory1Id(spu.getCatalogId());
            // 需要查询分类服务获取分类名称
            CategoryEntity category = categoryService.getById(spu.getCatalogId());
            if (category != null) {
                product.setCategory3Name(category.getCategoryName());
            }

            // 设置品牌信息
            product.setTmId(Math.toIntExact(spu.getBrandId()));
            BrandEntity brand = brandService.getById(spu.getBrandId());
            if (brand != null) {
                product.setTmName(brand.getName());
            }

            goodsList.add(product);
        });

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
