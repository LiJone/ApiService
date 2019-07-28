package com.tss.apiservice.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author 壮Jone
 */
public class StaffsDto implements Serializable {
    private static final long serialVersionUID = 7637903408477984906L;
    /**
     * 员工编号
     */
    private String staffid;

    /**
     * 中文姓名
     */
    private String chname;

    /**
     * 英文姓名
     */
    private String enname;

    /**
     * 标签
     */
    private List<Map<String, String>> tag;

    /**
     * 头像数据
     */
    private List<Map<String, String>> headImageData;

    /**
     * 用户ID
     */
    private String userid;

    /**
     * 绿卡数组
     */
    private List<Map<String, String>> carDataArr;

    /**
     * 员工日薪
     */
    private Integer treatment;

    /**
     * 员工新日薪
     */
    private Integer altersalary;

    /**
     * 员工新日薪生效时间
     */
    private String effdate;

    public StaffsDto() {
    }

    public Integer getAltersalary() {
        return altersalary;
    }

    public void setAltersalary(Integer altersalary) {
        this.altersalary = altersalary;
    }

    public String getEffdate() {
        return effdate;
    }

    public void setEffdate(String effdate) {
        this.effdate = effdate;
    }

    public String getStaffid() {
        return staffid;
    }

    public void setStaffid(String staffid) {
        this.staffid = staffid;
    }

    public String getChname() {
        return chname;
    }

    public void setChname(String chname) {
        this.chname = chname;
    }

    public String getEnname() {
        return enname;
    }

    public void setEnname(String enname) {
        this.enname = enname;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }


    public List<Map<String, String>> getTag() {
        return tag;
    }

    public void setTag(List<Map<String, String>> tag) {
        this.tag = tag;
    }

    public List<Map<String, String>> getCarDataArr() {
        return carDataArr;
    }

    public void setCarDataArr(List<Map<String, String>> carDataArr) {
        this.carDataArr = carDataArr;
    }

    public List<Map<String, String>> getHeadImageData() {
        return headImageData;
    }

    public void setHeadImageData(List<Map<String, String>> headImageData) {
        this.headImageData = headImageData;
    }

    public Integer getTreatment() {
        return treatment;
    }

    public void setTreatment(Integer treatment) {
        this.treatment = treatment;
    }

    @Override
    public String toString() {
        return "StaffsDto{" +
                "staffid='" + staffid + '\'' +
                ", chname='" + chname + '\'' +
                ", enname='" + enname + '\'' +
                ", tag=" + tag +
                ", headImageData=" + headImageData +
                ", userid='" + userid + '\'' +
                ", carDataArr=" + carDataArr +
                ", treatment=" + treatment +
                ", altersalary=" + altersalary +
                ", effdate='" + effdate + '\'' +
                '}';
    }
}
