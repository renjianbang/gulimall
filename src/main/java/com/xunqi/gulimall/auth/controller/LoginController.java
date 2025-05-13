package com.xunqi.gulimall.auth.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xunqi.gulimall.auth.vo.UserRegisterVo;
import com.xunqi.gulimall.common.constant.AuthServerConstant;
import com.xunqi.gulimall.exception.BizCodeEnum;
import com.xunqi.gulimall.member.entity.MemberEntity;
import com.xunqi.gulimall.member.service.MemberService;
import com.xunqi.gulimall.member.vo.MemberUserLoginVo;
import com.xunqi.gulimall.serurity.security.TokenManager;
import com.xunqi.gulimall.user.service.UserService;
import com.xunqi.gulimall.utils.product.JwtUtils;
import com.xunqi.gulimall.utils.product.R;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.xunqi.gulimall.common.constant.AuthServerConstant.LOGIN_USER;


/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: 夏沫止水
 * @createTime: 2020-06-24 10:37
 **/
@CrossOrigin
@Slf4j
@RequestMapping(value = "/api/user/passport")
@RestController
public class LoginController {

    // 在Controller中添加
    @Autowired
    private TokenManager tokenManager;

    @Resource
    private MemberService memberService;

    @Resource
    private UserService userService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping(value = "/sendCode/{phone}")
    public R sendCode(@PathVariable String phone) {

        //1、接口防刷
        String redisCode = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if (StrUtil.isNotBlank(redisCode)) {
            //活动存入redis的时间，用当前时间减去存入redis的时间，判断用户手机号是否在60s内发送验证码
            long currentTime = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() - currentTime < 60000) {
                //60s内不能再发
                return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(), BizCodeEnum.SMS_CODE_EXCEPTION.getMessage());
            }
        }

        //2、验证码的再次效验 redis.存key-phone,value-code
        int code = (int) ((Math.random() * 9 + 1) * 100000);
        String codeNum = String.valueOf(code);
        String redisStorage = codeNum + "_" + System.currentTimeMillis();

        //存入redis，防止同一个手机号在60秒内再次发送验证码
        stringRedisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX+phone,
                redisStorage,10, TimeUnit.MINUTES);

//        thirdPartFeignService.sendCode(phone, codeNum);
        log.info("控制台打印验证码：" + codeNum);

        return R.ok();
    }

    /**
     *
     * TODO: 重定向携带数据：利用session原理，将数据放在session中。
     * TODO:只要跳转到下一个页面取出这个数据以后，session里面的数据就会删掉
     * TODO：分布下session问题
     * RedirectAttributes：重定向也可以保留数据，不会丢失
     * 用户注册
     * @return
     */
    @PostMapping(value = "/register")
    public R register(/*@Valid */@RequestBody UserRegisterVo vos/*, BindingResult result,
                           RedirectAttributes attributes*/) {

        //如果有错误回到注册页面
//        if (result.hasErrors()) {
//            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
//            attributes.addFlashAttribute("errors",errors);
//
//            //效验出错回到注册页面
//            return "redirect:http://auth.gulimall.com/reg.html";
//        }

        //1、效验验证码
        String code = vos.getCode();

        //获取存入Redis里的验证码
        String redisCode = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vos.getPhone());
        if (StrUtil.isNotBlank(redisCode)) {
            //截取字符串
            if (code.equals(redisCode.split("_")[0])) {
                //删除验证码;令牌机制
                stringRedisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vos.getPhone());
                //验证码通过，真正注册，调用远程服务进行注册
                memberService.register(vos);
                userService.saveUser(vos);
//                if (register.getCode() == 0) {
//                    //成功
//                    return "redirect:http://auth.gulimall.com/login.html";
//                } else {
//                    //失败
//                    Map<String, String> errors = new HashMap<>();
//                    errors.put("msg", register.getData("msg",new TypeReference<String>(){}));
//                    attributes.addFlashAttribute("errors",errors);
//                    return "redirect:http://auth.gulimall.com/reg.html";
//                }
                return R.ok("注册成功");
            } else {
                //效验出错回到注册页面
//                Map<String, String> errors = new HashMap<>();
//                errors.put("code","验证码错误");
//                attributes.addFlashAttribute("errors",errors);
//                return "redirect:http://auth.gulimall.com/reg.html";
                return R.error("验证码错误");
            }
        } else {
            //效验出错回到注册页面
//            Map<String, String> errors = new HashMap<>();
//            errors.put("code","验证码错误");
//            attributes.addFlashAttribute("errors",errors);
//            return "redirect:http://auth.gulimall.com/reg.html";
            return R.error("验证码过期，请重新获取");
        }
    }

    @PostMapping(value = "/login")
    public R login(@RequestBody MemberUserLoginVo vo, RedirectAttributes attributes, HttpSession session) {
        //远程登录
        MemberEntity login = memberService.login(vo);

        if (login != null) {
//            MemberResponseVo data = login.getData("data", new TypeReference<MemberResponseVo>() {});
//            session.setAttribute(LOGIN_USER, login);
            //登录成功
            //生成token字符串，使用jwt工具类
            String jwtToken = JwtUtils.getJwtToken(login.getId().toString(), login.getUsername());
//            return "redirect:http://gulimall.com";
            Map<String, String> map = new HashMap<>();
            map.put("token", jwtToken);
            return R.ok("登录成功").put("data", map);
        } else {
//            Map<String,String> errors = new HashMap<>();
//            errors.put("msg", login.getData("msg",new TypeReference<String>(){}));
//            attributes.addFlashAttribute("errors",errors);
//            return "redirect:http://auth.gulimall.com/login.html";
            return R.error("登录失败");
        }
    }

    @GetMapping(value = "/auth/getUserInfo")
    public R getUserInfo(HttpServletRequest request) {
        String token = request.getHeader("token");

        // 1. 校验token有效性
        if (StrUtil.isBlank(token)) {
            return R.error(500, "Token不能为空");
        }

        try {
            // 2. 解析token获取用户名
            String memberId = JwtUtils.getMemberIdByJwtToken(request);
            MemberEntity memberEntity = memberService.getById(memberId);
            // 3. 查询真实用户信息（示例需要注入MemberService）
//            MemberEntity memberEntity = memberService.getOne(new LambdaQueryWrapper<MemberEntity>().eq(MemberEntity::getUsername, username));

            if (memberEntity == null) {
                return R.error(500, "用户不存在");
            }

            // 4. 敏感信息过滤（可选）
            memberEntity.setName("33333333333");
            return R.ok().put("data", memberEntity);
        } catch (Exception e) {
            log.error("用户信息获取失败：", e);
            return R.error(500, "Token无效或已过期");
        }
    }

    @GetMapping(value = "/logout")
    public R logout(HttpServletRequest request) {
        return R.ok();
    }

}
