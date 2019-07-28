package com.tss.apiservice.dto;

import java.io.Serializable;
import java.util.List;

/**
 * @author 壮Jone
 */
public class AttendSetDto implements Serializable {
    private static final long serialVersionUID = -7341530058134908107L;

    /**
     * 上午上班时间
     */
    private String amontime;

    /**
     * 上午下班时间
     */
    private String amofftime;

    /**
     * 下午上班时间
     */
    private String pmontime;

    /**
     * 下午下班时间
     */
    private String pmofftime;

    /**
     * 设置考勤加班
     */
    List<AttendOTSetDto> attendOTSetDtos;

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

    public List<AttendOTSetDto> getAttendOTSetDtos() {
        return attendOTSetDtos;
    }

    public void setAttendOTSetDtos(List<AttendOTSetDto> attendOTSetDtos) {
        this.attendOTSetDtos = attendOTSetDtos;
    }

    @Override
    public String toString() {
        return "AttendSetDto{" +
                "amontime='" + amontime + '\'' +
                ", amofftime='" + amofftime + '\'' +
                ", pmontime='" + pmontime + '\'' +
                ", pmofftime='" + pmofftime + '\'' +
                ", attendOTSetDtos=" + attendOTSetDtos +
                '}';
    }
}
