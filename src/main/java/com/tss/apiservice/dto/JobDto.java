package com.tss.apiservice.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 工程信息传输类
 * @author 壮Jone
 */
public class JobDto implements Serializable {
    private static final long serialVersionUID = -2121167009655798088L;
    private Integer id;

    /**
     * 工程编号
     */
    private String jobnum;

    /**
     *工程名字
     */
    private String name;

    /**
     *盒子编号
     */
    private String osdid;

    /**
     * 起始时间
     */
    private String startTime;

    /**
     * 终止时间
     */
    private String endTime;

    /**
     * 许可证
     */
    private Map<String,List<Map<String,String>>> permits;

    /**
     * 员工
     */
    private Map<String,List<Map<String ,Object>>> staffs;

    /**
     * 工具
     */
    private Map<String,List<Map<String,String>>> tools;

    /**
     * 运行状态
     */
    private boolean runStatus;

    /**
     * osd盒子名称
     */
    private String osdname;

    public JobDto() {
    }

    public String getJobnum() {
        return jobnum;
    }

    public void setJobnum(String jobnum) {
        this.jobnum = jobnum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOsdid() {
        return osdid;
    }

    public void setOsdid(String osdid) {
        this.osdid = osdid;
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

    public Map<String, List<Map<String, String>>> getPermits() {
        return permits;
    }

    public void setPermits(Map<String, List<Map<String, String>>> permits) {
        this.permits = permits;
    }

    public Map<String, List<Map<String, String>>> getTools() {
        return tools;
    }

    public void setTools(Map<String, List<Map<String, String>>> tools) {
        this.tools = tools;
    }

    public Map<String, List<Map<String, Object>>> getStaffs() {
        return staffs;
    }

    public void setStaffs(Map<String, List<Map<String, Object>>> staffs) {
        this.staffs = staffs;
    }

    public boolean isRunStatus() {
        return runStatus;
    }

    public void setRunStatus(boolean runStatus) {
        this.runStatus = runStatus;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOsdname() {
        return osdname;
    }

    public void setOsdname(String osdname) {
        this.osdname = osdname;
    }

    @Override
    public String toString() {
        return "JobDto{" +
                "id=" + id +
                ", jobnum='" + jobnum + '\'' +
                ", name='" + name + '\'' +
                ", osdid='" + osdid + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", permits=" + permits +
                ", staffs=" + staffs +
                ", tools=" + tools +
                ", runStatus=" + runStatus +
                ", osdname='" + osdname + '\'' +
                '}';
    }
}
