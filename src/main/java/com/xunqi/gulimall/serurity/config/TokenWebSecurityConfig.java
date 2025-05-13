package com.xunqi.gulimall.serurity.config;

//import com.xunqi.gulimall.serurity.filter.TokenAuthenticationFilter;
import com.xunqi.gulimall.serurity.filter.TokenAuthenticationFilter;
import com.xunqi.gulimall.serurity.filter.TokenLoginFilter;
import com.xunqi.gulimall.serurity.security.DefaultPasswordEncoder;
import com.xunqi.gulimall.serurity.security.TokenLogoutHandler;
import com.xunqi.gulimall.serurity.security.TokenManager;
//import com.xunqi.gulimall.serurity.security.UnauthorizedEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * <p>
 * Security配置类
 * </p>
 *
 * @author qy
 * @since 2019-11-18
 */
@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class TokenWebSecurityConfig extends WebSecurityConfigurerAdapter {

    private UserDetailsService userDetailsService;
    private TokenManager tokenManager;
    private DefaultPasswordEncoder defaultPasswordEncoder;
    private RedisTemplate redisTemplate;

    @Autowired
    public TokenWebSecurityConfig(UserDetailsService userDetailsService, DefaultPasswordEncoder defaultPasswordEncoder,
                                  TokenManager tokenManager, RedisTemplate redisTemplate) {
        this.userDetailsService = userDetailsService;
        this.defaultPasswordEncoder = defaultPasswordEncoder;
        this.tokenManager = tokenManager;
        this.redisTemplate = redisTemplate;
    }

//    /**
//     * 配置设置
//     * @param http
//     * @throws Exception
//     */
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.exceptionHandling()
////                .authenticationEntryPoint(new UnauthorizedEntryPoint())
//                .and().csrf().disable()
//                .authorizeRequests()
//                // permitAll定义匿名可访问接口
//                .antMatchers("/api/user/passport/login").permitAll()
//                .anyRequest().authenticated()
//                .and().logout().logoutUrl("/api/user/passport/logout")
//                .addLogoutHandler(new TokenLogoutHandler(tokenManager,redisTemplate)).and()
//                .addFilter(new TokenLoginFilter(authenticationManager(), tokenManager, redisTemplate))
//
//                .addFilter(new TokenAuthenticationFilter(authenticationManager(), tokenManager, redisTemplate)).httpBasic();
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/user/passport/login").permitAll()
                .anyRequest().authenticated()
                .and()
                .logout()
                .logoutUrl("/api/user/passport/logout")
                .addLogoutHandler(new TokenLogoutHandler(tokenManager, redisTemplate))
                .and()
                // 关键修改：显式指定过滤器顺序
                .addFilterBefore( // 优先执行 TokenAuthenticationFilter
                        new TokenAuthenticationFilter(authenticationManager(), tokenManager, redisTemplate),
                        UsernamePasswordAuthenticationFilter.class
                )
                .addFilter( // 登录过滤器保持原有逻辑
                        new TokenLoginFilter(authenticationManager(), tokenManager, redisTemplate)
                );
    }

    /**
     * 密码处理
     * @param auth
     * @throws Exception
     */
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(defaultPasswordEncoder);
    }

    /**
     *
     * 配置哪些请求不拦截
     * `HttpSecurity`用于配置那些需要经过安全过滤器链的请求，比如认证、授权等。
     * 而`WebSecurity`的`ignoring()`方法则是完全绕过整个安全过滤器链，包括自定义的过滤器，比如用户自己写的Token过滤器。
     * `permitAll()`只是允许所有用户访问，但请求仍然会经过过滤器链，而`ignoring()`则是让请求完全不经过过滤器链。
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
//                "/api/user/passport/**",
                "/api/test", // 测试地址
                "/api/user/passport/login",
                "/api/user/passport/register",
                "/api/user/passport/auth/getUserInfo",
                "/api/product/getBaseCategoryList",
                "/api/wx-pay/native/notify", // 微信支付异步通知回调放行
                "/api/wx-pay/refunds/notify", // 微信退款异步通知回调放行
//                "/api/product/list",
//                "/api/order/auth/**",
                "/api/wx-pay/native",
                "/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**"
               );
    }
}