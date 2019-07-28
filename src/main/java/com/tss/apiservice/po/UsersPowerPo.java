package com.tss.apiservice.po;

public class UsersPowerPo {
    private Integer id;

    private String jobid;

    private String osdid;

    private Integer power;

    private Integer userid;

    private Integer osdnum;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getJobid() {
        return jobid;
    }

    public void setJobid(String jobid) {
        this.jobid = jobid == null ? null : jobid.trim();
    }

    public String getOsdid() {
        return osdid;
    }

    public void setOsdid(String osdid) {
        this.osdid = osdid == null ? null : osdid.trim();
    }

    public Integer getPower() {
        return power;
    }

    public void setPower(Integer power) {
        this.power = power;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Integer getOsdnum() {
        return osdnum;
    }

    public void setOsdnum(Integer osdnum) {
        this.osdnum = osdnum;
    }
}