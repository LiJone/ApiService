package com.tss.apiservice.po;

public class ObjsconditionPo {
    private int id;

    private String permitname;

    private String staffid;

    private String osdid;

    private String jobnum;

    private Integer permittypeid;

    public Integer getPermittypeid() {
        return permittypeid;
    }

    public void setPermittypeid(Integer permittypeid) {
        this.permittypeid = permittypeid;
    }

    public String getPermitname() {
        return permitname;
    }

    public void setPermitname(String permitname) {
        this.permitname = permitname == null ? null : permitname.trim();
    }

    public String getStaffid() {
        return staffid;
    }

    public void setStaffid(String staffid) {
        this.staffid = staffid == null ? null : staffid.trim();
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}