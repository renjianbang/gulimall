package com.xunqi.gulimall.product.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.xunqi.gulimall.adminelementui.vo.AttrValueVo;
import com.xunqi.gulimall.adminelementui.vo.AttrVo;
import com.xunqi.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.xunqi.gulimall.product.dao.AttrDao;
import com.xunqi.gulimall.product.dao.AttrGroupDao;
import com.xunqi.gulimall.product.dao.CategoryDao;
import com.xunqi.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.xunqi.gulimall.product.entity.AttrEntity;
import com.xunqi.gulimall.product.entity.AttrGroupEntity;
import com.xunqi.gulimall.product.entity.CategoryEntity;
import com.xunqi.gulimall.product.service.AttrAttrgroupRelationService;
import com.xunqi.gulimall.product.service.AttrGroupService;
import com.xunqi.gulimall.product.service.AttrService;
import com.xunqi.gulimall.product.service.CategoryService;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Resource
    private CategoryService categoryService;

    @Resource
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Resource
    private AttrGroupService attrGroupService;

    @Resource
    private AttrService attrService;

    @Override
    public List<AttrVo> getAttrList(String category1Id, String category2Id, String category3Id) {
        //1、根据分类id查询所有属性
        List<AttrGroupEntity> attrGroupList = attrGroupService.list(new LambdaQueryWrapper<AttrGroupEntity>()
//                .eq(StrUtil.isNotBlank(category1Id), AttrGroupEntity::getCatelogId, category1Id)
//                .eq(StrUtil.isNotBlank(category2Id), AttrGroupEntity::getCatelogId, category2Id)
                .eq(StrUtil.isNotBlank(category3Id), AttrGroupEntity::getCatelogId, category3Id)
        );
        List<Long> groupIdList = attrGroupList.stream().map(AttrGroupEntity::getAttrGroupId).filter(Objects::nonNull).collect(Collectors.toList());
        if (CollUtil.isEmpty(groupIdList)) {
            return new ArrayList<>();
        }
        List<AttrAttrgroupRelationEntity> relationList = attrAttrgroupRelationService.list(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                .in(AttrAttrgroupRelationEntity::getAttrGroupId, groupIdList)
        );
        if (CollUtil.isEmpty(relationList)) {
            return attrGroupList.stream().map(group -> {
                AttrVo attrVo = new AttrVo();
                attrVo.setId(group.getAttrGroupId());
                attrVo.setAttrName(group.getAttrGroupName());
                attrVo.setCategoryId(group.getCatelogId());
                attrVo.setCategoryLevel(StrUtil.isNotBlank(category1Id) ? 1 : StrUtil.isNotBlank(category2Id) ? 2 : 3);
                attrVo.setAttrValueList(new ArrayList<>());
                return attrVo;
            }).collect(Collectors.toList());
        } else {
            List<AttrVo> attrVoList = attrGroupList.stream().map(group -> {


                AttrVo attrVo = new AttrVo();
                attrVo.setId(group.getAttrGroupId());
                attrVo.setAttrName(group.getAttrGroupName());
                attrVo.setCategoryId(group.getCatelogId());
                attrVo.setCategoryLevel(StrUtil.isNotBlank(category1Id) ? 1 : StrUtil.isNotBlank(category2Id) ? 2 : 3);
                attrVo.setAttrValueList(relationList.stream().filter(relation -> relation.getAttrGroupId().equals(group.getAttrGroupId()))
                        .map(relation -> {
                            AttrEntity attrById = attrService.getById(relation.getAttrId());
                            AttrValueVo attrValueVo = new AttrValueVo();
                            attrValueVo.setId(attrById.getAttrId());
                            attrValueVo.setValueName(attrById.getAttrName());
                            attrValueVo.setAttrId(attrById.getAttrId());
                            return attrValueVo;
                        }).collect(Collectors.toList()));
                return attrVo;
            }).collect(Collectors.toList());
            return attrVoList;
        }
    }
}