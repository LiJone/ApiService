package com.tss.apiservice.po;

import java.util.Date;

public class RecordsPo {
    private Integer id;

    private String jobname;

    private String posname;

    private String osdid;

    private String number;

    private Integer type;

    private Date recorddate;

    private Date recordtime;

    private Integer tagstatus;

    private Integer userid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getJobname() {
        return jobname;
    }

    public void setJobname(String jobname) {
        this.jobname = jobname == null ? null : jobname.trim();
    }

    public String getPosname() {
        return posname;
    }

    public void setPosname(String posname) {
        this.posname = posname == null ? null : posname.trim();
    }

    public String getOsdid() {
        return osdid;
    }

    public void setOsdid(String osdid) {
        this.osdid = osdid == null ? null : osdid.trim();
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

    public Date getRecorddate() {
        return recorddate;
    }

    public void setRecorddate(Date recorddate) {
        this.recorddate = recorddate;
    }

    public Date getRecordtime() {
        return recordtime;
    }

    public void setRecordtime(Date recordtime) {
        this.recordtime = recordtime;
    }

    public Integer getTagstatus() {
        return tagstatus;
    }

    public void setTagstatus(Integer tagstatus) {
        this.tagstatus = tagstatus;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }
}