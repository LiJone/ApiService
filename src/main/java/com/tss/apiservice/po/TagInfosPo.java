package com.tss.apiservice.po;

public class TagInfosPo {
    private String tagid;

    private String tagname;

    private String objnum;

    private Integer type;

    public String getTagid() {
        return tagid;
    }

    public void setTagid(String tagid) {
        this.tagid = tagid == null ? null : tagid.trim();
    }

    public String getTagname() {
        return tagname;
    }

    public void setTagname(String tagname) {
        this.tagname = tagname == null ? null : tagname.trim();
    }

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
}