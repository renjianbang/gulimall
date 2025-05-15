package com.xunqi.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xunqi.gulimall.product.entity.CategoryEntity;
import com.xunqi.gulimall.product.vo.Catelog2Vo;
import com.xunqi.gulimall.product.vo.DetailCategoryViewVo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author 夏沫止水
 * @email HeJieLin@gulimall.com
 * @date 2020-05-22 19:00:18
 */
public interface CategoryService extends IService<CategoryEntity> {

//    Map<String, List<Catelog2Vo>> getCatalogJson();

    List<CategoryEntity> listWithTree();

    DetailCategoryViewVo getCategoryView(Long catalogId);
}

