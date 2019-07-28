package com.tss.apiservice.po;

import java.io.Serializable;

/**
 * 箱子信息实体类
 * @author 壮Jone
 */
public class OSDInfosPo implements Serializable {
    private static final long serialVersionUID = -1604839432274007627L;
    /**
     * osd箱子id
     */
    private String osdid;

    /**
     * osd箱子名称
     */
    private String name;

    /**
     * 机构id
     */
    private String orgid;

    public String getOsdid() {
        return osdid;
    }

    public void setOsdid(String osdid) {
        this.osdid = osdid == null ? null : osdid.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getOrgid() {
        return orgid;
    }

    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }

    @Override
    public String toString() {
        return "OSDInfosPo{" +
                "osdid='" + osdid + '\'' +
                ", name='" + name + '\'' +
                ", orgid='" + orgid + '\'' +
                '}';
    }
}