package com.xunqi.gulimall.paymentrecord.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 支付宝、微信支付记录表
 * </p>
 *
 * @author renjianbang
 * @since 2025-05-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_payment_record")
public class
PaymentRecord implements Serializable {

    private static final long serialVersionUID = 1L;


    private String codeUrl;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 支付ID
     */
    private String payId;

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 支付完成时间
     */
    private Date paymentCompleteTime;

    /**
     * 支付过期时间
     */
    private Date paymentExpireTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 支付状态（0未支付/1已支付/3过期未支付）
     */
    private Integer paymentStatus;

    /**
     * 支付金额
     */
    private BigDecimal paymentAmount;

    /**
     * 支付手续费
     */
    private BigDecimal paymentFee;

    /**
     * 支付流水号
     */
    private String paymentSerialNo;

    /**
     * 支付结果详情（JSON字符串）
     */
    private String paymentResult;

    /**
     * 对账状态（0未对账/1对账成功/2对账失败）
     */
    private Integer reconciliationStatus;

    /**
     * 对账时间
     */
    private Date reconciliationTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 0支付宝 1微信
     */
    private Integer type;


}
