package com.xunqi.gulimall.product.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.xunqi.gulimall.product.dao.BrandDao;
import com.xunqi.gulimall.product.entity.BrandEntity;
import com.xunqi.gulimall.product.service.BrandService;
import com.xunqi.gulimall.product.service.CategoryBrandRelationService;

import com.xunqi.gulimall.product.vo.ProductListParam;
import com.xunqi.gulimall.product.vo.ProductListchilrenVo.Trademark;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {


    @Override
    public List<Trademark> brandListByParam(ProductListParam param) {
        if (param == null) {
            return new ArrayList<>();
        }

        String category1Id = param.getCategory1Id();
        String category2Id = param.getCategory2Id();
        String category3Id = param.getCategory3Id();
        String categoryName = param.getCategoryName();
        String keyword = param.getKeyword();
        String order = param.getOrder();
        Integer pageNo = param.getPageNo();
        Integer pageSize = param.getPageSize();
        List<String> props = param.getProps();
        String trademark =param.getTrademark();

        // 1. 获取品牌列表 如果categoryName不为空，则根据categoryName 模糊查询 如果keyword不为空，加上keyword的模糊查询
        // 构建查询条件
        QueryWrapper<BrandEntity> wrapper = new QueryWrapper<>();

        // 按分类名称模糊查询
//        if (StrUtil.isNotBlank(categoryName)) {
//            wrapper.like("category_name", categoryName);
//        }

        // 按关键词模糊查询（同时匹配名称和描述）
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(qw -> qw
                    .like("name", keyword)
                    .or()
                    .like("descript", keyword)
            );
        }
        List<BrandEntity> brandEntities = this.list(wrapper);
        return brandEntities.stream().map(entity -> {
            Trademark tm = new Trademark();
            tm.setTmId(Math.toIntExact(entity.getBrandId())); // 假设需要转换ID格式
            tm.setTmName(entity.getName());
            // 根据需要补充其他字段转换
            return tm;
        }).collect(Collectors.toList());
    }
}