package com.tss.apiservice.po;

import java.io.Serializable;

/**
 * 工程信息实体类
 * @author 壮Jone
 */
public class EngineerinfoPo implements Serializable {
    private static final long serialVersionUID = 7974881105394729371L;
    private Integer id;

    /**
     * 工程编号
     */
    private String jobnum;

    /**
     * 工程名称
     */
    private String name;

    /**
     * 执位名称
     */
    private String posname;

    /**
     * OSD箱子编号
     */
    private String osdid;

    /**
     * OSD箱子名称
     */
    private String osdname;

    /**
     * 工程开始时间
     */
    private String starttime;

    /**
     * 工程结束时间
     */
    private String endtime;

    /**
     * 使用进度
     */
    private Integer schedule;

    /**
     * 用户ID
     */
    //private Integer userid;

    /**
     * 机构id
     */
    private String orgid;

    public String getOrgid() {
        return orgid;
    }

    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getJobnum() {
        return jobnum;
    }

    public void setJobnum(String jobnum) {
        if(jobnum == null || jobnum.equals(""))
            this.jobnum = null;
        else
            this.jobnum = jobnum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getPosname() {
        return posname;
    }

    public void setPosname(String posname) {
        this.posname = posname == null ? null : posname.trim();
    }

    public String getOsdid() {
        return osdid;
    }

    public void setOsdid(String osdid) {
        this.osdid = osdid == null ? null : osdid.trim();
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public Integer getSchedule() {
        return schedule;
    }

    public void setSchedule(Integer schedule) {
        this.schedule = schedule;
    }

    public String getOsdname() {
        return osdname;
    }

    public void setOsdname(String osdname) {
        this.osdname = osdname;
    }

    @Override
    public String toString() {
        return "EngineerinfoPo{" +
                "id=" + id +
                ", jobnum='" + jobnum + '\'' +
                ", name='" + name + '\'' +
                ", posname='" + posname + '\'' +
                ", osdid='" + osdid + '\'' +
                ", osdname='" + osdname + '\'' +
                ", starttime=" + starttime +
                ", endtime=" + endtime +
                ", schedule=" + schedule +
                ", orgid='" + orgid + '\'' +
                '}';
    }
}