package com.xunqi.gulimall.payconfig.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xunqi.gulimall.payconfig.entity.PayConfig;
import com.xunqi.gulimall.payconfig.dao.PayConfigMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xunqi.gulimall.payconfig.service.PayConfigService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 支付宝支付配置表 服务实现类
 * </p>
 *
 * @author renjianbang
 * @since 2025-05-07
 */
@Service
public class PayConfigServiceImpl extends ServiceImpl<PayConfigMapper, PayConfig> implements PayConfigService {

    @Override
    public PayConfig getPayConfig(Integer type) {
        return this.getOne(new LambdaQueryWrapper<PayConfig>()
                .eq(PayConfig::getType, type));
    }
}
