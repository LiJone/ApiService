package com.tss.apiservice.form;

import lombok.Data;

/**
 * @author 壮Jone
 */
@Data
public class ToolBindInfoForm {

    /**
     * 工具类型编号
     */
    private Integer typeId;

    /**
     * 工具类型名称
     */
    private String typeName;

    /**
     * 工具类型个数
     */
    private Integer count;

}
