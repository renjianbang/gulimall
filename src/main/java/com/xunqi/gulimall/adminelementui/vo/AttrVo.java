package com.xunqi.gulimall.adminelementui.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Author cisz
 * @CreateTime 2025-05-22 14:42
 */
@Data
public class AttrVo {

    @ApiModelProperty(value = "属性ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "属性名称", example = "屏幕尺寸")
    private String attrName;

    @ApiModelProperty(value = "分类ID", example = "202")
    private Long categoryId;

    @ApiModelProperty(value = "分类级别", example = "3")
    private Integer categoryLevel;

    @ApiModelProperty("属性值列表")
    private List<AttrValueVo> attrValueList;

}
