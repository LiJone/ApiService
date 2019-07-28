package com.tss.apiservice.po;

import java.io.Serializable;
import java.util.List;

/**
 * 机构考勤设置实体类
 * @author 壮Jone
 */
public class AttendSetPo implements Serializable {
    private static final long serialVersionUID = -4004371262781459225L;

    /**
     * 机构id
     */
    private String orgid;

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
    List<AttendOTSetPo> attendOTSetPos;

    public String getOrgid() {
        return orgid;
    }

    public void setOrgid(String orgid) {
        this.orgid = orgid;
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

    public List<AttendOTSetPo> getAttendOTSetPos() {
        return attendOTSetPos;
    }

    public void setAttendOTSetPos(List<AttendOTSetPo> attendOTSetPos) {
        this.attendOTSetPos = attendOTSetPos;
    }

    @Override
    public String toString() {
        return "AttendSetPo{" +
                "orgid=" + orgid +
                ", amontime=" + amontime +
                ", amofftime=" + amofftime +
                ", pmontime=" + pmontime +
                ", pmofftime=" + pmofftime +
                '}';
    }
}
