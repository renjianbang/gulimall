package com.xunqi.gulimall.wxpay.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import com.xunqi.gulimall.common.enmus.OrderStatus;
import com.xunqi.gulimall.common.enmus.WxApiType;
import com.xunqi.gulimall.common.enmus.WxNotifyType;
import com.xunqi.gulimall.order.service.OrderService;
import com.xunqi.gulimall.payconfig.entity.PayConfig;
import com.xunqi.gulimall.payconfig.service.PayConfigService;
import com.xunqi.gulimall.paymentrecord.entity.PaymentRecord;
import com.xunqi.gulimall.paymentrecord.service.WxPaymentRecordService;
import com.xunqi.gulimall.refundrecord.entity.RefundRecord;
import com.xunqi.gulimall.refundrecord.service.WxRefundRecordService;
import com.xunqi.gulimall.wxpay.service.WxPayService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.annotation.Resource;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description
 * @Author cisz
 * @CreateTime 2025-05-08 14:53
 */
@Slf4j
@Service
public class WxPayServiceImpl implements WxPayService {

    @Resource
    private OrderService orderService;

    @Resource
    private WxPaymentRecordService wxPaymentRecordService;

    @Resource
    private CloseableHttpClient wxPayClient;

    @Resource
    private PayConfigService payConfigService;

    @Resource
    private WxRefundRecordService wxRefundRecordService;

    @Resource
    private CloseableHttpClient wxPayNoSignClient; //无需应答签名


    private final ReentrantLock lock = new ReentrantLock();


    /**
     * 调用Native支付接口
     * @param orderNo
     * @return code_url 和 订单号
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> nativePay(String orderNo) throws IOException {
        // 生成订单记录
        PaymentRecord record = wxPaymentRecordService.createPaymentRecord(orderNo);
        if (record == null) {
            log.error("生成订单记录失败");
            throw new RuntimeException("未知异常");
        }
        String codeUrl = record.getCodeUrl();
        if (StrUtil.isNotBlank(codeUrl)) {
            log.info("订单已存在，二维码已保存");
            //返回二维码
            Map<String, Object> map = new HashMap<>();
            map.put("codeUrl", codeUrl);
            map.put("orderNo", record.getOrderId());
            return map;
        }
        
        //调用统一下单API
        PayConfig wxPayConfig = payConfigService.getPayConfig(1);
        if (wxPayConfig == null) {
            log.error("未找到支付配置");
            throw new RuntimeException("未找到支付配置");
        }
        HttpPost httpPost = new HttpPost(wxPayConfig.getGatewayUrl().concat(WxApiType.NATIVE_PAY.getType()));

        // 请求body参数
        Gson gson = new Gson();
        Map paramsMap = new HashMap();
        paramsMap.put("appid", wxPayConfig.getAppId());
        paramsMap.put("mchid", wxPayConfig.getMchId());
        paramsMap.put("description", "description");
        paramsMap.put("out_trade_no", orderNo); 
        paramsMap.put("notify_url", wxPayConfig.getNotifyUrl().concat(WxNotifyType.NATIVE_NOTIFY.getType()));

        Map amountMap = new HashMap();
        /** yuanAmount.multiply(new BigDecimal(100))
         .setScale(0, RoundingMode.HALF_UP)
         .intValueExact() **/
        amountMap.put("total", 1);
        amountMap.put("currency", "CNY");

        paramsMap.put("amount", amountMap);

        // 将参数转换成json字符串
        String jsonParams = gson.toJson(paramsMap);
        log.info("请求参数 ===> {}" + jsonParams);

        StringEntity entity = new StringEntity(jsonParams,"utf-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");

        // 完成签名并执行请求
        CloseableHttpResponse response = wxPayClient.execute(httpPost);

        try {
            String bodyAsString = EntityUtils.toString(response.getEntity());//响应体
            int statusCode = response.getStatusLine().getStatusCode();//响应状态码
            if (statusCode == 200) { //处理成功
                log.info("成功, 返回结果 = " + bodyAsString);
            } else if (statusCode == 204) { //处理成功，无返回Body
                log.info("成功");
            } else {
                log.info("Native下单失败,响应码 = " + statusCode+ ",返回结果 = " + bodyAsString);
                throw new IOException("request failed");
            }
            // 响应结果
            Map<String, String> resultMap = gson.fromJson(bodyAsString, HashMap.class);
            //二维码
            codeUrl = resultMap.get("code_url");
            // 保存二维码
            wxPaymentRecordService.saveCodeUrl(orderNo, codeUrl);
            // 返回二维码
            Map<String, Object> map = new HashMap<>();
            map.put("codeUrl", codeUrl);
            map.put("orderNo", orderNo);
            return map;
        } finally {
            response.close();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void processOrder(Map<String, Object> bodyMap) throws GeneralSecurityException {
        log.info("处理订单");
        //解密报文
        String plainText = decryptFromResource(bodyMap);

        //将明文转换成map
        Gson gson = new Gson();
        HashMap plainTextMap = gson.fromJson(plainText, HashMap.class);
        String orderNo = (String)plainTextMap.get("out_trade_no");

        /*在对业务数据进行状态检查和处理之前，
        要采用数据锁进行并发控制，
        以避免函数重入造成的数据混乱*/
        //尝试获取锁：
        // 成功获取则立即返回true，获取失败则立即返回false。不必一直等待锁的释放
        if (lock.tryLock()) {
            try {
                //处理重复的通知
                //接口调用的幂等性：无论接口被调用多少次，产生的结果是一致的。
                Integer orderStatus = wxPaymentRecordService.getOrderStatus(orderNo);
                if (orderStatus != 0) {
                    return;
                }

                //模拟通知并发
//                try {
//                    TimeUnit.SECONDS.sleep(5);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

                //更新订单状态
                orderService.updateStatusByOrderNo(orderNo, OrderStatus.SUCCESS);
                //更新支付日志
                wxPaymentRecordService.updatePaymentInfo(plainText);
            } finally {
                //要主动释放锁
                lock.unlock();
            }
        }
    }

    /**
     * 用户取消订单
     * @param orderNo
     */
    @Transactional
    @Override
    public void cancelOrder(String orderNo) throws Exception {

        //调用微信支付的关单接口
        this.closeOrder(orderNo);

        //更新商户端的订单状态 更新支付记录
        orderService.updateStatusByOrderNo(orderNo, OrderStatus.CANCEL);
        wxPaymentRecordService.updateStatusByOrderNo(orderNo, OrderStatus.CANCEL);
    }

    @Override
    public String queryOrder(String orderNo) throws Exception {
        log.info("查单接口调用 ===> {}", orderNo);
        PayConfig wxPayConfig = payConfigService.getPayConfig(1);
        String url = String.format(WxApiType.ORDER_QUERY_BY_NO.getType(), orderNo);
        url = wxPayConfig.getGatewayUrl().concat(url).concat("?mchid=").concat(wxPayConfig.getMchId());

        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "application/json");

        //完成签名并执行请求
        CloseableHttpResponse response = wxPayClient.execute(httpGet);

        try {
            String bodyAsString = EntityUtils.toString(response.getEntity());//响应体
            int statusCode = response.getStatusLine().getStatusCode();//响应状态码
            if (statusCode == 200) { //处理成功
                log.info("成功, 返回结果 = " + bodyAsString);
            } else if (statusCode == 204) { //处理成功，无返回Body
                log.info("成功");
            } else {
                log.info("查单接口调用,响应码 = " + statusCode+ ",返回结果 = " + bodyAsString);
                throw new IOException("request failed");
            }

            return bodyAsString;

        } finally {
            response.close();
        }

    }

    /**
     * 根据订单号查询微信支付查单接口，核实订单状态
     * 如果订单已支付，则更新商户端订单状态，并记录支付日志
     * 如果订单未支付，则调用关单接口关闭订单，并更新商户端订单状态
     * @param orderNo
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void checkOrderStatus(String orderNo) throws Exception {

        log.warn("根据订单号核实订单状态 ===> {}", orderNo);

        //调用微信支付查单接口
        String result = this.queryOrder(orderNo);

        Gson gson = new Gson();
        Map<String, String> resultMap = gson.fromJson(result, HashMap.class);

        //获取微信支付端的订单状态
        String tradeState = resultMap.get("trade_state");

        //判断订单状态
//        if (WxTradeState.SUCCESS.getType().equals(tradeState)) {
//
//            log.warn("核实订单已支付 ===> {}", orderNo);
//
//            //如果确认订单已支付则更新本地订单状态
//            orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.SUCCESS);
//            //记录支付日志
//            paymentInfoService.createPaymentInfo(result);
//        }
//
//        if(WxTradeState.NOTPAY.getType().equals(tradeState)){
//            log.warn("核实订单未支付 ===> {}", orderNo);
//
//            //如果订单未支付，则调用关单接口
//            this.closeOrder(orderNo);
//
//            //更新本地订单状态
//            orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.CLOSED);
//        }

    }

    /**
     * 退款
     * @param orderNo
     * @param reason
     * @throws IOException
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void refund(String orderNo, String reason) throws Exception {
        String payId = IdUtil.getSnowflakeNextIdStr();
        log.info("调用退款API");
        //调用统一下单API
        PayConfig wxPayConfig = payConfigService.getPayConfig(1);
        String url = wxPayConfig.getGatewayUrl().concat(WxApiType.DOMESTIC_REFUNDS.getType());
        HttpPost httpPost = new HttpPost(url);

        // 请求body参数
        Gson gson = new Gson();
        Map paramsMap = new HashMap();
        paramsMap.put("out_trade_no", orderNo);//订单编号
        paramsMap.put("out_refund_no", payId);//退款单编号
        paramsMap.put("reason", reason);//退款原因
        paramsMap.put("notify_url", wxPayConfig.getNotifyUrl().concat(WxNotifyType.REFUND_NOTIFY.getType()));//退款通知地址

        Map amountMap = new HashMap();
        /** yuanAmount.multiply(new BigDecimal(100))
         .setScale(0, RoundingMode.HALF_UP)
         .intValueExact() **/
        amountMap.put("refund", 1);//退款金额
        amountMap.put("total", 1);//原订单金额
        amountMap.put("currency", "CNY");//退款币种
        paramsMap.put("amount", amountMap);

        //将参数转换成json字符串
        String jsonParams = gson.toJson(paramsMap);
        log.info("请求参数 ===> {}" + jsonParams);

        StringEntity entity = new StringEntity(jsonParams,"utf-8");
        entity.setContentType("application/json");//设置请求报文格式
        httpPost.setEntity(entity);//将请求报文放入请求对象
        httpPost.setHeader("Accept", "application/json");//设置响应报文格式

        //完成签名并执行请求，并完成验签
        CloseableHttpResponse response = wxPayClient.execute(httpPost);

        try {
            //解析响应结果
            String bodyAsString = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                log.info("成功, 退款返回结果 = " + bodyAsString);
            } else if (statusCode == 204) {
                log.info("成功");
            } else {
                throw new RuntimeException("退款异常, 响应码 = " + statusCode+ ", 退款返回结果 = " + bodyAsString);
            }
            //更新退款单
            orderService.updateStatusByOrderNo(orderNo, OrderStatus.REFUND_PROCESSING);

            log.info("创建退款单记录");
            //根据订单编号创建退款单 -- 记录
            wxRefundRecordService.createRefundByOrderNo(orderNo, reason, payId, bodyAsString);

        } finally {
            response.close();
        }
    }


    /**
     * 查询退款接口调用
     * @param refundNo
     * @return
     */
    @Override
    public String queryRefund(String refundNo) throws Exception {

        log.info("查询退款接口调用 ===> {}", refundNo);

        PayConfig wxPayConfig = payConfigService.getPayConfig(1);
        String url =  String.format(WxApiType.DOMESTIC_REFUNDS_QUERY.getType(), refundNo);
        url = wxPayConfig.getGatewayUrl().concat(url);

        //创建远程Get 请求对象
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "application/json");

        //完成签名并执行请求
        CloseableHttpResponse response = wxPayClient.execute(httpGet);

        try {
            String bodyAsString = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                log.info("成功, 查询退款返回结果 = " + bodyAsString);
            } else if (statusCode == 204) {
                log.info("成功");
            } else {
                throw new RuntimeException("查询退款异常, 响应码 = " + statusCode+ ", 查询退款返回结果 = " + bodyAsString);
            }

            return bodyAsString;

        } finally {
            response.close();
        }
    }

    /**
     * 根据退款单号核实退款单状态
     * @param refundNo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void checkRefundStatus(String refundNo) throws Exception {

        log.warn("根据退款单号核实退款单状态 ===> {}", refundNo);

        //调用查询退款单接口
        String result = this.queryRefund(refundNo);

        //组装json请求体字符串
        Gson gson = new Gson();
        Map<String, String> resultMap = gson.fromJson(result, HashMap.class);

        //获取微信支付端退款状态
        String status = resultMap.get("status");

        String orderNo = resultMap.get("out_trade_no");

//        if (WxRefundStatus.SUCCESS.getType().equals(status)) {
//
//            log.warn("核实订单已退款成功 ===> {}", refundNo);
//
//            //如果确认退款成功，则更新订单状态
//            orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.REFUND_SUCCESS);
//
//            //更新退款单
//            refundsInfoService.updateRefund(result);
//        }
//
//        if (WxRefundStatus.ABNORMAL.getType().equals(status)) {
//
//            log.warn("核实订单退款异常  ===> {}", refundNo);
//
//            //如果确认退款成功，则更新订单状态
//            orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.REFUND_ABNORMAL);
//
//            //更新退款单
//            refundsInfoService.updateRefund(result);
//        }
    }

    /**
     * 处理退款单
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void processRefund(Map<String, Object> bodyMap) throws Exception {
        log.info("退款单");
        //解密报文
        String plainText = decryptFromResource(bodyMap);

        //将明文转换成map
        Gson gson = new Gson();
        HashMap plainTextMap = gson.fromJson(plainText, HashMap.class);
        String orderNo = (String)plainTextMap.get("out_trade_no");

        if (lock.tryLock()) {
            try {
                Integer orderStatus = wxRefundRecordService.getOrderStatus(orderNo);
                if (orderStatus != 0) {
                    return;
                }

                //更新订单状态
                orderService.updateStatusByOrderNo(orderNo, OrderStatus.REFUND_SUCCESS);

                //更新退款单
                wxRefundRecordService.updateRefund(plainText, orderNo);

            } finally {
                //要主动释放锁
                lock.unlock();
            }
        }
    }

    /**
     * 申请账单
     * @param billDate
     * @param type
     * @return
     * @throws Exception
     */
    @Override
    public String queryBill(String billDate, String type) throws Exception {
        log.warn("申请账单接口调用 {}", billDate);

        String url = "";
        if("tradebill".equals(type)){
            url =  WxApiType.TRADE_BILLS.getType();
        }else if("fundflowbill".equals(type)){
            url =  WxApiType.FUND_FLOW_BILLS.getType();
        }else{
            throw new RuntimeException("不支持的账单类型");
        }
        PayConfig wxPayConfig = payConfigService.getPayConfig(1);
        url = wxPayConfig.getGatewayUrl().concat(url).concat("?bill_date=").concat(billDate);

        //创建远程Get 请求对象
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Accept", "application/json");

        //使用wxPayClient发送请求得到响应
        CloseableHttpResponse response = wxPayClient.execute(httpGet);

        try {

            String bodyAsString = EntityUtils.toString(response.getEntity());

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                log.info("成功, 申请账单返回结果 = " + bodyAsString);
            } else if (statusCode == 204) {
                log.info("成功");
            } else {
                throw new RuntimeException("申请账单异常, 响应码 = " + statusCode+ ", 申请账单返回结果 = " + bodyAsString);
            }

            //获取账单下载地址
            Gson gson = new Gson();
            Map<String, String> resultMap = gson.fromJson(bodyAsString, HashMap.class);
            return resultMap.get("download_url");

        } finally {
            response.close();
        }
    }

    /**
     * 下载账单
     * @param billDate
     * @param type
     * @return
     * @throws Exception
     */
    @Override
    public String downloadBill(String billDate, String type) throws Exception {
        log.warn("下载账单接口调用 {}, {}", billDate, type);

        //获取账单url地址
        String downloadUrl = this.queryBill(billDate, type);
        //创建远程Get 请求对象
        HttpGet httpGet = new HttpGet(downloadUrl);
        httpGet.addHeader("Accept", "application/json");

        //使用wxPayClient发送请求得到响应
        CloseableHttpResponse response = wxPayNoSignClient.execute(httpGet);

        try {

            String bodyAsString = EntityUtils.toString(response.getEntity());

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                log.info("成功, 下载账单返回结果 = " + bodyAsString);
            } else if (statusCode == 204) {
                log.info("成功");
            } else {
                throw new RuntimeException("下载账单异常, 响应码 = " + statusCode+ ", 下载账单返回结果 = " + bodyAsString);
            }

            return bodyAsString;

        } finally {
            response.close();
        }
    }


    @Override
    public Integer getOrderStatus(String orderNo) {
        QueryWrapper<PaymentRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderNo);
        queryWrapper.last("limit 1");
        PaymentRecord record = wxPaymentRecordService.getOne(queryWrapper);
        if (record == null) {
            return -1;
        }
        return record.getPaymentStatus();
    }

    /**
     * 关单接口的调用
     * @param orderNo
     */
    private void closeOrder(String orderNo) throws Exception {
        log.info("关单接口的调用，订单号 ===> {}", orderNo);
        //创建远程请求对象
        PayConfig wxPayConfig = payConfigService.getPayConfig(1);
        String url = String.format(WxApiType.CLOSE_ORDER_BY_NO.getType(), orderNo);
        url = wxPayConfig.getGatewayUrl().concat(url);
        HttpPost httpPost = new HttpPost(url);

        //组装json请求体
        Gson gson = new Gson();
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("mchid", wxPayConfig.getMchId());
        String jsonParams = gson.toJson(paramsMap);
        log.info("请求参数 ===> {}", jsonParams);

        //将请求参数设置到请求对象中
        StringEntity entity = new StringEntity(jsonParams,"utf-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");

        //完成签名并执行请求
        CloseableHttpResponse response = wxPayClient.execute(httpPost);

        try {
            int statusCode = response.getStatusLine().getStatusCode();//响应状态码
            if (statusCode == 200) { //处理成功
                log.info("成功200");
            } else if (statusCode == 204) { //处理成功，无返回Body
                log.info("成功204");
            } else {
                log.info("Native下单失败,响应码 = " + statusCode);
                throw new IOException("request failed");
            }
        } finally {
            response.close();
        }
    }

    /**
     * 对称解密
     * @param bodyMap
     * @return
     */
    private String decryptFromResource(Map<String, Object> bodyMap) throws GeneralSecurityException {

        log.info("密文解密");

        //通知数据
        Map<String, String> resourceMap = (Map) bodyMap.get("resource");
        //数据密文
        String ciphertext = resourceMap.get("ciphertext");
        //随机串
        String nonce = resourceMap.get("nonce");
        //附加数据
        String associatedData = resourceMap.get("associated_data");

        log.info("密文 ===> {}", ciphertext);
        PayConfig wxPayConfig = payConfigService.getPayConfig(1);
        AesUtil aesUtil = new AesUtil(wxPayConfig.getAesKey().getBytes(StandardCharsets.UTF_8));
        String plainText = aesUtil.decryptToString(associatedData.getBytes(StandardCharsets.UTF_8),
                nonce.getBytes(StandardCharsets.UTF_8),
                ciphertext);

        log.info("明文 ===> {}", plainText);

        return plainText;
    }

}
