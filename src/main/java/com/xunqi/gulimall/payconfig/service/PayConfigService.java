package com.xunqi.gulimall.payconfig.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xunqi.gulimall.payconfig.entity.PayConfig;

/**
 * <p>
 * 支付宝支付配置表 服务类
 * </p>
 *
 * @author renjianbang
 * @since 2025-05-07
 */
public interface PayConfigService extends IService<PayConfig> {

    PayConfig getPayConfig(Integer type);

}
