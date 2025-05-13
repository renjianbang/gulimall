package com.xunqi.gulimall.common.enmus;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description
 * @Author cisz
 * @CreateTime 2025-05-08 15:27
 */
@AllArgsConstructor
@Getter
public enum OrderStatus {
    /**
     * 未支付
     */
    NOTPAY("未支付"),

    /**
     * 支付成功
     */
    SUCCESS("已支付"),

    /**
     * 已关闭
     */
    CLOSED("过期未支付"),

    /**
     * 已取消
     */
    CANCEL("用户已取消"),

    /**
     * 退款中
     */
    REFUND_PROCESSING("退款中"),

    /**
     * 已退款
     */
    REFUND_SUCCESS("已退款"),

    /**
     * 退款异常
     */
    REFUND_ABNORMAL("退款异常");

    /**
     * 类型
     */
    private final String type;
}
