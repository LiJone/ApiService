package com.tss.apiservice.form;

import lombok.Data;

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
    private String jobNum;

    /**
     * 工程名称
     */
    private String jobName;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 使用进度
     */
    private Integer schedule;

    private List<OsdBindInfoForm> osdBindInfoFormList;
    private List<PermitBindInfoForm> permitBindInfoFormList;
    private List<FuncBindInfoForm> funcBindInfoFormList;
    private List<WSWPInfoForm> wswpInfoFormList;
}
