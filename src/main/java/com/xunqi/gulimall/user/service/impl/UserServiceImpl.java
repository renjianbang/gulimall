package com.xunqi.gulimall.user.service.impl;

import com.xunqi.gulimall.auth.vo.UserRegisterVo;
//import com.xunqi.gulimall.config.product.DBUserDetailsManager;
import com.xunqi.gulimall.user.entity.User;
import com.xunqi.gulimall.user.mapper.UserMapper;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xunqi.gulimall.user.service.UserService;
import com.xunqi.gulimall.user.vo.FindUserAddressListVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
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
}
