package com.tss.apiservice.po;

import java.io.Serializable;

public class PermitsImagePo implements Serializable {
    private Integer id;
    private Integer imageindex;
    private String imagename;
    private String imagetype;
    private String permitid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getImageindex() {
        return imageindex;
    }

    public void setImageindex(Integer imageindex) {
        this.imageindex = imageindex;
    }

    public String getImagename() {
        return imagename;
    }

    public void setImagename(String imagename) {
        this.imagename = imagename;
    }

    public String getImagetype() {
        return imagetype;
    }

    public void setImagetype(String imagetype) {
        this.imagetype = imagetype;
    }

    public String getPermitid() {
        return permitid;
    }

    public void setPermitid(String permitid) {
        this.permitid = permitid;
    }

    @Override
    public String toString() {
        return "permitsImagePo{" +
                "id=" + id +
                ", imageindex=" + imageindex +
                ", imagename='" + imagename + '\'' +
                ", imagetype='" + imagetype + '\'' +
                ", permitid='" + permitid + '\'' +
                '}';
    }
}
