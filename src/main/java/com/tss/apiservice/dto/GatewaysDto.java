package com.tss.apiservice.dto;

import java.io.Serializable;

/**
 * 网关信息传输类
 * @author 壮Jone
 */
public class GatewaysDto implements Serializable {
    private static final long serialVersionUID = 1242620390088054778L;

    /**
     * 网关唯一码
     */
    private String number;

    /**
     * 网关名称
     */
    private String name;

    /**
     * 网关协议
     */
    private String protocol;

    /**
     * 联网方式
     */
    private String network;

    /**
     * 盒子编号
     */
    private String osdid;

    /**
     * 类型
     */
    private int type;

    public GatewaysDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getOsdid() {
        return osdid;
    }

    public void setOsdid(String osdid) {
        this.osdid = osdid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "GatewaysDto{" +
                "number='" + number + '\'' +
                ", name='" + name + '\'' +
                ", protocol='" + protocol + '\'' +
                ", network='" + network + '\'' +
                ", osdid='" + osdid + '\'' +
                ", type=" + type +
                '}';
    }
}
