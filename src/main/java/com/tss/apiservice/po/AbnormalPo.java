package com.tss.apiservice.po;

import java.io.Serializable;
import java.util.Date;

/**
 * 异常信息实体类
 * @author 壮Jone
 */
public class AbnormalPo implements Serializable {
    private static final long serialVersionUID = 2749055316361240695L;

    private Integer id;

    /**
     * 异常时间
     */
    private Date abtime;

    /**
     * 工程编号
     */
    private String jobnum;

    /**
     * 检测对象编码
     */
    private String number;

    /**
     * 检测对象类型
     */
    private Integer type;

    /**
     * 异常原因
     */
    private String reason;

    /**
     * 图像id
     */
    private String imageid;

    /**
     *
     */
    private String orgid;

    /**
     *
     */
    private String code;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getAbtime() {
        return abtime;
    }

    public void setAbtime(Date abtime) {
        this.abtime = abtime;
    }

    public String getJobnum() {
        return jobnum;
    }

    public void setJobnum(String jobnum) {
        this.jobnum = jobnum == null ? null : jobnum.trim();
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number == null ? null : number.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason == null ? null : reason.trim();
    }

    public String getOrgid() {
        return orgid;
    }

    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getImageid() {
        return imageid;
    }

    public void setImageid(String imageid) {
        this.imageid = imageid == null ? null : imageid.trim();
    }

    @Override
    public String toString() {
        return "AbnormalPo{" +
                "id=" + id +
                ", abtime=" + abtime +
                ", jobnum='" + jobnum + '\'' +
                ", number='" + number + '\'' +
                ", type=" + type +
                ", reason='" + reason + '\'' +
                ", imageid='" + imageid + '\'' +
                ", orgid='" + orgid + '\'' +
                ", code='" + code + '\'' +
                '}';
    }

//    public String getEnterpriseid() {
//        return enterpriseid;
//    }
//
//    public void setEnterpriseid(String enterpriseid) {
//        this.enterpriseid = enterpriseid == null ? null : enterpriseid.trim();
//    }
}