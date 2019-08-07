package com.tss.apiservice.dto;

public class SafeobjsDto {

    private String osdid;
    private String jobnum;
    private String objtype;

    private String objnum;
    //分钟
    private String leavetime;

    public SafeobjsDto() {
    }

    public String getObjnum() {
        return objnum;
    }

    public void setObjnum(String objnum) {
        this.objnum = objnum;
    }

    public String getLeavetime() {
        return leavetime;
    }

    public void setLeavetime(String leavetime) {
        this.leavetime = leavetime;
    }

    public String getObjtype() {
        return objtype;
    }

    public void setObjtype(String objtype) {
        this.objtype = objtype;
    }
}
