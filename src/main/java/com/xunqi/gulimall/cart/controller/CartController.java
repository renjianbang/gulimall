package com.xunqi.gulimall.cart.controller;

import cn.hutool.core.lang.hash.Hash;
import com.xunqi.gulimall.cart.vo.CartItemVo;
import com.xunqi.gulimall.cart.vo.CartVo;

import com.xunqi.gulimall.cart.service.CartService;
import com.xunqi.gulimall.utils.product.R;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: 夏沫止水
 * @createTime: 2020-06-30 17:12
 **/

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Resource
    private CartService cartService;

    /**
     * 获取当前用户的购物车商品项
     * @return
     */
    @GetMapping(value = "/currentUserCartItems")
    @ResponseBody
    public List<CartItemVo> getCurrentCartItems() {

        List<CartItemVo> cartItemVoList = cartService.getUserCartItems();

        return cartItemVoList;
    }

    /**
     * 去购物车页面的请求
     * 浏览器有一个cookie:user-key 标识用户的身份，一个月过期
     * 如果第一次使用jd的购物车功能，都会给一个临时的用户身份:
     * 浏览器以后保存，每次访问都会带上这个cookie；
     *
     * 登录：session有
     * 没登录：按照cookie里面带来user-key来做
     * 第一次，如果没有临时用户，自动创建一个临时用户
     *
     * @return
     */
    @GetMapping(value = "/cart.html")
    public String cartListPage(Model model) throws ExecutionException, InterruptedException {
        //快速得到用户信息：id,user-key
        // UserInfoTo userInfoTo = CartInterceptor.toThreadLocal.get();
//
//        List<CartItemVo> res = cartService.getCart();
//        model.addAttribute("cart",cartVo);
        return "cartList";
    }

//    {
//        "code": 200,
//            "message": "成功",
//            "data": [
//        {
//            "id": 61,
//                "userId": "2",
//                "skuId": 4,
//                "cartPrice": 5999,
//                "skuNum": 4,
//                "imgUrl": "http://192.168.200.128:8080xxx.jpg",
//                "skuName": "Apple iPhone 11 (A2223) ",
//                "isChecked": 1,
//                "skuPrice": 5999
//        },
//        {
//            "id": 62,
//                "userId": "2",
//                "skuId": 2,
//                "cartPrice": 5499,
//                "skuNum": 1,
//                "imgUrl": "http://192.168.200.128:8080/yyyy.jpg",
//                "skuName": "Apple iPhone 11 (A2223) 64GB 红色",
//                "isChecked": 0,
//                "skuPrice": 5499
//        }
//    ],
//        "ok": true
//    }
    @GetMapping(value = "/cartList")
    public R cartListPage() throws ExecutionException, InterruptedException {
        //快速得到用户信息：id,user-key
        // UserInfoTo userInfoTo = CartInterceptor.toThreadLocal.get();

        CartVo cartVo = cartService.getCart();
        return R.ok().put("data", cartVo);
    }



    /**
     * 添加商品到购物车
     * attributes.addFlashAttribute():将数据放在session中，可以在页面中取出，但是只能取一次
     * attributes.addAttribute():将数据放在url后面
     * @return
     */
    @PostMapping(value = "/addCartItem")
    public String addCartItem(@RequestParam("skuId") Long skuId,
                              @RequestParam("num") Integer num,
                              RedirectAttributes attributes) throws ExecutionException, InterruptedException {

        cartService.addToCart(skuId, num);

        attributes.addAttribute("skuId", skuId);
        return "redirect:http://cart.gulimall.com/addToCartSuccessPage.html";
    }

    /**
     * 加入购物车功能
     */
    @PostMapping("/addToCart/{skuId}/{skuNum}")
    public R addToCart(@PathVariable("skuId") Long skuId, @PathVariable("skuNum") Integer skuNum) throws ExecutionException, InterruptedException {
        CartItemVo cartItemVo = cartService.addToCart(skuId, skuNum);
        return R.ok().put("data", cartItemVo);
    }

    /**
     * 跳转到添加购物车成功页面
     * @param skuId
     * @param model
     * @return
     */
    @GetMapping(value = "/addToCartSuccessPage.html")
    public String addToCartSuccessPage(@RequestParam("skuId") Long skuId,
                                       Model model) {
        //重定向到成功页面。再次查询购物车数据即可
        CartItemVo cartItemVo = cartService.getCartItem(skuId);
        model.addAttribute("cartItem",cartItemVo);
        return "success";
    }


    /**
     * 商品是否选中
     * @param skuId
     * @param checked
     * @return
     */
    @GetMapping(value = "/checkCart/{skuId}/{checked}")
    public R checkItem(@PathVariable("skuId") Long skuId,
                            @PathVariable("checked") Integer checked) {

        cartService.checkItem(skuId, checked);

        return R.ok();

    }


    /**
     * 改变商品数量
     * @param skuId
     * @param num
     * @return
     */
    @GetMapping(value = "/countItem")
    public String countItem(@RequestParam(value = "skuId") Long skuId,
                            @RequestParam(value = "num") Integer num) {

        cartService.changeItemCount(skuId,num);

        return "redirect:http://cart.gulimall.com/cart.html";
    }


    /**
     * 删除商品信息
     * @param skuId
     * @return
     */
    @GetMapping(value = "/deleteItem")
    public String deleteItem(@RequestParam("skuId") Integer skuId) {

        cartService.deleteIdCartInfo(skuId);

        return "redirect:http://cart.gulimall.com/cart.html";

    }

}
