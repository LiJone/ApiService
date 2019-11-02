package com.tss.apiservice.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author 壮Jone
 */
@Data
public class OsdBindInfoForm {
    /**
     * osd编号
     */
    @NotBlank(message = "盒子編號不能為空")
    private String osdNum;

    /**
     * osd名称
     */
    @NotBlank(message = "盒子名稱不能為空")
    private String osdName;

    /**
     * 绑定对象类型 0表示工程,1表示功能
     */
    private Integer bindType;

}
