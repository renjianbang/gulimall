//package com.xunqi.gulimall.config.product;
//
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
///**
// * @Description
// * @Author cisz
// * @CreateTime 2025-04-29 16:35
// */
//public class MyAuthenticationSuccessLogoutHandler implements LogoutSuccessHandler {
//    @Override
//    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
//
//        // 也可以返回一个 JSON 响应
//         httpServletResponse.setContentType("application/json;charset=UTF-8");
//         httpServletResponse.getWriter().write("{\"message\":\"Logout successful\"}");
//    }
//}
