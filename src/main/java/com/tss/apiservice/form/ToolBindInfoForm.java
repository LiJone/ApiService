package com.tss.apiservice.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author 壮Jone
 */
@Data
public class ToolBindInfoForm {

    /**
     * 工具类型编号
     */
    @NotNull(message = "工具類型編號不能為空")
    private Integer typeId;

    /**
     * 工具类型名称
     */
    @NotBlank(message = "工具類型名稱不能為空")
    private String typeName;

    /**
     * 工具类型个数
     */
    private Integer count;

    /**
     * 是否需要
     */
    @NotNull(message = "工具是否需要不能為空")
    private Integer valid;

}
