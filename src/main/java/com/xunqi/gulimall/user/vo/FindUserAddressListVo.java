package com.xunqi.gulimall.user.vo;

import lombok.Data;

/**
 * @Description
 * @Author cisz
 * @CreateTime 2025-05-06 16:08
 */
@Data
public class FindUserAddressListVo {

    private String id;

    private Integer isDefault;

    private String fullAddress;

    private String phoneNum;

    private String consignee;
}
