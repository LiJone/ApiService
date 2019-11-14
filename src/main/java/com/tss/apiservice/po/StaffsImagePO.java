package com.tss.apiservice.po;

/**
 * @author 壮Jone
 */
public class StaffsImagePO extends ImageBaseModel {
    private static final long serialVersionUID = 4825233167931958495L;

    /**
     * 员工编号
     */
    private String staffId;

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    @Override
    public String toString() {
        return "StaffsImagePO{" +
                "staffId='" + staffId + '\'' +
                '}';
    }
}
