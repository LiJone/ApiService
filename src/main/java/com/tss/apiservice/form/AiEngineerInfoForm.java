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
public class AiEngineerInfoForm {
    private Integer id;

    /**
     * 工程编号
     */
    @NotBlank(message = "工程編號不能為空")
    private String jobNum;

    /**
     * 工程名称
     */
    @NotBlank(message = "工程名稱不能為空")
    private String jobName;

    /**
     * 开始时间
     */
    @NotBlank(message = "工程開始時間不能為空")
    private String startTime;

    /**
     * 结束时间
     */
    @NotBlank(message = "工程結束時間不能為空")
    private String endTime;

    /**
     * 使用进度
     */
    @NotNull(message = "工程狀態不能為空")
    private Integer schedule;

    @Valid
    private List<OsdBindInfoForm> osdBindInfoFormList;
    @Valid
    private List<PermitBindInfoForm> permitBindInfoFormList;
    @Valid
    private List<FuncBindInfoForm> funcBindInfoFormList;
    @Valid
    private List<WSWPInfoForm> wswpInfoFormList;
}
