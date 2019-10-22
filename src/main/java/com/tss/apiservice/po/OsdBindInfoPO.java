package com.tss.apiservice.po;

import java.io.Serializable;

/**
 * @author 壮Jone
 */
public class OsdBindInfoPO implements Serializable {
    private static final long serialVersionUID = -1193725369800745132L;

    private Integer id;
    /**
     * osd编号
     */
    private String osdNum;

    /**
     * osd名称
     */
    private String osdName;

    /**
     * 绑定对象类型 0表示工程,1表示功能
     */
    private Integer bindType;

    /**
     * 绑定对象编号
     */
    private String bindNum;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOsdNum() {
        return osdNum;
    }

    public void setOsdNum(String osdNum) {
        this.osdNum = osdNum;
    }

    public String getOsdName() {
        return osdName;
    }

    public void setOsdName(String osdName) {
        this.osdName = osdName;
    }

    public Integer getBindType() {
        return bindType;
    }

    public void setBindType(Integer bindType) {
        this.bindType = bindType;
    }

    public String getBindNum() {
        return bindNum;
    }

    public void setBindNum(String bindNum) {
        this.bindNum = bindNum;
    }

    @Override
    public String toString() {
        return "GatewayBindInfoPO{" +
                "id=" + id +
                ", osdNum='" + osdNum + '\'' +
                ", osdName='" + osdName + '\'' +
                ", bindType=" + bindType +
                ", bindNum='" + bindNum + '\'' +
                '}';
    }
}
