package com.xunqi.gulimall.order.web;


import com.github.pagehelper.PageInfo;
import com.xunqi.gulimall.order.service.OrderService;
import com.xunqi.gulimall.order.vo.MyOrderListVo;
import com.xunqi.gulimall.order.vo.OrderConfirmVo;
import com.xunqi.gulimall.order.vo.OrderSubmitVo;
import com.xunqi.gulimall.utils.product.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: 夏沫止水
 * @createTime: 2020-07-02 18:35
 **/
@RequestMapping(value = "/api/order")
@RestController
public class OrderWebController {

    @Autowired
    private OrderService orderService;

    /**
     * 订单页回显数据
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping(value = "/auth/trade")
    public R trade() throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = orderService.confirmOrder();
        return R.ok().put("data", confirmVo);
    }

    /**
     * 下单功能
     * @param vo
     * @return
     */
    @PostMapping(value = "/auth/submitOrder")
    public R submitOrder(@RequestParam("tradeNo") String tradeNo,
                              @RequestBody OrderSubmitVo vo) {
        String orderId = orderService.submitOrder(vo);
        return R.ok().put("data", orderId);
    }

    /**
     * http://localhost:8080/api/order/auth/1/3
     * export const reqMyOrderList = (page,limit)=>requests({url:`/order/auth/${page}/${limit}`,method:'get'});
     *
     * @return
     */
    @ApiOperation("订单列表")
    @GetMapping("/auth/{page}/{limit}")
    public R list(@PathVariable("page") Integer page,
                  @PathVariable("limit") Integer limit) {
        PageInfo<MyOrderListVo> resPage = orderService.listMyOrder(page, limit);
        return R.ok().put("page", resPage);
    }

}
