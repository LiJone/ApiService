package com.tss.apiservice.po;

import java.io.Serializable;

/**
 * 考勤加班实体类
 * @author 壮Jone
 */
public class AttendOTRecordPo implements Serializable {
    private static final long serialVersionUID = 5490095795104023065L;

    private Integer id;

    /**
     * 加班时间
     */
    private String ottime;

    /**
     * 加班小时
     */
    private Float hour;

    /**
     * 关联考勤id
     */
    private Integer attendid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOttime() {
        return ottime;
    }

    public void setOttime(String ottime) {
        this.ottime = ottime;
    }

    public Float getHour() {
        return hour;
    }

    public void setHour(Float hour) {
        this.hour = hour;
    }

    public Integer getAttendid() {
        return attendid;
    }

    public void setAttendid(Integer attendid) {
        this.attendid = attendid;
    }

    @Override
    public String toString() {
        return "AttendOTRecordPo{" +
                "id=" + id +
                ", ottime=" + ottime +
                ", hour=" + hour +
                ", attendid=" + attendid +
                '}';
    }
}
