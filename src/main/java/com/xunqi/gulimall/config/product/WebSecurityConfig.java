//package com.xunqi.gulimall.config.product;
//
//import com.alibaba.fastjson.JSON;
//import com.xunqi.gulimall.utils.product.R;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.provisioning.InMemoryUserDetailsManager;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//
//import static org.springframework.security.config.Customizer.withDefaults;
//
//
//@Configuration
//@EnableWebSecurity//Spring项目总需要添加此注解，SpringBoot项目中不需要
//public class WebSecurityConfig {
//
//    @Autowired
//    private UserDetailsService userDetailsService;
//
////    @Bean
////    public UserDetailsService userDetailsService() {
////        DBUserDetailsManager manager = new DBUserDetailsManager();
////        return manager;
////    }
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        //authorizeRequests()：开启授权保护
//        //anyRequest()：对所有请求开启授权保护
//        //authenticated()：已认证请求会自动被授权
//        http
//                .cors(withDefaults())
//                .authorizeRequests()
//                .antMatchers("/", "/api/user/passport/login").permitAll() // 放行根路径和登录页
//                .antMatchers("/api/product/**").permitAll() // 商品公开访问
//                .antMatchers("/api/list/**").permitAll() // 商品公开访问
//                .antMatchers("/api/user/passport/**").permitAll()
//                .antMatchers("/api/cart/**").authenticated() // 购物车需认证
//                .anyRequest().authenticated()
//                .and()
////                .formLogin(form -> form
////                        .loginProcessingUrl("/api/user/passport/login") // 匹配现有接口
////                        .successHandler(new MyAuthenticationSuccessHandler())
////                        .failureHandler(new MyAuthenticationFailureHandler())
////                )
////                .logout(logout -> logout
////                        .logoutUrl("/api/user/passport/logout")
////                        .logoutSuccessHandler(new MyAuthenticationSuccessLogoutHandler())
////                )
//                .userDetailsService(userDetailsService)
//                .httpBasic(withDefaults());//基本授权方式
////关闭csrf攻击防御
//        http.csrf((csrf) -> {
//            csrf.disable();
//        });
//
//        http.logout(logout -> {
//            logout.logoutSuccessHandler(new MyLogoutSuccessHandler()); //注销成功时的处理
//        });
//
//        //错误处理
//        http.exceptionHandling(exception  -> {
//            exception.authenticationEntryPoint(new MyAuthenticationEntryPoint());//请求未认证的接口
//        });
//        return http.build();
//    }
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
////
////
/////**
//// * @Description
//// * @Author cisz
//// * @CreateTime 2025-04-28 10:42
//// */
////@Configuration
////@EnableWebSecurity
////@EnableGlobalMethodSecurity(prePostEnabled = true) // 启用方法级安全控制
////public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
////
////    @Autowired
////    private UserDetailsService userDetailsService;
////
////    @Override
////    protected void configure(HttpSecurity http) throws Exception {
////        http
////                .authorizeRequests()
////                .antMatchers("/", "/login").permitAll() // 放行根路径和登录页
////                .antMatchers("/api/product/**").permitAll() // 商品详情公开访问
////                .antMatchers("/api/cart/**").authenticated() // 购物车需认证
////                .anyRequest().authenticated()
////                .and()
////                .formLogin()
////                .loginPage("/login") // 自定义登录页路径
////                .loginProcessingUrl("/auth/login") // 登录处理端点
////                .successHandler(loginSuccessHandler()) // 登录成功处理
////                .permitAll()
////                .and()
////                .logout()
////                .logoutUrl("/auth/logout")
////                .deleteCookies("JSESSIONID")
////                .and()
////                .rememberMe() // 记住我功能
////                .tokenValiditySeconds(3600*24*7)
////                .and()
////                .csrf().disable(); // 根据需求决定是否禁用
////    }
////
////    @Bean
////    public AuthenticationSuccessHandler loginSuccessHandler() {
////        return (request, response, authentication) -> {
////            response.setContentType("application/json;charset=UTF-8");
////            response.getWriter().write(JSON.toJSONString(R.ok("登录成功")));
////        };
////    }
////
////    @Override
////    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
////        auth.userDetailsService(userDetailsService)
////                .passwordEncoder(passwordEncoder());
////    }
////
////    @Bean
////    public PasswordEncoder passwordEncoder() {
////        return new BCryptPasswordEncoder();
////    }
////}
