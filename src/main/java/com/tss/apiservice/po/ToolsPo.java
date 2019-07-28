package com.tss.apiservice.po;

import java.io.Serializable;

/**
 * 工具信息实体类
 * @author 壮Jone
 */
public class ToolsPo implements Serializable {
    private static final long serialVersionUID = -5880118143842929858L;
    /**
     * 工具编号
     */
    private String toolid;

    /**
     * 工具名称
     */
    private String name;

    /**
     * 工具图像路径
     */
    private String imagepath;

    /**
     * 工具图像名称
     */
    private String imagename;

    /**
     * 检测对象类型
     */
    private Integer type;

    /**
     * 机构id
     */
    private String orgid;

    public String getToolid() {
        return toolid;
    }

    public void setToolid(String toolid) {
        this.toolid = toolid == null ? null : toolid.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getOrgid() {
        return orgid;
    }

    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }

    @Override
    public String toString() {
        return "ToolsPo{" +
                "toolid='" + toolid + '\'' +
                ", name='" + name + '\'' +
                ", imagepath='" + imagepath + '\'' +
                ", imagename='" + imagename + '\'' +
                ", type=" + type +
                ", orgid='" + orgid + '\'' +
                '}';
    }
}