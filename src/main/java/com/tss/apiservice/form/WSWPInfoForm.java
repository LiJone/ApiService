package com.tss.apiservice.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author 壮Jone
 */
@Data
public class WSWPInfoForm {

    /**
     * cp编号
     */
    @NotBlank(message = "CP編號不能為空")
    private String cpNum;

    /**
     * 红臂章最少个数
     */
    private Integer captainCount;

}
