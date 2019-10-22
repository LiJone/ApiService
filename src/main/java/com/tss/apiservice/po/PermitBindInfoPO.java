package com.tss.apiservice.po;

import java.io.Serializable;

/**
 * @author 壮Jone
 */
public class PermitBindInfoPO implements Serializable {
    private static final long serialVersionUID = 7066697026550587910L;

    private Integer id;
    /**
     * 许可证编号
     */
    private String permitNum;

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

    public String getPermitNum() {
        return permitNum;
    }

    public void setPermitNum(String permitNum) {
        this.permitNum = permitNum;
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
        return "PermitBindInfoPO{" +
                "id=" + id +
                ", permitNum='" + permitNum + '\'' +
                ", bindType=" + bindType +
                ", bindNum='" + bindNum + '\'' +
                '}';
    }
}
