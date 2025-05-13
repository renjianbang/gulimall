//package com.xunqi.gulimall.config.product;
//
//import com.alibaba.fastjson.JSON;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.HashMap;
//
///**
// * @Description
// * @Author cisz
// * @CreateTime 2025-04-29 13:18
// */
//public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//
//        //获取用户身份信息
//        Object principal = authentication.getPrincipal();
//
//        //创建结果对象
//        HashMap result = new HashMap();
//        result.put("code", 0);
//        result.put("message", "登录成功");
//        result.put("data", principal);
//
//        //转换成json字符串
//        String json = JSON.toJSONString(result);
//
//        //返回响应
//        response.setContentType("application/json;charset=UTF-8");
//        response.getWriter().println(json);
//    }
//}