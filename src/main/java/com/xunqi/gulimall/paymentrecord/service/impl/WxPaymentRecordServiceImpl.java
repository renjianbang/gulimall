package com.xunqi.gulimall.paymentrecord.service.impl;

import cn.hutool.core.date.DateException;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.xunqi.gulimall.common.enmus.OrderStatus;
import com.xunqi.gulimall.paymentrecord.dao.PaymentRecordMapper;
import com.xunqi.gulimall.paymentrecord.entity.PaymentRecord;
import com.xunqi.gulimall.paymentrecord.service.PaymentRecordService;
import com.xunqi.gulimall.paymentrecord.service.WxPaymentRecordService;
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
 * 支付宝、微信支付记录表 服务实现类
 * </p>
 *
 * @author renjianbang
 * @since 2025-05-07
 */
@Slf4j
@Service
public class WxPaymentRecordServiceImpl extends ServiceImpl<PaymentRecordMapper, PaymentRecord> implements WxPaymentRecordService {

    @Override
    public PaymentRecord createPaymentRecord(String orderNo) {
        // 查找已存在的支付记录
        PaymentRecord one = this.getOne(new LambdaQueryWrapper<PaymentRecord>()
                .eq(PaymentRecord::getOrderId, orderNo)
                .eq(PaymentRecord::getPaymentStatus, 0)
                .last("limit 1")
        );
        if (one != null) {
            return one;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getPrincipal().toString();

        PaymentRecord record = new PaymentRecord();
        record.setOrderId(orderNo);
        record.setUserId(userName);
        record.setCreateTime(new Date(System.currentTimeMillis()));
        record.setPaymentStatus(0);
        record.setType(1);
        this.save(record);
        return record;
    }

    @Override
    public void saveCodeUrl(String orderNo, String codeUrl) {
        QueryWrapper<PaymentRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderNo);

        PaymentRecord record = new PaymentRecord();
        record.setCodeUrl(codeUrl);
        baseMapper.update(record, queryWrapper);
    }

    @Override
    public void updatePaymentInfo(String plainText) {
        log.info("更新支付日志");
        Gson gson = new Gson();
        HashMap plainTextMap = gson.fromJson(plainText, HashMap.class);
        //订单号
        String orderNo = (String)plainTextMap.get("out_trade_no");
        //业务编号
        String transactionId = (String)plainTextMap.get("transaction_id");
        //支付类型
        String tradeType = (String)plainTextMap.get("trade_type");
        //交易状态
        String tradeState = (String)plainTextMap.get("trade_state");
        //用户实际支付金额
        Map<String, Object> amount = (Map)plainTextMap.get("amount");
        //支付完成时间
        String successTime = (String)plainTextMap.get("success_time");
        // 获取分单位的整型值
        Object payerTotalObj = amount.get("payer_total");

        // 安全转换逻辑
        Integer payerTotalCents = 0;
        if (payerTotalObj instanceof Number) {
            payerTotalCents = ((Number) payerTotalObj).intValue();
        } else {
            throw new IllegalArgumentException("payer_total字段类型不合法");
        }
        // 转换为元单位（保留2位小数）
        BigDecimal payerTotalBigDecimal = new BigDecimal(payerTotalCents).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);

        QueryWrapper<PaymentRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", orderNo);
        wrapper.eq("type", 1);

        PaymentRecord paymentInfo = new PaymentRecord();
        paymentInfo.setPayId(IdUtil.getSnowflakeNextIdStr());
        paymentInfo.setOrderId(orderNo);
        paymentInfo.setPaymentCompleteTime(DateUtil.parse(successTime));
        paymentInfo.setPaymentStatus(1);
        paymentInfo.setPaymentAmount(payerTotalBigDecimal);
        paymentInfo.setPaymentSerialNo(transactionId);
        paymentInfo.setPaymentResult(plainText);

        baseMapper.update(paymentInfo, wrapper);
    }

    @Override
    public void updateStatusByOrderNo(String orderNo, OrderStatus orderStatus) {
        log.info("更新订单记录状态 ===> {}", orderStatus.getType());

        QueryWrapper<PaymentRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderNo);


        PaymentRecord record = new PaymentRecord();
        switch (orderStatus) {
            case CANCEL:
                record.setPaymentStatus(2);
                break;
//            case REFUND:
//                record.setPaymentStatus(3);
//                break;
//            case CLOSE:
//                record.setPaymentStatus(4);
//                break;
            default:
                throw new IllegalArgumentException("Invalid order status: " + orderStatus);
        }
        baseMapper.update(record, queryWrapper);
    }

    @Override
    public Integer getOrderStatus(String orderNo) {
        PaymentRecord one = this.getOne(new LambdaQueryWrapper<PaymentRecord>().eq(PaymentRecord::getOrderId, orderNo).last("limit 1"));
        if (one == null) {
            throw new RuntimeException("支付订单不存在");
        }
        return one.getPaymentStatus();
    }
}
