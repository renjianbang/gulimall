package com.xunqi.gulimall.config.product;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author cisz
 * @CreateTime 2025-04-27 15:15
 */
@Configuration
public class ThreadPoolConfig {

    @Bean("threadPoolExecutor")
    public ThreadPoolExecutor threadPoolExecutor() {
        return new ThreadPoolExecutor(
                20,  // 核心线程数（根据服务器CPU核数调整）
                200, // 最大线程数
                10,  // 空闲线程存活时间（秒）
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100000), // 队列容量（根据业务峰值调整）
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy() // 拒绝策略
        );
    }

}
