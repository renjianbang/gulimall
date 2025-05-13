//package com.xunqi.gulimall.config.product;
//
//import com.xunqi.gulimall.member.entity.MemberEntity;
//import com.xunqi.gulimall.member.service.MemberService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.stream.Collectors;
//
///**
// * @Description
// * @Author cisz
// * @CreateTime 2025-04-29 16:39
// */
//@Component
//public class CustomAuthenticationProvider implements AuthenticationProvider {
//    @Autowired
//    private MemberService memberService;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Override
//    public Authentication authenticate(Authentication auth) throws AuthenticationException {
//        String username = auth.getName();
//        String password = auth.getCredentials().toString();
//
//        MemberEntity user = memberService.getByUsername(username);
//        if (user == null) {
//            throw new BadCredentialsException("用户不存在");
//        }
//
//        if (!passwordEncoder.matches(password, user.getPassword())) {
//            throw new BadCredentialsException("密码错误");
//        }
//
//        return new UsernamePasswordAuthenticationToken(
//                user,
//                null,
//                getAuthorities(user.getRoles())
//        );
//    }
//
//    private Collection<? extends GrantedAuthority> getAuthorities(String roles) {
//        return Arrays.stream(roles.split(","))
//                .map(SimpleGrantedAuthority::new)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public boolean supports(Class<?> authentication) {
//        return authentication.equals(UsernamePasswordAuthenticationToken.class);
//    }
//}