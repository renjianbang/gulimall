package com.xunqi.gulimall.wxpay.controller;

import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import com.wechat.pay.contrib.apache.httpclient.auth.Verifier;
import com.xunqi.gulimall.common.enmus.OrderStatus;
import com.xunqi.gulimall.utils.Result;
import com.xunqi.gulimall.utils.wxpayment.HttpUtils;
import com.xunqi.gulimall.utils.wxpayment.WechatPay2ValidatorForRequest;
import com.xunqi.gulimall.wxpay.service.WxPayService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author cisz
 * @CreateTime 2025-05-08 14:44
 */
@Slf4j
@RestController
@RequestMapping("/api/wx-pay")
public class WxPayController {

    @Resource
    private WxPayService wxPayService;

    @Resource
    private Verifier verifier;

    /**
     * Native下单
     * @param orderNo
     * @return
     * @throws Exception
     */
    ///api/wx-pay/native/
    @ApiOperation("调用统一下单API，生成支付二维码")
    @PostMapping("/native/{orderNo}")
    public Result<?> nativePay(@PathVariable String orderNo) throws Exception {
        log.info("发起支付请求 v3");
        // 返回支付二维码连接和订单号
        if (StrUtil.isBlank(orderNo)) {
            return Result.error("参数为空");
        }
        Map<String, Object> map = wxPayService.nativePay(orderNo);
        return Result.ok(map);
    }

    /**
     * 支付通知
     * 微信支付通过支付通知接口将用户支付成功消息通知给商户
     */
    @ApiOperation("支付通知")
    @PostMapping("/native/notify")
    public String nativeNotify(HttpServletRequest request, HttpServletResponse response) {
        Gson gson = new Gson();
        Map<String, String> map = new HashMap<>();//应答对象
        try {
            //处理通知参数
            String body = HttpUtils.readData(request);
            Map<String, Object> bodyMap = gson.fromJson(body, HashMap.class);
            String requestId = (String)bodyMap.get("id");
            log.info("支付通知的id ===> {}", requestId);
            //log.info("支付通知的完整数据 ===> {}", body);
            //int a = 9 / 0;

            //签名的验证
            WechatPay2ValidatorForRequest wechatPay2ValidatorForRequest = new WechatPay2ValidatorForRequest(verifier, requestId, body);
            if (!wechatPay2ValidatorForRequest.validate(request)) {
                log.error("通知验签失败");
                //失败应答
                response.setStatus(500);
                map.put("code", "ERROR");
                map.put("message", "通知验签失败");
                return gson.toJson(map);
            }
            log.info("通知验签成功");
            //处理订单
            wxPayService.processOrder(bodyMap);
            //应答超时
            //模拟接收微信端的重复通知
//            TimeUnit.SECONDS.sleep(5);
            //成功应答
            response.setStatus(200);
            map.put("code", "SUCCESS");
            map.put("message", "成功");
            return gson.toJson(map);

        } catch (Exception e) {
            e.printStackTrace();
            //失败应答
            response.setStatus(500);
            map.put("code", "ERROR");
            map.put("message", "失败");
            return gson.toJson(map);
        }

    }

    /**
     * 用户取消订单
     * @param orderNo
     * @return
     * @throws Exception
     */
    @ApiOperation("用户取消订单")
    @PostMapping("/cancel/{orderNo}")
    public Result<?> cancel(@PathVariable String orderNo) throws Exception {
        log.info("取消订单");
        wxPayService.cancelOrder(orderNo);
        return Result.ok("订单已取消");
    }

    /**
     * 查询本地订单状态
     * @param orderNo
     * @return
     */
    @ApiOperation("查询本地订单状态")
    @GetMapping("/query-order-status/{orderNo}")
    public Result<?> queryOrderStatus(@PathVariable String orderNo) {
        Integer orderStatus = wxPayService.getOrderStatus(orderNo);
        if (orderStatus == 1) {
            return Result.ok("支付成功"); //支付成功
        }
        return Result.error(101,"支付中......");
    }

    @ApiOperation("申请退款")
    @PostMapping("/refunds/{orderNo}/{reason}")
    public Result<?> refunds(@PathVariable String orderNo, @PathVariable String reason) throws Exception {
        if (StrUtil.isBlank(orderNo)) {
            return Result.error("参数为空");
        }
        log.info("申请退款");
        wxPayService.refund(orderNo, reason);
        return Result.ok();
    }

    /**
     * 查询退款
     * @param refundNo
     * @return
     * @throws Exception
     */
    @ApiOperation("查询退款：测试用")
    @GetMapping("/query-refund/{refundNo}")
    public Result queryRefund(@PathVariable String refundNo) throws Exception {
        log.info("查询退款");
        String result = wxPayService.queryRefund(refundNo);
        return Result.ok(result);
    }


    /**
     * 退款结果通知
     * 退款状态改变后，微信会把相关退款结果发送给商户。
     */
    @ApiOperation("退款结果通知")
    @PostMapping("/refunds/notify")
    public String refundsNotify(HttpServletRequest request, HttpServletResponse response){
        log.info("退款通知执行");
        Gson gson = new Gson();
        Map<String, String> map = new HashMap<>();//应答对象

        try {
            //处理通知参数
            String body = HttpUtils.readData(request);
            Map<String, Object> bodyMap = gson.fromJson(body, HashMap.class);
            String requestId = (String)bodyMap.get("id");
            log.info("支付通知的id ===> {}", requestId);

            //签名的验证
            WechatPay2ValidatorForRequest wechatPay2ValidatorForRequest = new WechatPay2ValidatorForRequest(verifier, requestId, body);
            if (!wechatPay2ValidatorForRequest.validate(request)) {
                log.error("通知验签失败");
                //失败应答
                response.setStatus(500);
                map.put("code", "ERROR");
                map.put("message", "通知验签失败");
                return gson.toJson(map);
            }
            log.info("通知验签成功");

            //处理退款单
            wxPayService.processRefund(bodyMap);

            //成功应答
            response.setStatus(200);
            map.put("code", "SUCCESS");
            map.put("message", "成功");
            return gson.toJson(map);

        } catch (Exception e) {
            e.printStackTrace();
            //失败应答
            response.setStatus(500);
            map.put("code", "ERROR");
            map.put("message", "失败");
            return gson.toJson(map);
        }
    }
}
