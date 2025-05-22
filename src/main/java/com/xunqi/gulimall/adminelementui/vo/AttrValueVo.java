package com.xunqi.gulimall.adminelementui.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description
 * @Author cisz
 * @CreateTime 2025-05-22 14:46
 */
// 属性值VO
@Data
@ApiModel(description = "属性值")
public class AttrValueVo {
    @ApiModelProperty(value = "属性值ID", example = "5001")
    private Long id;

    @ApiModelProperty(value = "属性值名称", example = "6.1英寸")
    private String valueName;

    @ApiModelProperty(value = "所属属性ID", example = "1")
    private Long attrId;
}