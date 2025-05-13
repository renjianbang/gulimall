package com.xunqi.gulimall.cart.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
//import com.xunqi.common.utils.R;
//import com.xunqi.gulimall.cart.exception.CartExceptionHandler;
//import com.xunqi.gulimall.cart.feign.ProductFeignService;
//import com.xunqi.gulimall.cart.interceptor.CartInterceptor;
import com.xunqi.gulimall.cart.service.CartService;
//import com.xunqi.gulimall.cart.to.UserInfoTo;
import com.xunqi.gulimall.cart.vo.CartItemVo;
import com.xunqi.gulimall.cart.vo.CartVo;
import com.xunqi.gulimall.cart.vo.SkuInfoVo;
import com.xunqi.gulimall.product.entity.SkuInfoEntity;
import com.xunqi.gulimall.product.service.SkuInfoService;
import com.xunqi.gulimall.product.service.SkuSaleAttrValueService;
import com.xunqi.gulimall.utils.product.R;
import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.BoundHashOperations;
//import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.xunqi.gulimall.common.constant.CartConstant.CART_PREFIX;

//import static com.xunqi.common.constant.CartConstant.CART_PREFIX;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: 夏沫止水
 * @createTime: 2020-06-30 17:06
 **/

@Slf4j
@Service("cartService")
public class CartServiceImpl implements CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;
//
//    @Autowired
//    private ProductFeignService productFeignService;

    @Autowired
    private ThreadPoolExecutor executor;

    @Resource
    private SkuInfoService skuInfoService;

    @Resource
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Override
    public CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {

        //拿到要操作的购物车信息
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();

        //判断Redis是否有该商品的信息
        String productRedisValue = (String) cartOps.get(skuId.toString());
        //如果没有就添加数据
        if (StringUtils.isEmpty(productRedisValue)) {

            //2、添加新的商品到购物车(redis)
            CartItemVo cartItemVo = new CartItemVo();
            //开启第一个异步任务
            CompletableFuture<Void> getSkuInfoFuture = CompletableFuture.runAsync(() -> {
                //1、远程查询当前要添加商品的信息
//                R productSkuInfo = productFeignService.getInfo(skuId);
//                SkuInfoVo skuInfo = productSkuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {});
                SkuInfoEntity skuInfo = skuInfoService.getById(skuId);

                //数据赋值操作
                cartItemVo.setSkuId(skuInfo.getSkuId());
                cartItemVo.setSkuName(skuInfo.getSkuTitle());
                cartItemVo.setImgUrl(skuInfo.getSkuDefaultImg());
                cartItemVo.setSkuPrice(skuInfo.getPrice());
                cartItemVo.setSkuNum(num);
            }, executor);

            //开启第二个异步任务
            CompletableFuture<Void> getSkuAttrValuesFuture = CompletableFuture.runAsync(() -> {
                //2、远程查询skuAttrValues组合信息
//                List<String> skuSaleAttrValues = productFeignService.getSkuSaleAttrValues(skuId);
                List<String> skuSaleAttrValues = skuSaleAttrValueService.getSkuSaleAttrValuesAsStringList(skuId);
                cartItemVo.setSkuAttrValues(skuSaleAttrValues);
            }, executor);

            //等待所有的异步任务全部完成
            CompletableFuture.allOf(getSkuInfoFuture, getSkuAttrValuesFuture).get();

            String cartItemJson = JSON.toJSONString(cartItemVo);
            cartOps.put(skuId.toString(), cartItemJson);

            return cartItemVo;
        } else {
            //购物车有此商品，修改数量即可
            CartItemVo cartItemVo = JSON.parseObject(productRedisValue, CartItemVo.class);
            cartItemVo.setSkuNum(cartItemVo.getSkuNum() + num);
            //修改redis的数据
            String cartItemJson = JSON.toJSONString(cartItemVo);
            cartOps.put(skuId.toString(),cartItemJson);

            return cartItemVo;
        }
//        return null;
    }

    @Override
    public CartItemVo getCartItem(Long skuId) {
        //拿到要操作的购物车信息
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();

        String redisValue = (String) cartOps.get(skuId.toString());

        CartItemVo cartItemVo = JSON.parseObject(redisValue, CartItemVo.class);

        return cartItemVo;
    }

    /**
     * 获取用户登录或者未登录购物车里所有的数据
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Override
    public CartVo getCart() throws ExecutionException, InterruptedException {
        CartVo cartVo = new CartVo();
        //先得到当前用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String cartKey = "";
        if (authentication != null && authentication.isAuthenticated()) {
            //gulimall:cart:1
            String userId = authentication.getPrincipal().toString();
            cartKey = CART_PREFIX + userId;
        }

        //3、获取登录后的购物车数据【包含合并过来的临时购物车的数据和登录后购物车的数据】
        List<CartItemVo> cartItems = getCartItems(cartKey);
        cartItems.forEach(p -> {
            p.setId(IdUtil.randomUUID());
        });
        cartVo.setCartInfoList(cartItems);
        return cartVo;
    }

    /**
     * 获取到我们要操作的购物车
     * @return
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        //先得到当前用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String cartKey = "";
        if (authentication != null && authentication.isAuthenticated()) {
            //gulimall:cart:1
            Object principal = authentication.getPrincipal();
//            String userId = authentication.getPrincipal().toString();
            String userId = authentication.getPrincipal().toString(); // 直接获取用户名
            System.out.println("当前登录用户的ID为：" + userId);
//            String userId = null;
            cartKey = CART_PREFIX + userId;
        }

        if (StrUtil.isBlank(cartKey)) {
            //没有登录，使用临时购物车
            throw new RuntimeException("当前用户未登录，请登录！");
        }

        //绑定指定的key操作Redis
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);

        return operations;
    }


    /**
     * 获取购物车里面的数据
     * @param cartKey
     * @return
     */
    private List<CartItemVo> getCartItems(String cartKey) {
        //获取购物车里面的所有商品
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        List<Object> values = operations.values();
        if (values != null && values.size() > 0) {
            List<CartItemVo> cartItemVoStream = values.stream().map((obj) -> {
                String str = (String) obj;
                CartItemVo cartItem = JSON.parseObject(str, CartItemVo.class);
                return cartItem;
            }).collect(Collectors.toList());
            return cartItemVoStream;
        }
        return null;

    }


    @Override
    public void clearCartInfo(String cartKey) {
        redisTemplate.delete(cartKey);
    }

    @Override
    public void checkItem(Long skuId, Integer check) {

        //查询购物车里面的商品
        CartItemVo cartItem = getCartItem(skuId);
        //修改商品状态
        cartItem.setChecked(check == 1?true:false);

        //序列化存入redis中
        String redisValue = JSON.toJSONString(cartItem);

        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(skuId.toString(),redisValue);

    }

    /**
     * 修改购物项数量
     * @param skuId
     * @param num
     */
    @Override
    public void changeItemCount(Long skuId, Integer num) {

        //查询购物车里面的商品
        CartItemVo cartItem = getCartItem(skuId);
        cartItem.setSkuNum(num);

        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        //序列化存入redis中
        String redisValue = JSON.toJSONString(cartItem);
        cartOps.put(skuId.toString(),redisValue);
    }


    /**
     * 删除购物项
     * @param skuId
     */
    @Override
    public void deleteIdCartInfo(Integer skuId) {

        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }

    @Override
    public List<CartItemVo> getUserCartItems() {

        List<CartItemVo> cartItemVoList = new ArrayList<>();
        //先得到当前用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String cartKey = "";
        if (authentication != null && authentication.isAuthenticated()) {
            //gulimall:cart:1
            String userId = authentication.getPrincipal().toString();
            System.out.println("当前登录用户的ID为：" + userId);
//            String userId = null;
            cartKey = CART_PREFIX + userId;
        }

            //获取所有的
            List<CartItemVo> cartItems = getCartItems(cartKey);
            if (cartItems == null) {
                throw new RuntimeException("购物车数据为空");
            }
            //筛选出选中的
            cartItemVoList = cartItems.stream().filter(CartItemVo::getChecked)
//                    .map(item -> {
//                        //更新为最新的价格（查询数据库）
//                        BigDecimal price = productFeignService.getPrice(item.getSkuId());
//                        item.setPrice(price);
//                        return item;
//                    })
                    .collect(Collectors.toList());


        return cartItemVoList;
    }
}
