package com.xunqi.gulimall.user.controller;


import cn.hutool.core.util.StrUtil;
import com.xunqi.gulimall.member.entity.MemberEntity;
import com.xunqi.gulimall.user.entity.User;
import com.xunqi.gulimall.user.service.UserService;
import com.xunqi.gulimall.user.vo.FindUserAddressListVo;
import com.xunqi.gulimall.utils.Result;
import com.xunqi.gulimall.utils.product.JwtUtils;
import com.xunqi.gulimall.utils.product.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 系统用户 前端控制器
 * </p>
 *
 * @author renjianbang
 * @since 2025-04-29
 */
@Slf4j
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

    @PostMapping("/login")
    public Result<?> vueadminelementuiLogin(@RequestBody User user) {
        User login = userService.login(user);
        if (login != null) {
            //登录成功
            //生成token字符串，使用jwt工具类
            String jwtToken = JwtUtils.getJwtToken(login.getUserId().toString(), login.getUsername());
            Map<String, String> map = new HashMap<>();
            map.put("token", jwtToken);
            return Result.ok(map);
        } else {
            return Result.error("登录失败");
        }
    }

    @GetMapping("/info")
    public Result<?> info(HttpServletRequest request) {
        String token = request.getHeader("token");

        // 1. 校验token有效性
        if (StrUtil.isBlank(token)) {
            return Result.error(500, "Token不能为空");
        }

        try {
            // 2. 解析token获取用户名
            String userId = JwtUtils.getMemberIdByJwtToken(request);
            User userInfo = userService.getById(userId);
            // 3. 查询真实用户信息（示例需要注入MemberService）
//            MemberEntity memberEntity = memberService.getOne(new LambdaQueryWrapper<MemberEntity>().eq(MemberEntity::getUsername, username));

            if (userInfo == null) {
                return Result.error(500, "用户不存在");
            }
            List<String> routes = new ArrayList<>();
            routes.add("Dashboard");
            routes.add("Product");
            routes.add("Acl");
            routes.add("User");

            routes.add("Attr");
//            routes.add("Acl");
            userInfo.setRoutes(routes);
            // 4. 敏感信息过滤（可选）
//            memberEntity.setName("33333333333");
            return Result.ok(userInfo);
        } catch (Exception e) {
            log.error("用户信息获取失败：", e);
            return Result.error(500, "Token无效或已过期");
        }
    }
}
