package com.xunqi.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xunqi.gulimall.product.dao.SkuImagesDao;
import com.xunqi.gulimall.product.entity.SkuImagesEntity;
import com.xunqi.gulimall.product.service.SkuImagesService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("skuImagesService")
public class SkuImagesServiceImpl extends ServiceImpl<SkuImagesDao, SkuImagesEntity> implements SkuImagesService {


    @Override
    public List<SkuImagesEntity> getImagesBySkuId(Long skuId) {
        QueryWrapper<SkuImagesEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("sku_id", skuId);
        return baseMapper.selectList(wrapper);
    }
}