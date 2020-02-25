package com.tss.apiservice.po.vo;

import java.util.Date;

public class AbnormalExceptionVo {
    private Integer id;

    private Date abtime;

    private String jobnum;

    private  String engineerName;

    private String aiEngineerName;

    private String number;

    private Integer type;

    private String code;

    private String reason;

    private String imageid;

    //private Integer userid;

    private String orgid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getAbtime() {
        return abtime;
    }

    public void setAbtime(Date abtime) {
        this.abtime = abtime;
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason == null ? null : reason.trim();
    }

    public String getImageid() {
        return imageid;
    }

    public void setImageid(String imageid) {
        this.imageid = imageid == null ? null : imageid.trim();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOrgid() {
        return orgid;
    }

    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }

    public String getEngineerName() {
        return engineerName;
    }

    public void setEngineerName(String engineerName) {
        this.engineerName = engineerName;
    }

    public String getAiEngineerName() {
        return aiEngineerName;
    }

    public void setAiEngineerName(String aiEngineerName) {
        this.aiEngineerName = aiEngineerName;
    }
}
