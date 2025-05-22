package com.xunqi.gulimall.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xunqi.gulimall.auth.vo.UserRegisterVo;
//import com.xunqi.gulimall.config.product.DBUserDetailsManager;
import com.xunqi.gulimall.member.entity.MemberEntity;
import com.xunqi.gulimall.member.vo.MemberUserLoginVo;
import com.xunqi.gulimall.user.entity.User;
import com.xunqi.gulimall.user.mapper.UserMapper;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xunqi.gulimall.user.service.UserService;
import com.xunqi.gulimall.user.vo.FindUserAddressListVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 系统用户 服务实现类
 * </p>
 *
 * @author renjianbang
 * @since 2025-04-29
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


//    @Resource
//    private DBUserDetailsManager dbUserDetailsManager;

    @Override
    public void saveUserDetails(User user) {

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withDefaultPasswordEncoder()
                .username(user.getUsername()) //自定义用户名
                .password(user.getPassword()) //自定义密码
                .build();
//        dbUserDetailsManager.createUser(userDetails);

    }

    @Override
    public void saveUser(UserRegisterVo vos) {
        User user = new User();
        user.setUsername(vos.getUserName());
        user.setPassword(vos.getPassword());
        user.setMobile(vos.getPhone());
//        user.setEmail(vos.getEmail());
        user.setStatus(1);
        this.save(user);
    }

    @Override
    public List<FindUserAddressListVo> findUserAddressList() {
        ArrayList<FindUserAddressListVo> res = new ArrayList<>();
        FindUserAddressListVo vo = new FindUserAddressListVo();
        vo.setId("1");
        vo.setFullAddress("广东省深圳市南山区");
        vo.setPhoneNum("123456789");
        vo.setIsDefault(1);
        vo.setConsignee("张三");
        res.add(vo);
        return res;
    }

    @Override
    public User login(User user) {

        String username = user.getUsername();
        String password = user.getPassword();

        //1、去数据库查询 SELECT * FROM ums_member WHERE username = ? OR mobile = ?
        User userOne = this.baseMapper.selectOne(new QueryWrapper<User>()
                .eq("username", username).or().eq("mobile", username));

        if (userOne == null) {
            //登录失败
            return null;
        } else {
            //获取到数据库里的password
            String password1 = user.getPassword();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            //进行密码匹配
            boolean matches = passwordEncoder.matches(password, password1);
//            if (matches) {
                //登录成功
                return userOne;
//            }
        }
    }

}
