package com.tss.apiservice.form;

import lombok.Data;

/**
 * @author 壮Jone
 */
@Data
public class WSWPInfoForm {
    private Integer id;

    /**
     * cp编号
     */
    private String cpNum;

    /**
     * 红臂章最少个数
     */
    private Integer captainCount;

}
