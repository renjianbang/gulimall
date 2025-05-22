package com.xunqi.gulimall.adminelementui.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pagehelper.PageInfo;
import com.xunqi.gulimall.adminelementui.vo.TradeMarkVo;

/**
 * @Description 服务接口
 * @Author cisz
 * @CreateTime 2025-05-22 09:48
 */
public interface AdminElementUIService {
    IPage<TradeMarkVo> tradeMarkList(Integer page, Integer limit);
}
