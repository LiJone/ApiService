package com.tss.apiservice.dto;

import java.io.Serializable;

/**
 * @author 壮Jone
 */
public class AttendOTSetDto implements Serializable {
    private static final long serialVersionUID = -5262302755841443754L;

    /**
     * 加班开始时间
     */
    private String otontime;

    /**
     * 加班介绍时间
     */
    private String otofftime;

    /**
     * 加班小时
     */
    private Double hour;

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

    public Double getHour() {
        return hour;
    }

    public void setHour(Double hour) {
        this.hour = hour;
    }

    @Override
    public String toString() {
        return "AttendOTSetDto{" +
                "otontime='" + otontime + '\'' +
                ", otofftime='" + otofftime + '\'' +
                ", hour=" + hour +
                '}';
    }
}
