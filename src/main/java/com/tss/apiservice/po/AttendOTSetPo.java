package com.tss.apiservice.po;

import java.io.Serializable;

/**
 * 机构考勤加班设置实体类
 * @author 壮Jone
 */
public class AttendOTSetPo implements Serializable {
    private static final long serialVersionUID = -8073762149092403017L;

    private Integer id;

    /**
     * 加班开始时间
     */
    private String otontime;

    /**
     * 加班结束时间
     */
    private String otofftime;

    /**
     * 加班小时
     */
    private Double hour;

    /**
     * 机构id
     */
    private String orgid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Double getHour() {
        return hour;
    }

    public void setHour(Double hour) {
        this.hour = hour;
    }

    public String getOrgid() {
        return orgid;
    }

    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }

    @Override
    public String toString() {
        return "AttendOTSetPo{" +
                "id=" + id +
                ", otontime=" + otontime +
                ", otofftime=" + otofftime +
                ", hour=" + hour +
                ", orgid='" + orgid + '\'' +
                '}';
    }
}
