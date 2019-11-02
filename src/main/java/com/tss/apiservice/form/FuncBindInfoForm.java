package com.tss.apiservice.form;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author 壮Jone
 */
@Data
public class FuncBindInfoForm {

    /**
     * 功能编号
     */
    @NotBlank(message = "功能編號不能為空")
    private String funcNum;

    /**
     * 功能名称
     */
    @NotBlank(message = "功能名稱不能為空")
    private String funcName;

    /**
     * 开始时间
     */
    @NotBlank(message = "功能開始時間不能為空")
    private String startTime;

    /**
     * 结束时间
     */
    @NotBlank(message = "功能結束時間不能為空")
    private String endTime;

    /**
     * 功能类型(0:普通功能 1:特殊功能)
     */
    @NotNull(message = "功能類型不能為空")
    private Integer funcType;

    /**
     * 功能状态 默认0停用
     */
    @NotNull(message = "功能狀態不能為空")
    private Integer funcStatus;

    @Valid
    private List<OsdBindInfoForm> osdBindInfoFormList;
    @Valid
    private List<PermitBindInfoForm> permitBindInfoFormList;
    @Valid
    private List<ToolBindInfoForm> toolBindInfoFormList;
}
