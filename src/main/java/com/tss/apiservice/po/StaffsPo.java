package com.tss.apiservice.po;

import java.io.Serializable;
import java.util.List;

/**
 * 员工信息实体类
 * @author 壮Jone
 */
public class StaffsPo implements Serializable {

    private static final long serialVersionUID = 1859341904038938226L;
    /**
     * 员工编号
     */
    private String staffid;

    /**
     * 中文名称
     */
    private String chname;

    /**
     * 英文名
     */
    private String enname;

    /**
     * 英文姓
     */
    private String ensurname;

    /**
     * 检测对象类型
     */
    private Integer type;

    /**
     * 机构id
     */
    private String orgid;

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

    private List<StaffsImagePO> staffsImageList;

    public List<StaffsImagePO> getStaffsImageList() {
        return staffsImageList;
    }

    public void setStaffsImageList(List<StaffsImagePO> staffsImageList) {
        this.staffsImageList = staffsImageList;
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

    public String getChname() {
        return chname;
    }

    public void setChname(String chname) {
        this.chname = chname == null ? null : chname.trim();
    }

    public String getEnname() {
        return enname;
    }

    public void setEnname(String enname) {
        this.enname = enname == null ? null : enname.trim();
    }

    public String getEnsurname() {
        return ensurname;
    }

    public void setEnsurname(String ensurname) {
        this.ensurname = ensurname == null ? null : ensurname.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getStaffid() {
        return staffid;
    }

    public void setStaffid(String staffid) {
        this.staffid = staffid;
    }

    public String getOrgid() {
        return orgid;
    }

    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }

    public Integer getTreatment() {
        return treatment;
    }

    public void setTreatment(Integer treatment) {
        this.treatment = treatment;
    }

    @Override
    public String toString() {
        return "StaffsPo{" +
                "staffid='" + staffid + '\'' +
                ", chname='" + chname + '\'' +
                ", enname='" + enname + '\'' +
                ", ensurname='" + ensurname + '\'' +
                ", type=" + type +
                ", orgid='" + orgid + '\'' +
                ", treatment=" + treatment +
                ", altersalary=" + altersalary +
                ", effdate='" + effdate + '\'' +
                ", staffsImageList=" + staffsImageList +
                '}';
    }
}