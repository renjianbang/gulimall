package com.xunqi.gulimall.product.web;

import com.xunqi.gulimall.product.entity.CategoryEntity;
import com.xunqi.gulimall.product.service.CategoryService;
import com.xunqi.gulimall.product.service.MallSearchService;
import com.xunqi.gulimall.product.service.SkuInfoService;
import com.xunqi.gulimall.utils.product.R;

import com.xunqi.gulimall.product.vo.ProductListParam;
import com.xunqi.gulimall.product.vo.ProductListVo;
import com.xunqi.gulimall.product.vo.SkuItemVo;
import org.springframework.web.bind.annotation.*;


import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: 夏沫止水
 * @createTime: 2020-06-08 14:14
 **/
@CrossOrigin
@RequestMapping("/api")
@RestController
public class IndexController {

    @Resource
    private CategoryService categoryService;

    @Resource
    private MallSearchService mallSearchService;

    @Resource
    private SkuInfoService skuInfoService;


    //index/json/catalog.json
//    @GetMapping(value = "/product/getBaseCategoryList")
//    public Map<String, List<Catelog2Vo>> getCatalogJson() {
//        Map<String, List<Catelog2Vo>> catalogJson = categoryService.getCatalogJson();
////        return R.ok().put("data",entities);
//        return catalogJson;
//    }

    /**
     * 查询出所有分类以及子分类，以树形结构组装起来列表
     */
    @RequestMapping("/product/getBaseCategoryList")
    //@RequiresPermissions("product:category:list")
    public R getBaseCategoryList(){

        List<CategoryEntity> entities = categoryService.listWithTree();

        return R.ok().put("data", entities);
    }

    @PostMapping("/list")
    public R list(@RequestBody ProductListParam param){
        ProductListVo result = mallSearchService.listProducts(param);
        return R.ok().put("data", result);
    }


    /**
     * 展示当前sku的详情
     * @param skuId
     * @return
     */
    @GetMapping("/item/{skuId}")
    public R skuItem(@PathVariable("skuId") Long skuId) throws ExecutionException, InterruptedException {
        SkuItemVo vos = skuInfoService.item(skuId);
        return R.ok().put("data", vos);
    }



}
