package com.tss.apiservice.po;

import java.util.Date;

public class SafeobjsPo {
    private String objnum;

    private Integer type;

    private Integer isleave;

    private Date leavetime;

    private String osdid;

    private String jobnum;

    private String objname;

    public String getObjnum() {
        return objnum;
    }

    public void setObjnum(String objnum) {
        this.objnum = objnum == null ? null : objnum.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getOsdid() {
        return osdid;
    }

    public void setOsdid(String osdid) {
        this.osdid = osdid == null ? null : osdid.trim();
    }

    public String getJobnum() {
        return jobnum;
    }

    public void setJobnum(String jobnum) {
        this.jobnum = jobnum == null ? null : jobnum.trim();
    }

    public Integer getIsleave() {
        return isleave;
    }

    public void setIsleave(Integer isleave) {
        this.isleave = isleave;
    }

    public String getObjname() {
        return objname;
    }

    public void setObjname(String objname) {
        this.objname = objname;
    }

    public Date getLeavetime() {
        return leavetime;
    }

    public void setLeavetime(Date leavetime) {
        this.leavetime = leavetime;
    }
}