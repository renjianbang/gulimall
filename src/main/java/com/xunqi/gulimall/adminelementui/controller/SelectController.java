package com.xunqi.gulimall.adminelementui.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xunqi.gulimall.adminelementui.vo.TradeMarkVo;
import com.xunqi.gulimall.product.service.AttrService;
import com.xunqi.gulimall.product.service.CategoryService;
import com.xunqi.gulimall.utils.Result;
import com.xunqi.gulimall.utils.product.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Description
 * @Author cisz
 * @CreateTime 2025-05-22 13:15
 */
@RestController
@RequestMapping("/api/admin/product")
public class SelectController {

//    export const reqCategory1List = ()=>request({url:'/admin/product/getCategory1',method:'get'});
//
//    export const reqCategory2List = (category1Id)=>request({url:`/admin/product/getCategory2/${category1Id}`,method:'get'});
//
//    export const reqCategory3List = (category2Id)=>request({url:`/admin/product/getCategory3/${category2Id}`,method:'get'});

    @Resource
    private CategoryService categoryService;

    @Resource
    private AttrService attrService;


    @ApiOperation("分类下拉框")
    @GetMapping("/getCategory1")
    public Result<?> getCategory1() {
        return Result.ok(categoryService.getCategory1());
    }

    @ApiOperation("分类下拉框")
    @GetMapping("/getCategory2/{category1Id}")
    public Result<?> getCategory2(@PathVariable("category1Id") String category1Id) {
        return Result.ok(categoryService.getCategory2(category1Id));
    }

    @ApiOperation("分类下拉框")
    @GetMapping("/getCategory3/{category2Id}")
    public Result<?> getCategory3(@PathVariable("category2Id") String category2Id) {
        return Result.ok(categoryService.getCategory3(category2Id));
    }


//    export const reqAttrList = (category1Id,category2Id,category3Id)=>request({url:`/api/admin/product/attrInfoList/${category1Id}/${category2Id}/${category3Id}`,method:'get'});
    @ApiOperation("分类下拉框")
    @GetMapping("/attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result<?> getCategory3(@PathVariable("category1Id") String category1Id,
                                    @PathVariable("category2Id") String category2Id,
                                  @PathVariable("category3Id") String category3Id
    ) {
        return Result.ok(attrService.getAttrList(category1Id, category2Id, category3Id));
    }
}
