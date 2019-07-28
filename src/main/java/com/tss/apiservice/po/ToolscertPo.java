package com.tss.apiservice.po;

import java.io.Serializable;

/**
 * @author 壮Jone
 */
public class ToolscertPo implements Serializable {
    private static final long serialVersionUID = 3421702444078884894L;
    private String certid;

    private String name;

    private String typename;

    private String validity;

    private String imagepath;

    private String imagename;

    private String toolid;

    public String getCertid() {
        return certid;
    }

    public void setCertid(String certid) {
        this.certid = certid == null ? null : certid.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename == null ? null : typename.trim();
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath == null ? null : imagepath.trim();
    }

    public String getImagename() {
        return imagename;
    }

    public void setImagename(String imagename) {
        this.imagename = imagename == null ? null : imagename.trim();
    }

    public String getToolid() {
        return toolid;
    }

    public void setToolid(String toolid) {
        this.toolid = toolid == null ? null : toolid.trim();
    }

    @Override
    public String toString() {
        return "ToolscertPo{" +
                "certid='" + certid + '\'' +
                ", name='" + name + '\'' +
                ", typename='" + typename + '\'' +
                ", validity='" + validity + '\'' +
                ", imagepath='" + imagepath + '\'' +
                ", imagename='" + imagename + '\'' +
                ", toolid='" + toolid + '\'' +
                '}';
    }
}