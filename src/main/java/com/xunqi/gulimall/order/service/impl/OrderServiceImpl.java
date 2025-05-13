package com.xunqi.gulimall.order.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xunqi.gulimall.cart.service.CartService;
import com.xunqi.gulimall.cart.vo.CartItemVo;
import com.xunqi.gulimall.common.enmus.OrderStatus;
import com.xunqi.gulimall.order.constant.PayConstant;
import com.xunqi.gulimall.order.dao.OrderDao;
import com.xunqi.gulimall.order.entity.OrderEntity;
import com.xunqi.gulimall.order.entity.OrderItemEntity;
import com.xunqi.gulimall.order.entity.PaymentInfoEntity;
import com.xunqi.gulimall.order.enume.OrderStatusEnum;

import com.xunqi.gulimall.order.service.OrderItemService;
import com.xunqi.gulimall.order.service.OrderService;
import com.xunqi.gulimall.order.to.OrderCreateTo;
import com.xunqi.gulimall.order.vo.*;
import com.xunqi.gulimall.utils.wxpayment.OrderNoUtils;
import com.xunqi.gulimall.paymentrecord.entity.PaymentRecord;
import com.xunqi.gulimall.paymentrecord.service.PaymentRecordService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;


@Slf4j
@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {


    @Autowired
    private OrderItemService orderItemService;


    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;


    @Resource
    private OrderService orderService;


    @Resource
    private CartService cartService;

    @Resource
    private PaymentRecordService paymentRecordService;

    /**
     * 订单确认页返回需要用的数据
     * @return
     */
    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {

        //构建OrderConfirmVo
        OrderConfirmVo confirmVo = new OrderConfirmVo();

        List<CartItemVo> userCartItems = cartService.getUserCartItems();
        List<OrderItemVo> cartItemVoList = userCartItems.stream().map(item -> {
            OrderItemVo orderItemVo = new OrderItemVo();
            orderItemVo.setSkuId(item.getSkuId());
            orderItemVo.setSkuName(item.getSkuName());
            orderItemVo.setImgUrl(item.getImgUrl());
            orderItemVo.setOrderPrice(item.getSkuPrice());
            orderItemVo.setSkuNum(item.getSkuNum());
            orderItemVo.setSkuAttrValues(item.getSkuAttrValues());
            return orderItemVo;
        }).collect(Collectors.toList());
        confirmVo.setDetailArrayList(cartItemVoList);
        //TODO 5、防重令牌(防止表单重复提交)
        //为用户设置一个token，三十分钟过期时间（存在redis）
        String token = UUID.randomUUID().toString().replace("-", "");


        return confirmVo;
    }

    /**
     * 提交订单
     * @param vo
     * @return
     */
    // @Transactional(isolation = Isolation.READ_COMMITTED) 设置事务的隔离级别
    // @Transactional(propagation = Propagation.REQUIRED)   设置事务的传播级别
    @Transactional(rollbackFor = Exception.class)
    // @GlobalTransactional(rollbackFor = Exception.class)
    @Override
    public String submitOrder(OrderSubmitVo vo) {
        OrderCreateTo order = createOrder(vo);
        saveOrder(order);
        return order.getOrder().getOrderSn();
    }

    /**
     * 按照订单号获取订单信息
     * @param orderSn
     * @return
     */
    @Override
    public OrderEntity getOrderByOrderSn(String orderSn) {

        OrderEntity orderEntity = this.baseMapper.selectOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));

        return orderEntity;
    }




    /**
     * 获取当前订单的支付信息
     * @param orderSn
     * @return
     */
    @Override
    public PayVo getOrderPay(String orderSn) {

        PayVo payVo = new PayVo();
        OrderEntity orderInfo = this.getOrderByOrderSn(orderSn);

        //保留两位小数点，向上取值
        BigDecimal payAmount = orderInfo.getPayAmount().setScale(2, BigDecimal.ROUND_UP);
        payVo.setTotal_amount(payAmount.toString());
        payVo.setOut_trade_no(orderInfo.getOrderSn());

        //查询订单项的数据
        List<OrderItemEntity> orderItemInfo = orderItemService.list(
                new QueryWrapper<OrderItemEntity>().eq("order_sn", orderSn));
        OrderItemEntity orderItemEntity = orderItemInfo.get(0);
        payVo.setBody(orderItemEntity.getSkuAttrsVals());

        payVo.setSubject(orderItemEntity.getSkuName());

        return payVo;
    }



    /**
     * 保存订单所有数据
     * @param orderCreateTo
     */
    private void saveOrder(OrderCreateTo orderCreateTo) {

        //获取订单信息
        OrderEntity order = orderCreateTo.getOrder();
        order.setModifyTime(new Date());
        order.setCreateTime(new Date());
        //保存订单
        this.baseMapper.insert(order);

        //获取订单项信息
        List<OrderItemEntity> orderItems = orderCreateTo.getOrderItems();
        //批量保存订单项数据
        orderItemService.saveBatch(orderItems);
    }


    private OrderCreateTo createOrder(OrderSubmitVo vo) {
        OrderCreateTo createTo = new OrderCreateTo();
        // 1、生成订单号
        String orderSn = OrderNoUtils.getOrderNo();
        OrderEntity orderEntity = builderOrder(orderSn, vo);

        //2、获取到所有的订单项
        List<OrderItemEntity> orderItemEntities = null;
//        try {
            orderItemEntities = builderOrderItems(orderSn);
//        } catch (Exception e) {
//            log.error("生成订单项失败" + e.getMessage());
//            throw new RuntimeException("生成订单项失败");
//        }

        //3、验价(计算价格、积分等信息)
//        computePrice(orderEntity, orderItemEntities);

        createTo.setOrder(orderEntity);
        createTo.setOrderItems(orderItemEntities);

        return createTo;
    }

    /**
     * 计算价格的方法
     * @param orderEntity
     * @param orderItemEntities
     */
    private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> orderItemEntities) {

        //总价
        BigDecimal total = new BigDecimal("0.0");
        //优惠价
        BigDecimal coupon = new BigDecimal("0.0");
        BigDecimal intergration = new BigDecimal("0.0");
        BigDecimal promotion = new BigDecimal("0.0");

        //积分、成长值
        Integer integrationTotal = 0;
        Integer growthTotal = 0;

        //订单总额，叠加每一个订单项的总额信息
        for (OrderItemEntity orderItem : orderItemEntities) {
            //优惠价格信息
            coupon = coupon.add(orderItem.getCouponAmount());
            promotion = promotion.add(orderItem.getPromotionAmount());
            intergration = intergration.add(orderItem.getIntegrationAmount());

            //总价
            total = total.add(orderItem.getRealAmount());

            //积分信息和成长值信息
            integrationTotal += orderItem.getGiftIntegration();
            growthTotal += orderItem.getGiftGrowth();

        }
        //1、订单价格相关的
        orderEntity.setTotalAmount(total);
        //设置应付总额(总额+运费)
        orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount()));
        orderEntity.setCouponAmount(coupon);
        orderEntity.setPromotionAmount(promotion);
        orderEntity.setIntegrationAmount(intergration);

        //设置积分成长值信息
        orderEntity.setIntegration(integrationTotal);
        orderEntity.setGrowth(growthTotal);

        //设置删除状态(0-未删除，1-已删除)
        orderEntity.setDeleteStatus(0);

    }


    /**
     * 构建订单数据
     * @param orderSn
     * @return
     */
    private OrderEntity builderOrder(String orderSn, OrderSubmitVo vo) {
        //获取当前用户登录信息
//        MemberResponseVo memberResponseVo = LoginUserInterceptor.loginUser.get();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getPrincipal().toString();

        OrderEntity orderEntity = new OrderEntity();
//        orderEntity.setMemberId(userName);
        orderEntity.setOrderSn(orderSn);
        orderEntity.setMemberUsername(userName);

        //远程获取收货地址和运费信息

        //获取到运费信息
//        BigDecimal fare = fareResp.getFare();
//        orderEntity.setFreightAmount(fare);

        //获取到收货地址信息
//        MemberAddressVo address = fareResp.getAddress();
        //设置收货人信息
        orderEntity.setReceiverName(vo.getConsignee());
        orderEntity.setReceiverPhone(vo.getConsigneeTel());
//        orderEntity.setReceiverPostCode(address.getPostCode());
//        orderEntity.setReceiverProvince(address.getProvince());
//        orderEntity.setReceiverCity(address.getCity());
//        orderEntity.setReceiverRegion(address.getRegion());
        orderEntity.setReceiverDetailAddress(vo.getDeliveryAddress());

        //设置订单相关的状态信息
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        orderEntity.setAutoConfirmDay(7);
        orderEntity.setConfirmStatus(0);
        return orderEntity;
    }

    /**
     * 构建所有订单项数据
     * @return
     */
    public List<OrderItemEntity> builderOrderItems(String orderSn) {

        List<OrderItemEntity> orderItemEntityList = new ArrayList<>();

        //最后确定每个购物项的价格
        // CartVo cart = cartService.getCart();
        // List<CartItemVo> carts = cart.getCartInfoList();
        List<CartItemVo> carts = cartService.getUserCartItems();


        List<OrderItemVo> currentCartItems = new ArrayList<>();
        carts.forEach(p -> {
            //获取当前购物车的商品信息
            OrderItemVo orderItemVo = new OrderItemVo();
            orderItemVo.setSkuId(p.getSkuId());
            orderItemVo.setSkuName(p.getSkuName());
            orderItemVo.setImgUrl(p.getImgUrl());
            orderItemVo.setOrderPrice(p.getSkuPrice());
            orderItemVo.setSkuNum(p.getSkuNum());
            orderItemVo.setSkuAttrValues(p.getSkuAttrValues());

            currentCartItems.add(orderItemVo);
        });
        if (currentCartItems != null && currentCartItems.size() > 0) {
            orderItemEntityList = currentCartItems.stream().map((items) -> {
                //构建订单项数据
                OrderItemEntity orderItemEntity = builderOrderItem(items);
                orderItemEntity.setOrderSn(orderSn);

                return orderItemEntity;
            }).collect(Collectors.toList());
        }

        return orderItemEntityList;
    }

    /**
     * 构建某一个订单项的数据
     * @param items
     * @return
     */
    private OrderItemEntity builderOrderItem(OrderItemVo items) {

        OrderItemEntity orderItemEntity = new OrderItemEntity();

        //1、商品的spu信息
        Long skuId = items.getSkuId();



        //2、商品的sku信息
        orderItemEntity.setSkuId(skuId);


        //使用StringUtils.collectionToDelimitedString将list集合转换为String
        String skuAttrValues = StringUtils.collectionToDelimitedString(items.getSkuAttrValues(), ";");
        orderItemEntity.setSkuAttrsVals(skuAttrValues);

        //3、商品的优惠信息

        //4、商品的积分信息


        //5、订单项的价格信息
        orderItemEntity.setPromotionAmount(BigDecimal.ZERO);
        orderItemEntity.setCouponAmount(BigDecimal.ZERO);
        orderItemEntity.setIntegrationAmount(BigDecimal.ZERO);

        //当前订单项的实际金额.总额 - 各种优惠价格
        //原来的价格
        BigDecimal origin = /*orderItemEntity.getSkuPrice().multiply(new BigDecimal(orderItemEntity.getSkuQuantity().toString()));*/new BigDecimal("0.01");
        //原价减去优惠价得到最终的价格
        BigDecimal subtract = /*origin.subtract(orderItemEntity.getCouponAmount())
                .subtract(orderItemEntity.getPromotionAmount())
                .subtract(orderItemEntity.getIntegrationAmount());*/new BigDecimal("0.01");
        orderItemEntity.setRealAmount(subtract);

        return orderItemEntity;
    }


    /**
     * 处理支付宝的支付结果
     * @param asyncVo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String handlePayResult(PayAsyncVo asyncVo) {

        //保存交易流水信息
        PaymentInfoEntity paymentInfo = new PaymentInfoEntity();
        paymentInfo.setOrderSn(asyncVo.getOut_trade_no());
        paymentInfo.setAlipayTradeNo(asyncVo.getTrade_no());
        paymentInfo.setTotalAmount(new BigDecimal(asyncVo.getBuyer_pay_amount()));
        paymentInfo.setSubject(asyncVo.getBody());
        paymentInfo.setPaymentStatus(asyncVo.getTrade_status());
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setCallbackTime(asyncVo.getNotify_time());
        //添加到数据库中


        //修改订单状态
        //获取当前状态
        String tradeStatus = asyncVo.getTrade_status();

        if (tradeStatus.equals("TRADE_SUCCESS") || tradeStatus.equals("TRADE_FINISHED")) {
            //支付成功状态
            String orderSn = asyncVo.getOut_trade_no(); //获取订单号
            this.updateOrderStatus(orderSn,OrderStatusEnum.PAYED.getCode(),PayConstant.ALIPAY);
        }

        return "success";
    }

    @Override
    public PageInfo<MyOrderListVo> listMyOrder(Integer pageNum, Integer limit) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getPrincipal().toString();
        List<MyOrderListVo> voList = new ArrayList<>();
        List<OrderEntity> orderList = orderService.list(new LambdaQueryWrapper<OrderEntity>().eq(OrderEntity::getMemberUsername, userName));
        List<String> orderSnList = orderList.stream().map(OrderEntity::getOrderSn).collect(Collectors.toList());
        if (CollUtil.isEmpty(orderList) || CollUtil.isEmpty(orderSnList)) {
            return new PageInfo<>();
        }
        List<OrderItemEntity> orderItemList = orderItemService.list(new LambdaQueryWrapper<OrderItemEntity>().in(OrderItemEntity::getOrderSn, orderSnList));
//        Map<String, Integer> orderStatusMap = paymentRecordService.list(new LambdaQueryWrapper<PaymentRecord>().in(PaymentRecord::getOrderId, orderSnList)).stream().collect(Collectors.toMap(PaymentRecord::getOrderId, PaymentRecord::getPaymentStatus));
        for (OrderEntity orderEntity : orderList) {
            MyOrderListVo myOrderListVo = new MyOrderListVo();
            myOrderListVo.setId(String.valueOf(orderEntity.getId()));
            myOrderListVo.setCreateTime(orderEntity.getCreateTime());
            myOrderListVo.setOutTradeNo(orderEntity.getOrderSn());
            myOrderListVo.setConsignee(orderEntity.getReceiverName());
            myOrderListVo.setTotalAmount(orderEntity.getTotalAmount());
//            Integer statusInt = orderStatusMap.get(orderEntity.getOrderSn());
            Integer statusInt = orderEntity.getStatus();
            if (statusInt == null) {
                myOrderListVo.setOrderStatusName("未知");
                continue;
            }
            switch (statusInt) {
                case 0:
                    myOrderListVo.setOrderStatusName("未支付");
                    break;
                case 1:
                    myOrderListVo.setOrderStatusName("已支付");
                    break;
                case 4:
                    myOrderListVo.setOrderStatusName("用户已取消");
                    break;
                case 6:
                    myOrderListVo.setOrderStatusName("退款中");
                    break;
                case 7:
                    myOrderListVo.setOrderStatusName("已退款");
                    break;
                default:
                    myOrderListVo.setOrderStatusName("未知");
            }
            myOrderListVo.setOrderDetailList(orderItemList.stream().filter(p -> p.getOrderSn().equals(orderEntity.getOrderSn())).map(p -> {
                MyOrderDetailListVo myOrderDetailListVo = new MyOrderDetailListVo();
                myOrderDetailListVo.setId(String.valueOf(p.getId()));
                myOrderDetailListVo.setImgUrl(p.getSkuPic());
                myOrderDetailListVo.setSkuName(p.getSkuName());
                myOrderDetailListVo.setSkuNum(p.getSkuQuantity());
                return myOrderDetailListVo;
            }).collect(Collectors.toList()));
            voList.add(myOrderListVo);
        }

        PageHelper.startPage(pageNum, limit);
        PageInfo<MyOrderListVo> page = new PageInfo<>(voList);
        page.setPageSize(page.getPageSize());
        page.setPageNum(page.getPageNum());
        page.setPages(page.getPages());
        page.setTotal(page.getTotal());
        return page;
    }

    @Override
    public OrderEntity getOrderByOrderNo(String orderNo) {
        return null;
    }

    @Override
    public String getOrderStatus(String orderNo) {
        OrderEntity one = this.getOne(new LambdaQueryWrapper<OrderEntity>().eq(OrderEntity::getOrderSn, orderNo).last("limit 1"));
        if (one != null) {
            Integer status = one.getStatus();
            switch (status) {
                case 0:
                    return "未支付";
                case 1:
                    return "已支付";
                case 2:
                    return "已发货";
                case 3:
                    return "已完成";
                case 4:
                    return "已关闭";
                case 5:
                    return "无效订单";
                default:
                    return "未知";
            }
        }
        return "";
    }

    @Override
    public void updateStatusByOrderNo(String orderNo, OrderStatus orderStatus) {
        log.info("更新订单状态 ===> {}", orderStatus.getType());

        QueryWrapper<OrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_sn", orderNo);

        OrderEntity orderInfo = new OrderEntity();
        switch (orderStatus.getType()) {
            //订单状态【0->未支付；1->已支付；2->已发货；3->已完成；4->用户已取消；5->无效订单;6->退款中;7->已退款】
            case "未支付":
                orderInfo.setStatus(0);
                break;
            case "已支付":
                orderInfo.setStatus(1);
                break;
            case "用户已取消":
                orderInfo.setStatus(4);
                break;
            case "退款中":
                orderInfo.setStatus(6);
                break;
            case "已退款":
                orderInfo.setStatus(7);
                break;
//            case 5:
//                orderInfo.setStatus(OrderStatusEnum.INVALID.getCode());
//                break;
        }
        baseMapper.update(orderInfo, queryWrapper);
    }


    /**
     * 修改订单状态
     * @param orderSn
     * @param code
     */
    private void updateOrderStatus(String orderSn, Integer code,Integer payType) {

        this.baseMapper.updateOrderStatus(orderSn,code,payType);
    }






    public static void main(String[] args) {
        String orderSn = IdWorker.getTimeId().substring(0,16);
        System.out.println(orderSn);
    }

}