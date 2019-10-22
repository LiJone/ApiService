package com.tss.apiservice.po;

import java.io.Serializable;

/**
 * @author 壮Jone
 */
public class WSWPInfoPO implements Serializable {
    private static final long serialVersionUID = -2891224565265451584L;

    private Integer id;

    /**
     * cp编号
     */
    private String cpNum;

    /**
     * 红臂章最少个数
     */
    private Integer captainCount;

    /**
     * 工程编号
     */
    private String jobNum;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCpNum() {
        return cpNum;
    }

    public void setCpNum(String cpNum) {
        this.cpNum = cpNum;
    }

    public Integer getCaptainCount() {
        return captainCount;
    }

    public void setCaptainCount(Integer captainCount) {
        this.captainCount = captainCount;
    }

    public String getJobNum() {
        return jobNum;
    }

    public void setJobNum(String jobNum) {
        this.jobNum = jobNum;
    }

    @Override
    public String toString() {
        return "WSWPInfoPO{" +
                "id=" + id +
                ", cpNum='" + cpNum + '\'' +
                ", captainCount=" + captainCount +
                ", jobNum='" + jobNum + '\'' +
                '}';
    }
}
