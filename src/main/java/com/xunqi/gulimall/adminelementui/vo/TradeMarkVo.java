package com.xunqi.gulimall.adminelementui.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description
 * @Author cisz
 * @CreateTime 2025-05-22 09:49
 */
@Data
public class TradeMarkVo {

    @ApiModelProperty(value = "品牌ID", example = "1")
    private String id;

    @ApiModelProperty(value = "品牌名称", example = "华为")
    private String tmName;

    @ApiModelProperty(value = "品牌LOGO地址", example = "http://xxx.com/logo.jpg")
    private String logoUrl;

}
