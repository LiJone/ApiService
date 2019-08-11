package com.tss.apiservice.po;

import java.io.Serializable;

/**
 * 网关信息实体类
 * @author 壮Jone
 */
public class GatewaysPo implements Serializable {
    private static final long serialVersionUID = -2656405873016995088L;

    /**
     * 网关唯一码
     */
    private String number;

    /**
     * 网关名称
     */
    private String name;

    /**
     * 网关类型 1=2.4G,2=UHF,3=RTU
     */
    private Integer type;

    /**
     * 网关协议
     */
    private String protocol;

    /**
     * 联网方式
     */
    private String network;

    /**
     * osd箱子编码
     */
    private String osdid;

    /**
     * 机构id
     */
    private String orgid;

    /**
     * 网关状态
     */
    private Boolean status;

    /**
     * 网关灵敏度
     */
    private Integer sensitivity;

    public Integer getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(Integer sensitivity) {
        this.sensitivity = sensitivity;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number == null ? null : number.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol == null ? null : protocol.trim();
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network == null ? null : network.trim();
    }

    public String getOsdid() {
        return osdid;
    }

    public void setOsdid(String osdid) {
        this.osdid = osdid == null ? null : osdid.trim();
    }

    public String getOrgid() {
        return orgid;
    }

    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }

    @Override
    public String toString() {
        return "GatewaysPo{" +
                "number='" + number + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", protocol='" + protocol + '\'' +
                ", network='" + network + '\'' +
                ", osdid='" + osdid + '\'' +
                ", orgid='" + orgid + '\'' +
                ", status=" + status +
                ", sensitivity=" + sensitivity +
                '}';
    }
}