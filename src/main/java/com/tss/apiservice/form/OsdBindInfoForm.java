package com.tss.apiservice.form;

import lombok.Data;

/**
 * @author 壮Jone
 */
@Data
public class OsdBindInfoForm {
    /**
     * osd编号
     */
    private String osdNum;

    /**
     * osd名称
     */
    private String osdName;

    /**
     * 绑定对象类型 0表示工程,1表示功能
     */
    private Integer bindType;

}
