package com.tss.apiservice.po;

import java.io.Serializable;

/**
 * @author 壮Jone
 */
public class PermitsImagePO extends ImageBaseModel {
    private static final long serialVersionUID = 8956453868307907846L;

    /**
     * 许可证编号
     */
    private String permitId;

    public String getPermitId() {
        return permitId;
    }

    public void setPermitId(String permitId) {
        this.permitId = permitId;
    }

    @Override
    public String toString() {
        return "PermitsImagePO{" +
                "permitId='" + permitId + '\'' +
                '}';
    }
}
