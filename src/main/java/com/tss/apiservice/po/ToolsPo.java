package com.tss.apiservice.po;

import java.io.Serializable;
import java.util.List;

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
     * 工具類型id
     */
    private Integer typeid;

    /**
     * 工具种类名称
     */
    private String typename;

    /**
     * 检测对象类型
     */
    private Integer type;

    /**
     * 有效期
     */
    private String validity;

    /**
     * 机构id
     */
    private String orgid;

    /**
     * 工具图像数据
     */
    private List<ToolsImagePO> toolsImageList;

    public List<ToolsImagePO> getToolsImageList() {
        return toolsImageList;
    }

    public void setToolsImageList(List<ToolsImagePO> toolsImageList) {
        this.toolsImageList = toolsImageList;
    }

    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    public Integer getTypeid() {
        return typeid;
    }

    public void setTypeid(Integer typeid) {
        this.typeid = typeid;
    }

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

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    @Override
    public String toString() {
        return "ToolsPo{" +
                "toolid='" + toolid + '\'' +
                ", name='" + name + '\'' +
                ", typeid=" + typeid +
                ", typename='" + typename + '\'' +
                ", type=" + type +
                ", validity='" + validity + '\'' +
                ", orgid='" + orgid + '\'' +
                ", toolsImageList=" + toolsImageList +
                '}';
    }
}