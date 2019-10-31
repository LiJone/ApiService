package com.tss.apiservice.po;

import java.io.Serializable;
import java.util.List;

/**
 * @author 壮Jone
 */
public class AiEngineerInfoPO implements Serializable {
    private static final long serialVersionUID = 767318874086595586L;

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

    /**
     * 机构编码
     */
    private String orgId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getJobNum() {
        return jobNum;
    }

    public void setJobNum(String jobNum) {
        this.jobNum = jobNum;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getSchedule() {
        return schedule;
    }

    public void setSchedule(Integer schedule) {
        this.schedule = schedule;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    @Override
    public String toString() {
        return "AiEngineerInfoPO{" +
                "id=" + id +
                ", jobNum='" + jobNum + '\'' +
                ", jobName='" + jobName + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", schedule='" + schedule + '\'' +
                ", orgId='" + orgId + '\'' +
                '}';
    }
}

