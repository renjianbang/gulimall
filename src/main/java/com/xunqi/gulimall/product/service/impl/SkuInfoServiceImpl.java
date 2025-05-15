package com.xunqi.gulimall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.xunqi.gulimall.product.dao.SkuInfoDao;
import com.xunqi.gulimall.product.entity.SkuImagesEntity;
import com.xunqi.gulimall.product.entity.SkuInfoEntity;
import com.xunqi.gulimall.product.entity.SpuInfoDescEntity;

import com.xunqi.gulimall.product.service.*;


import com.xunqi.gulimall.product.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Resource
    private SkuImagesService skuImagesService;

    @Resource
    private SpuInfoDescService spuInfoDescService;

    @Resource
    private AttrGroupService attrGroupService;

    @Resource
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private ThreadPoolExecutor executor;

    // 转换方法示例
    private DetailSkuInfoVo convertToDetailSkuInfoVo(SkuInfoEntity entity) {
        DetailSkuInfoVo vo = new DetailSkuInfoVo();
        vo.setId(String.valueOf(entity.getSkuId()));
        vo.setSkuName(entity.getSkuName());
        vo.setPrice(entity.getPrice());
        // 其他字段转换...
        // 转换图片列表（需要确保skuImagesService已注入）
        List<DetailSkuImageVo> imageVos = skuImagesService.getImagesBySkuId(entity.getSkuId())
                .stream()
                .map(this::convertToImageVo)
                .collect(Collectors.toList());
        vo.setSkuImageList(imageVos);
        return vo;
    }

    // 新增图片转换方法
    private DetailSkuImageVo convertToImageVo(SkuImagesEntity imageEntity) {
        DetailSkuImageVo vo = new DetailSkuImageVo();
        vo.setId(String.valueOf(imageEntity.getId()));
        vo.setImgUrl(imageEntity.getImgUrl());
        return vo;
    }

    @Override
    public DetailVo item(Long skuId) throws ExecutionException, InterruptedException {
        DetailVo detailVo = new DetailVo();

        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
            // 1.获取SKU基本信息
            SkuInfoEntity skuInfo = getById(skuId);
            // 转换为DetailSkuInfoVo（需要自行实现转换逻辑）
            detailVo.setSkuInfo(convertToDetailSkuInfoVo(skuInfo));
            return skuInfo;
        }, executor);

        CompletableFuture<Void> categoryFuture = infoFuture.thenAcceptAsync(skuInfo -> {
                // 2.获取分类视图（需要实现分类查询服务）
                DetailCategoryViewVo categoryView = categoryService.getCategoryView(skuInfo.getCatalogId());
                detailVo.setCategoryView(categoryView);
        }, executor);

        CompletableFuture<Void> saleAttrFuture = infoFuture.thenAcceptAsync(skuInfo -> {
            // 3.获取SPU销售属性组合
            List<DetailSpuSaleAttrVo> saleAttrs = skuSaleAttrValueService
                    .getSaleAttrsBySpuId(skuInfo.getSpuId());
            detailVo.setSpuSaleAttrList(saleAttrs);
        }, executor);

        // 等待所有异步任务完成
        CompletableFuture.allOf(infoFuture, categoryFuture, saleAttrFuture).get();

        return detailVo;
    }


/*
    @Override
    public DetailVo item(Long skuId) throws ExecutionException, InterruptedException {

        SkuItemVo skuItemVo = new SkuItemVo();

        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
            //1、sku基本信息的获取  pms_sku_info
            SkuInfoEntity info = this.getById(skuId);
            skuItemVo.setSkuInfo(info);
            return info;
        }, executor);


        CompletableFuture<Void> saleAttrFuture = infoFuture.thenAcceptAsync((res) -> {
            //3、获取spu的销售属性组合
            List<SkuItemSaleAttrVo> saleAttrVos = skuSaleAttrValueService.getSaleAttrBySpuId(res.getSpuId());
            skuItemVo.setSpuSaleAttrList(saleAttrVos);
        }, executor);


        CompletableFuture<Void> descFuture = infoFuture.thenAcceptAsync((res) -> {
            //4、获取spu的介绍    pms_spu_info_desc
            SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(res.getSpuId());
            skuItemVo.setDesc(spuInfoDescEntity);
        }, executor);


//        CompletableFuture<Void> baseAttrFuture = infoFuture.thenAcceptAsync((res) -> {
//            //5、获取spu的规格参数信息
//            List<SpuItemAttrGroupVo> attrGroupVos = attrGroupService.getAttrGroupWithAttrsBySpuId(res.getSpuId(), res.getCatalogId());
//            skuItemVo.setGroupAttrs(attrGroupVos);
//        }, executor);
//
//
//         Long spuId = info.getSpuId();
//         Long catalogId = info.getCatalogId();

        //2、sku的图片信息    pms_sku_images
//        CompletableFuture<Void> imageFuture = CompletableFuture.runAsync(() -> {
//            List<SkuImagesEntity> imagesEntities = skuImagesService.getImagesBySkuId(skuId);
//            skuItemVo.setImages(imagesEntities);
//        }, executor);

*/
/*        CompletableFuture<Void> seckillFuture = CompletableFuture.runAsync(() -> {
            //3、远程调用查询当前sku是否参与秒杀优惠活动
            R skuSeckilInfo = seckillFeignService.getSkuSeckilInfo(skuId);
            if (skuSeckilInfo.getCode() == 0) {
                //查询成功
                SeckillSkuVo seckilInfoData = skuSeckilInfo.getData("data", new TypeReference<SeckillSkuVo>() {
                });
                skuItemVo.setSeckillSkuVo(seckilInfoData);

                if (seckilInfoData != null) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime > seckilInfoData.getEndTime()) {
                        skuItemVo.setSeckillSkuVo(null);
                    }
                }
            }
        }, executor);*//*



        //等到所有任务都完成
        CompletableFuture.allOf(saleAttrFuture,descFuture*/
/*,baseAttrFuture,imageFuture*//*
*/
/*,seckillFuture*//*
).get();

        return skuItemVo;
    }
*/

}