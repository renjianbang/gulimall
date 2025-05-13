package com.xunqi.gulimall.serurity.filter;


import com.xunqi.gulimall.serurity.security.TokenManager;
import com.xunqi.gulimall.utils.product.R;
import com.xunqi.gulimall.utils.product.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 访问过滤器
 * </p>
 *
 * @author qy
 * @since 2019-11-08
 */
@Slf4j
public class TokenAuthenticationFilter extends BasicAuthenticationFilter {
    private TokenManager tokenManager;
    private RedisTemplate redisTemplate;

    public TokenAuthenticationFilter(AuthenticationManager authManager, TokenManager tokenManager, RedisTemplate redisTemplate) {
        super(authManager);
        this.tokenManager = tokenManager;
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        logger.info("访问过滤器doFilterInternal方法执行------" + req.getRequestURI());
        // 从请求头获取 Token
        String token = req.getHeader("token");
        if (token != null) {
            // 解析 Token 获取用户名
            String username = tokenManager.getUserFromToken(token);
            if (username != null) {
                // 构造 Authentication 对象（无需权限）
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                throw new RuntimeException("用户认证失败");
            }
        }
        chain.doFilter(req, res);










//        if (req.getRequestURI().indexOf("api") == -1) {
//            chain.doFilter(req, res);
//            return;
//        }
        // 示例：仅放行登录、注册等接口
//        if (req.getRequestURI().contains("/api/user/passport/login")
//                || req.getRequestURI().contains("/api/user/passport/register")) {
//            chain.doFilter(req, res);
//            return;
//        }
//
//        UsernamePasswordAuthenticationToken authentication = null;
//        try {
//            authentication = getAuthentication(req);
//        } catch (Exception e) {
////            ResponseUtil.out(res, R.error());
//            log.error(e.getMessage());
//        }
//
//        if (authentication != null) {
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        } else {
////            ResponseUtil.out(res, R.error());
//            log.error("用户认证失败");
//        }
//        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        // token置于header里
        String token = request.getHeader("token");
        log.info("Received token: {}", token); // 打印 Token
        if (token != null && !"".equals(token.trim())) {
            String userName = tokenManager.getUserFromToken(token);
            log.info("Parsed username: {}", userName); // 打印解析结果
            List<String> permissionValueList = (List<String>) redisTemplate.opsForValue().get(userName);
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            for (String permissionValue : permissionValueList) {
                if (StringUtils.isEmpty(permissionValue)) continue;
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(permissionValue);
                authorities.add(authority);
            }

            if (!StringUtils.isEmpty(userName)) {
                return new UsernamePasswordAuthenticationToken(userName, token, authorities);
            }
            return null;
        }
        return null;
    }
}