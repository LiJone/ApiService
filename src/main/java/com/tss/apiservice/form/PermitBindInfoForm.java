package com.tss.apiservice.form;

import lombok.Data;

/**
 * @author 壮Jone
 */
@Data
public class PermitBindInfoForm {
    /**
     * 许可证编号
     */
    private String permitNum;

    /**
     * 绑定对象类型 0表示工程,1表示功能
     */
    private Integer bindType;
}
