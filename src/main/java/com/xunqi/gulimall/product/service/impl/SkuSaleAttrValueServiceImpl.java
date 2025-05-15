package com.xunqi.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.xunqi.gulimall.product.dao.SkuSaleAttrValueDao;
import com.xunqi.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.xunqi.gulimall.product.service.SkuSaleAttrValueService;

import com.xunqi.gulimall.product.vo.DetailSpuSaleAttrValueVo;
import com.xunqi.gulimall.product.vo.DetailSpuSaleAttrVo;
import com.xunqi.gulimall.product.vo.SkuItemSaleAttrVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public List<SkuItemSaleAttrVo> getSaleAttrBySpuId(Long spuId) {

        SkuSaleAttrValueDao baseMapper = this.getBaseMapper();
        List<SkuItemSaleAttrVo> saleAttrVos = baseMapper.getSaleAttrBySpuId(spuId);

        return saleAttrVos;
    }

    @Override
    public List<String> getSkuSaleAttrValuesAsStringList(Long skuId) {

        SkuSaleAttrValueDao baseMapper = this.baseMapper;
        List<String> stringList = baseMapper.getSkuSaleAttrValuesAsStringList(skuId);

        return stringList;
    }

    @Override
    public List<DetailSpuSaleAttrVo> getSaleAttrsBySpuId(Long spuId) {
        // 1. 获取原始销售属性数据
        List<SkuItemSaleAttrVo> originList = this.getSaleAttrBySpuId(spuId);

        // 2. 转换为目标结构
        return originList.stream().map(origin -> {
            DetailSpuSaleAttrVo vo = new DetailSpuSaleAttrVo();
            vo.setSaleAttrName(origin.getSaleAttrName());

            // 3. 转换属性值列表
            List<DetailSpuSaleAttrValueVo> valueVos = origin.getSpuSaleAttrValueList().stream()
                    .map(value -> {
                        DetailSpuSaleAttrValueVo valueVo = new DetailSpuSaleAttrValueVo();
                        valueVo.setId(value.getSkuIds());
                        valueVo.setSaleAttrValueName(value.getAttrValue());
                        valueVo.setIsChecked(0); // 默认未选中，需根据业务逻辑调整
                        return valueVo;
                    }).collect(Collectors.toList());

            vo.setSpuSaleAttrValueList(valueVos);
            return vo;
        }).collect(Collectors.toList());
    }
}