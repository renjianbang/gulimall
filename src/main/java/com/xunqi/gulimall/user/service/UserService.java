package com.xunqi.gulimall.user.service;

import com.xunqi.gulimall.auth.vo.UserRegisterVo;
import com.xunqi.gulimall.user.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xunqi.gulimall.user.vo.FindUserAddressListVo;

import java.util.List;

/**
 * <p>
 * 系统用户 服务类
 * </p>
 *
 * @author renjianbang
 * @since 2025-04-29
 */
public interface UserService extends IService<User> {

    void saveUserDetails(User user);

    void saveUser(UserRegisterVo vos);

    List<FindUserAddressListVo> findUserAddressList();
}
