package com.tss.apiservice.dto;

import java.io.Serializable;

/**
 * 盒子传输实体类
 * @author 壮Jone
 */
public class OSDsDto implements Serializable {
    private static final long serialVersionUID = -828473238745253929L;
    /**
     * osd盒子id
     */
    private String osdid;

    /**
     * osd盒子名称
     */
    private String name;

    public OSDsDto() {
    }

    public String getOsdid() {
        return osdid;
    }

    public void setOsdid(String osdid) {
        this.osdid = osdid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "OSDsDto{" +
                "osdid='" + osdid + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
