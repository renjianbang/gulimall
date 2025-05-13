//package com.xunqi.gulimall.config.product;
//
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.xunqi.gulimall.user.entity.User;
//import com.xunqi.gulimall.user.mapper.UserMapper;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsPasswordService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.provisioning.UserDetailsManager;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.util.ArrayList;
//import java.util.Collection;
//
///**
// * @Description
// * @Author cisz
// * @CreateTime 2025-04-29 11:30
// */
//@Component
//public class DBUserDetailsManager implements UserDetailsManager, UserDetailsPasswordService {
//
//    @Resource
//    private UserMapper userMapper;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("username", username);
//        User user = userMapper.selectOne(queryWrapper);
//        if (user == null) {
//            throw new UsernameNotFoundException(username);
//        } else {
//            Collection<GrantedAuthority> authorities = new ArrayList<>();
//            return new org.springframework.security.core.userdetails.User(
//                    user.getUsername(),
//                    user.getPassword(),
//                    user.getEnabled(),
//                    true, //用户账号是否过期
//                    true, //用户凭证是否过期
//                    true, //用户是否未被锁定
//                    authorities); //权限列表
//        }
//    }
//
//    @Override
//    public UserDetails updatePassword(UserDetails user, String newPassword) {
//        return null;
//    }
//
//    @Override
//    public void createUser(UserDetails userDetails) {
//        User user = new User();
//        user.setUsername(userDetails.getUsername());
//        user.setPassword(userDetails.getPassword());
//        user.setEnabled(true);
//        userMapper.insert(user);
//    }
//
//    @Override
//    public void updateUser(UserDetails user) {
//
//    }
//
//    @Override
//    public void deleteUser(String username) {
//
//    }
//
//    @Override
//    public void changePassword(String oldPassword, String newPassword) {
//
//    }
//
//    @Override
//    public boolean userExists(String username) {
//        return false;
//    }
//}