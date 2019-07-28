package com.tss.apiservice.dto;

import java.util.Date;

public class UsersSetDto {

    private String amontime;

    private String amofftime;

    private String pmontime;

    private String pmofftime;

    private String otontime;

    private String otofftime;

    private Integer timeout;

    public UsersSetDto() {
    }


    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public String getAmontime() {
        return amontime;
    }

    public void setAmontime(String amontime) {
        this.amontime = amontime;
    }

    public String getAmofftime() {
        return amofftime;
    }

    public void setAmofftime(String amofftime) {
        this.amofftime = amofftime;
    }

    public String getPmontime() {
        return pmontime;
    }

    public void setPmontime(String pmontime) {
        this.pmontime = pmontime;
    }

    public String getPmofftime() {
        return pmofftime;
    }

    public void setPmofftime(String pmofftime) {
        this.pmofftime = pmofftime;
    }

    public String getOtontime() {
        return otontime;
    }

    public void setOtontime(String otontime) {
        this.otontime = otontime;
    }

    public String getOtofftime() {
        return otofftime;
    }

    public void setOtofftime(String otofftime) {
        this.otofftime = otofftime;
    }
}
