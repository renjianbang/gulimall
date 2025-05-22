package com.xunqi.gulimall.adminelementui.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pagehelper.PageInfo;
import com.xunqi.gulimall.adminelementui.service.AdminElementUIService;
import com.xunqi.gulimall.adminelementui.vo.TradeMarkVo;
import com.xunqi.gulimall.order.vo.MyOrderListVo;
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
 * @CreateTime 2025-05-22 09:46
 */
@RestController
@RequestMapping("/api/admin/product")
public class AdminElementUIController {

    @Resource
    private AdminElementUIService adminElementUIService;

    @ApiOperation("品牌列表")
    @GetMapping("/baseTrademark/{page}/{limit}")
    public R list(@PathVariable("page") Integer page,
                  @PathVariable("limit") Integer limit) {
        IPage<TradeMarkVo> resPage = adminElementUIService.tradeMarkList(page, limit);
        return R.ok().put("page", resPage);
    }

}
