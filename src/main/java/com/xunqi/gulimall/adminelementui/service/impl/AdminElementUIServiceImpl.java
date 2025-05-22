package com.xunqi.gulimall.adminelementui.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xunqi.gulimall.adminelementui.service.AdminElementUIService;
import com.xunqi.gulimall.adminelementui.vo.TradeMarkVo;
import com.xunqi.gulimall.order.vo.MyOrderListVo;
import com.xunqi.gulimall.product.entity.BrandEntity;
import com.xunqi.gulimall.product.service.BrandService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author cisz
 * @CreateTime 2025-05-22 09:48
 */
@Service
public class AdminElementUIServiceImpl implements AdminElementUIService {

    @Resource
    private BrandService brandService;

    @Override
    public IPage<TradeMarkVo> tradeMarkList(Integer pageNum, Integer limit) {
        //使用brandService分页查询数据库
        Page<BrandEntity> page = new Page<>(pageNum, limit);
        IPage<BrandEntity> brandEntityIPage = brandService.page(page);
        //转化为TradeMarkVo类型
        IPage<TradeMarkVo> tradeMarkVoIPage = new Page<>();
        tradeMarkVoIPage.setCurrent(brandEntityIPage.getCurrent());
        tradeMarkVoIPage.setPages(brandEntityIPage.getPages());
        tradeMarkVoIPage.setSize(brandEntityIPage.getSize());
        tradeMarkVoIPage.setTotal(brandEntityIPage.getTotal());
        tradeMarkVoIPage.setRecords(brandEntityIPage.getRecords().stream().map(brandEntity -> {
            TradeMarkVo tradeMarkVo = new TradeMarkVo();
            tradeMarkVo.setId(brandEntity.getBrandId().toString());
            tradeMarkVo.setTmName(brandEntity.getName());
            tradeMarkVo.setLogoUrl(brandEntity.getLogo());
            return tradeMarkVo;
        }).collect(Collectors.toList()));
        return tradeMarkVoIPage;
    }


}
