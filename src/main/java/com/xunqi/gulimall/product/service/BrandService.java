package com.xunqi.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xunqi.gulimall.product.entity.BrandEntity;
import com.xunqi.gulimall.product.vo.ProductListParam;
import com.xunqi.gulimall.product.vo.ProductListchilrenVo.Trademark;

import java.util.List;
import java.util.Map;

/**
 * 品牌
 *
 * @author 夏沫止水
 * @email HeJieLin@gulimall.com
 * @date 2020-05-22 19:00:18
 */
public interface BrandService extends IService<BrandEntity> {

    List<Trademark> brandListByParam(ProductListParam param);
}

