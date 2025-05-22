package com.xunqi.gulimall.utils.vueadminelementui;

/**
 * @Description
 * @Author cisz
 * @CreateTime 2025-05-21 10:19
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;

/*** 自定义验证异常类 */
public class CustomerAuthenticationException extends AuthenticationException {
    protected static final Logger log = LoggerFactory.getLogger(CustomerAuthenticationException.class);

    public CustomerAuthenticationException(String message) {
        super(message);
        log.error(message);
    }
}
