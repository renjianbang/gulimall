package com.xunqi.gulimall.product.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xunqi.gulimall.adminelementui.vo.SelectCategoryVo;
import com.xunqi.gulimall.product.dao.CategoryDao;
import com.xunqi.gulimall.product.entity.CategoryEntity;
import com.xunqi.gulimall.product.service.CategoryService;
import com.xunqi.gulimall.product.vo.Catelog2Vo;

import com.xunqi.gulimall.product.vo.DetailCategoryViewVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;

import org.springframework.stereotype.Service;


import java.util.*;

import java.util.stream.Collectors;

@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    private Map<String,Object> cache = new HashMap<>();

    //递归查找所有菜单的子菜单
    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {

        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid().equals(root.getCategoryId());
        }).map(categoryEntity -> {
            //1、找到子菜单(递归)
            categoryEntity.setCategoryChild(getChildrens(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu, menu2) -> {
            //2、菜单的排序
            return (menu.getSort() == null ? 0 : menu.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());

        return children;

    }


/*
    @Cacheable(value = "category",key = "#root.methodName")
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        //将数据库的多次查询变为一次
        List<CategoryEntity> selectList = this.baseMapper.selectList(null);

        //1、查出所有分类
        //1、1）查出所有一级分类
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);

        //封装数据
        Map<String, List<Catelog2Vo>> parentCid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //1、每一个的一级分类,查到这个一级分类的二级分类
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());

            //2、封装上面的结果
            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName().toString());

                    //1、找当前二级分类的三级分类封装成vo
                    List<CategoryEntity> level3Catelog = getParent_cid(selectList, l2.getCatId());

                    if (level3Catelog != null) {
                        List<Catelog2Vo.Category3Vo> category3Vos = level3Catelog.stream().map(l3 -> {
                            //2、封装成指定格式
                            Catelog2Vo.Category3Vo category3Vo = new Catelog2Vo.Category3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());

                            return category3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(category3Vos);
                    }

                    return catelog2Vo;
                }).collect(Collectors.toList());
            }

            return catelog2Vos;
        }));

        return parentCid;
    }
*/

    @Override
    public List<CategoryEntity> listWithTree() {

        //1、查询出所有分类
        List<CategoryEntity> entities = super.baseMapper.selectList(null);

        //2、组装成父子的树形结构

        //2.1)、找到所有一级分类
        List<CategoryEntity> levelMenus = entities.stream()
                .filter(e -> e.getParentCid() == 0)
                .map((menu) -> {
                    menu.setCategoryChild(getChildrens(menu, entities));
                    return menu;
                })
                .sorted((menu, menu2) -> {
                    return (menu.getSort() == null ? 0 : menu.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
                })
                .collect(Collectors.toList());

        return levelMenus;
    }

    @Override
    public DetailCategoryViewVo getCategoryView(Long catalogId) {
        DetailCategoryViewVo detailCategoryViewVo = new DetailCategoryViewVo();
        CategoryEntity byId = this.getById(catalogId);
        if (byId == null) {
            log.info("catalogId:{}不存在", catalogId);
            return detailCategoryViewVo;
        }
        if (byId.getCatLevel() == null) {
            log.info("catalogId:{}的分类等级不存在", catalogId);
            return detailCategoryViewVo;
        }
        switch (byId.getCatLevel()) {
            case 1:
                detailCategoryViewVo.setCategory1Name(byId.getCategoryName());
                break;
            case 2:
                detailCategoryViewVo.setCategory2Name(byId.getCategoryName());
                break;
            case 3:
                detailCategoryViewVo.setCategory3Name(byId.getCategoryName());
                break;
        }
        return detailCategoryViewVo;
    }

    @Override
    public List<SelectCategoryVo> getCategory1() {
        return this.list(new LambdaQueryWrapper<CategoryEntity>().eq(CategoryEntity::getCatLevel, 1)).stream().map(item -> {
            SelectCategoryVo selectCategoryVo = new SelectCategoryVo();
            selectCategoryVo.setId(item.getCategoryId().toString());
            selectCategoryVo.setName(item.getCategoryName());
            return selectCategoryVo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<SelectCategoryVo> getCategory2(String category1Id) {
        return this.list(new LambdaQueryWrapper<CategoryEntity>().eq(CategoryEntity::getCatLevel, 2)
                .eq(StrUtil.isNotBlank(category1Id), CategoryEntity::getParentCid, category1Id)).stream().map(item -> {
            SelectCategoryVo selectCategoryVo = new SelectCategoryVo();
            selectCategoryVo.setId(item.getCategoryId().toString());
            selectCategoryVo.setName(item.getCategoryName());
            return selectCategoryVo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<SelectCategoryVo> getCategory3(String category2Id) {
        return this.list(new LambdaQueryWrapper<CategoryEntity>().eq(CategoryEntity::getCatLevel, 3)
                .eq(StrUtil.isNotBlank(category2Id), CategoryEntity::getParentCid, category2Id)).stream().map(item -> {
            SelectCategoryVo selectCategoryVo = new SelectCategoryVo();
            selectCategoryVo.setId(item.getCategoryId().toString());
            selectCategoryVo.setName(item.getCategoryName());
            return selectCategoryVo;
        }).collect(Collectors.toList());
    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList,Long parentCid) {
        List<CategoryEntity> categoryEntities = selectList.stream().filter(item -> item.getParentCid().equals(parentCid)).collect(Collectors.toList());
        return categoryEntities;
        // return this.baseMapper.selectList(
        //         new QueryWrapper<CategoryEntity>().eq("parent_cid", parentCid));
    }

    private List<Long> findParentPath(Long catelogId, List<Long> paths) {

        //1、收集当前节点id
        paths.add(catelogId);

        //根据当前分类id查询信息
        CategoryEntity byId = this.getById(catelogId);
        //如果当前不是父分类
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }

        return paths;
    }

}