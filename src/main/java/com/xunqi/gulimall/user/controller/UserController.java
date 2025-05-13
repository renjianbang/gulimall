package com.xunqi.gulimall.user.controller;


import com.xunqi.gulimall.user.entity.User;
import com.xunqi.gulimall.user.service.UserService;
import com.xunqi.gulimall.user.vo.FindUserAddressListVo;
import com.xunqi.gulimall.utils.product.R;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 系统用户 前端控制器
 * </p>
 *
 * @author renjianbang
 * @since 2025-04-29
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    public UserService userService;

    @GetMapping("/list")
    public List<User> getList(){
        return userService.list();
    }

    @PostMapping("/add")
    public void add(@RequestBody User user){
        userService.saveUserDetails(user);
    }

    @GetMapping("/userAddress/auth/findUserAddressList")
    public R findUserAddressList(){
        List<FindUserAddressListVo> vo = userService.findUserAddressList();
        return R.ok().put("data", vo);
    }
}
