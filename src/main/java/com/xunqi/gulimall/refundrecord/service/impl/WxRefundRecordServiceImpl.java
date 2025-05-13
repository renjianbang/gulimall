package com.xunqi.gulimall.refundrecord.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.xunqi.gulimall.common.enmus.OrderStatus;
import com.xunqi.gulimall.refundrecord.dao.RefundRecordMapper;
import com.xunqi.gulimall.refundrecord.entity.RefundRecord;
import com.xunqi.gulimall.refundrecord.service.RefundRecordService;
import com.xunqi.gulimall.refundrecord.service.WxRefundRecordService;
import com.xunqi.gulimall.utils.wxpayment.OrderNoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 支付宝、微信退款记录表 服务实现类
 * </p>
 *
 * @author 
 * @since 2025-05-09
 */
@Slf4j
@Service
public class WxRefundRecordServiceImpl extends ServiceImpl<RefundRecordMapper, RefundRecord> implements WxRefundRecordService {

    @Override
    public void createRefundByOrderNo(String orderNo, String reason, String payId, String content) {
        //将json字符串转换成Map
        Gson gson = new Gson();
        Map<String, String> resultMap = gson.fromJson(content, HashMap.class);

        String createTime = resultMap.get("create_time");

        //根据订单号获取订单信息
//        OrderInfo orderInfo = orderInfoService.getOrderByOrderNo(orderNo);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getPrincipal().toString();

        RefundRecord refundRecord = new RefundRecord();
        String out_refund_no = resultMap.get("out_refund_no"); //【商户退款单号】 商户申请退款时传的商户系统内部退款单号。
        refundRecord.setPayId(payId);
        refundRecord.setOrderId(orderNo);
        refundRecord.setRefundApplicantId(userId);
        Date now = new Date(System.currentTimeMillis());
        refundRecord.setRefundApplicantTime(DateUtil.parse(createTime));
        refundRecord.setRefundApplicantName(userId);
        refundRecord.setCreateTime(now);

        refundRecord.setRefundReason(reason);
        refundRecord.setRefundStatus(0);
        refundRecord.setRefundSerialNo(resultMap.get("refund_id")); //【微信支付退款单号】申请退款受理成功时，该笔退款单在微信支付侧生成的唯一标识。
        refundRecord.setPaymentPlatformResult(content);
        refundRecord.setType(1);

        //保存退款订单记录
        baseMapper.insert(refundRecord);
    }

    @Override
    public void updateRefund(String content, String orderNo) {
        //将json字符串转换成Map
        Gson gson = new Gson();
        Map resultMap = gson.fromJson(content, HashMap.class);

        //根据退款单编号修改退款单
        QueryWrapper<RefundRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderNo);

        //设置要修改的字段
        RefundRecord refundRecord = new RefundRecord();

//        refundInfo.setRefundId(resultMap.get("refund_id"));//微信支付退款单号

        /**
         * 【退款状态】 退款状态：
         * SUCCESS—退款成功
         * CLOSED—退款关闭。
         * PROCESSING—退款处理中
         * ABNORMAL—退款异常，退款到银行发现用户的卡作废或者冻结了，导致原路退款银行卡失败，可前往商户平台-交易中心，手动处理此笔退款，可参考： 退款异常的处理，或者通过发起异常退款接口进行处理。
         */
        String refundStatus = (String) resultMap.get("refund_status");
        switch (refundStatus) {
            case "SUCCESS":
                refundRecord.setRefundStatus(1); //退款状态（0退款中/1退款成功/2退款失败/3已撤销）
                break;
            case "CLOSED":
//                refundRecord.setRefundStatus(2);
                log.info("退款关闭");
                break;
            case "PROCESSING":
                refundRecord.setRefundStatus(0);
                break;
            case "ABNORMAL":
                refundRecord.setRefundStatus(2);
                break;
        }
        //  `refund_complete_time` datetime DEFAULT NULL COMMENT '退款完成时间',
        //  `refund_amount` decimal(50,2) DEFAULT NULL COMMENT '退款金额',
        //  `refund_reason` varchar(255) DEFAULT NULL COMMENT '退款原因',
        refundRecord.setRefundCompleteTime(DateUtil.parse(resultMap.get("success_time").toString()));
        //用户实际支付金额
        Map<String, Object> amountMap = (Map)resultMap.get("amount");
        // 获取分单位的整型值
        Double refund = (Double) amountMap.get("refund");
        // 转换为元单位（保留2位小数）
        BigDecimal refundBigDecimal = new BigDecimal(refund).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        refundRecord.setRefundAmount(refundBigDecimal);
        refundRecord.setPaymentPlatformResult(content);
        refundRecord.setType(1);
        //更新退款单
        baseMapper.update(refundRecord, queryWrapper);
    }

    @Override
    public Integer getOrderStatus(String orderNo) {
        RefundRecord one = this.getOne(new LambdaQueryWrapper<RefundRecord>().eq(RefundRecord::getOrderId, orderNo).last("limit 1"));
        if (one == null) {
            throw new RuntimeException("退款订单不存在");
        }
        return one.getRefundStatus();
    }


}
