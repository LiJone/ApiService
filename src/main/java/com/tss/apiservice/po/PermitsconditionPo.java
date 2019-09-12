package com.tss.apiservice.po;

public class PermitsconditionPo {
    private Integer id;

    private String permitname;

    private String certname;

    private Integer typeid;

    private Integer type;

    public Integer getTypeid() {
        return typeid;
    }

    public void setTypeid(Integer typeid) {
        this.typeid = typeid;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPermitname() {
        return permitname;
    }

    public void setPermitname(String permitname) {
        this.permitname = permitname == null ? null : permitname.trim();
    }

    public String getCertname() {
        return certname;
    }

    public void setCertname(String certname) {
        this.certname = certname == null ? null : certname.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}