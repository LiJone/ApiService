package com.tss.apiservice.form;

import lombok.Data;

import java.util.List;

/**
 * @author 壮Jone
 */
@Data
public class FuncBindInfoForm {
    private Integer id;

    /**
     * 功能编号
     */
    private String funcNum;

    /**
     * 功能名称
     */
    private String funcName;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 功能类型(0:普通功能 1:特殊功能)
     */
    private Integer funcType;

    /**
     * 功能状态 默认0停用
     */
    private Integer funcStatus;


    private List<OsdBindInfoForm> osdBindInfoFormList;

    private List<PermitBindInfoForm> permitBindInfoFormList;

    private List<ToolBindInfoForm> toolBindInfoFormList;
}
