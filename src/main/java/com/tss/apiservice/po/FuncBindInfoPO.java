package com.tss.apiservice.po;

import java.io.Serializable;

/**
 * @author 壮Jone
 */
public class FuncBindInfoPO implements Serializable {
    private static final long serialVersionUID = 6572633399857653990L;

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

    /**
     * 智能工程编号
     */
    private String jobNum;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFuncNum() {
        return funcNum;
    }

    public void setFuncNum(String funcNum) {
        this.funcNum = funcNum;
    }

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
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

    public Integer getFuncType() {
        return funcType;
    }

    public void setFuncType(Integer funcType) {
        this.funcType = funcType;
    }

    public Integer getFuncStatus() {
        return funcStatus;
    }

    public void setFuncStatus(Integer funcStatus) {
        this.funcStatus = funcStatus;
    }

    public String getJobNum() {
        return jobNum;
    }

    public void setJobNum(String jobNum) {
        this.jobNum = jobNum;
    }
}
