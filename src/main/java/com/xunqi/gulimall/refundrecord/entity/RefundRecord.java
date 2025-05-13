package com.xunqi.gulimall.refundrecord.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 支付宝、微信退款记录表
 * </p>
 *
 * @author 
 * @since 2025-05-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_refund_record")
public class RefundRecord implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 退款申请人ID
     */
    private String refundApplicantId;

    /**
     * 退款申请时间
     */
    private Date refundApplicantTime;

    /**
     * 退款申请人姓名
     */
    private String refundApplicantName;

    /**
     * 退款完成时间
     */
    private Date refundCompleteTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 退款原因
     */
    private String refundReason;

    /**
     * 退款状态（0退款中/1退款成功/2退款失败/3已撤销）
     */
    private Integer refundStatus;

    /**
     * 退款流水号
     */
    private String refundSerialNo;

    /**
     * 支付平台退款结果详情（JSON字符串）
     */
    private String paymentPlatformResult;

    /**
     * 退款备注
     */
    private String refundRemark;

    /**
     * 0支付宝 1微信
     */
    private Integer type;


}
