package com.xunqi.gulimall.payconfig.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 支付宝支付配置表
 * </p>
 *
 * @author renjianbang
 * @since 2025-05-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_pay_config")
public class PayConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 支付宝应用ID/微信应用ID
     */
    private String appId;

    /**
     * 商户私钥/微信商户私钥
     */
    private String merchantPrivateKey;

    /**
     * 支付宝公钥
     */
    private String alipayPublicKey;

    /**
     * 签名类型
     */
    private String signType;

    /**
     * 支付宝网关地址/微信服务器地址
     */
    private String gatewayUrl;

    /**
     * 异步通知地址
     */
    private String notifyUrl;

    /**
     * 同步跳转地址
     */
    private String returnUrl;

    /**
     * 编码格式
     */
    private String charset;

    /**
     * 数据格式
     */
    private String format;

    /**
     * 加密秘钥/微信API V3 秘钥
     */
    private String aesKey;

    /**
     * 应用公钥证书SN
     */
    private String appCertSn;

    /**
     * 支付宝根证书SN
     */
    private String alipayRootCertSn;

    /**
     * 状态（0禁用/1启用）
     */
    private Integer status;

    /**
     * 配置名称
     */
    private String configName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 应用类型（WEB/APP/MINI_PROGRAM）
     */
    private String appType;

    /**
     * 环境标识（SANDBOX/PRODUCTION）
     */
    private String env;

    /**
     * 接口白名单
     */
    private String apiWhitelist;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 微信商户号
     */
    private String mchId;

    /**
     * 微信商户API证书序列号
     */
    private String mchSerialNo;

    /**
     * 0支付宝 1微信
     */
    private Integer type;


}
