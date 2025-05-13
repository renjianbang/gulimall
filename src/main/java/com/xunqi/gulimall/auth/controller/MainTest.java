package com.xunqi.gulimall.auth.controller;

import com.xunqi.gulimall.auth.vo.UserRegisterVo;
import com.xunqi.gulimall.utils.product.R;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.stream.Collectors;

/**
 * @Description
 * @Author cisz
 * @CreateTime 2025-04-29 09:46
 */
@Controller
public class MainTest {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
