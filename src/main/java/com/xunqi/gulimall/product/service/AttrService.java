package com.xunqi.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.xunqi.gulimall.adminelementui.vo.AttrVo;
import com.xunqi.gulimall.product.entity.AttrEntity;


import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author 夏沫止水
 * @email HeJieLin@gulimall.com
 * @date 2020-05-22 19:00:18
 */
public interface AttrService extends IService<AttrEntity> {


    List<AttrVo> getAttrList(String category1Id, String category2Id, String category3Id);
}

