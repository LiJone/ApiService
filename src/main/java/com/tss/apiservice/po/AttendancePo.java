package com.tss.apiservice.po;

import java.io.Serializable;

/**
 * 考勤实体类
 * @author 壮Jone
 */
public class AttendancePo implements Serializable {
    private static final long serialVersionUID = 8882078139048095888L;
    private Integer id;

    /**
     * 考勤日期
     */
    private String attdate;

    /**
     * 工程编号
     */
    private String jobnum;

    /**
     * 检测对象编号
     */
    private String number;

    /**
     * 检测对象类型
     */
    private Integer type;

    /**
     * 检测对象日新
     */
    private Integer treatment;

    /**
     * 上午上班时间
     */
    private String amontime;

    /**
     * 上午下班时间
     */
    private String amofftime;

    /**
     * 下午上班时间
     */
    private String pmontime;

    /**
     * 下午下班时间
     */
    private String pmofftime;

    /**
     * 机构id
     */
    private String orgid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAttdate() {
        return attdate;
    }

    public void setAttdate(String attdate) {
        this.attdate = attdate;
    }

    public void setAmontime(String amontime) {
        this.amontime = amontime;
    }

    public void setAmofftime(String amofftime) {
        this.amofftime = amofftime;
    }

    public void setPmontime(String pmontime) {
        this.pmontime = pmontime;
    }

    public void setPmofftime(String pmofftime) {
        this.pmofftime = pmofftime;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getAmontime() {
        return amontime;
    }

    public String getAmofftime() {
        return amofftime;
    }

    public String getPmontime() {
        return pmontime;
    }

    public String getPmofftime() {
        return pmofftime;
    }

    public String getJobnum() {
        return jobnum;
    }

    public void setJobnum(String jobnum) {
        this.jobnum = jobnum == null ? null : jobnum.trim();
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number == null ? null : number.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getTreatment() {
        return treatment;
    }

    public void setTreatment(Integer treatment) {
        this.treatment = treatment;
    }

    public String getOrgid() {
        return orgid;
    }

    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }

    @Override
    public String toString() {
        return "AttendancePo{" +
                "id=" + id +
                ", attdate='" + attdate + '\'' +
                ", jobnum='" + jobnum + '\'' +
                ", number='" + number + '\'' +
                ", type=" + type +
                ", treatment=" + treatment +
                ", amontime='" + amontime + '\'' +
                ", amofftime='" + amofftime + '\'' +
                ", pmontime='" + pmontime + '\'' +
                ", pmofftime='" + pmofftime + '\'' +
                ", orgid='" + orgid + '\'' +
                '}';
    }
}