package com.tss.apiservice.po;

import java.io.Serializable;

/**
 * 许可证信息实体类
 * @author 壮Jone
 */
public class PermitsPo implements Serializable {
    private static final long serialVersionUID = 3877200739757290070L;
    /**
     * 许可证id
     */
    private String permitid;

    /**
     * 许可证名称
     */
    private String name;

    /**
     * 许可证种类id
     */
    private Integer typeid;

    /**
     * 许可证种类名称
     */
    private String typename;

    /**
     * 有效开始日期
     */
    private String startdate;

    /**
     * 有效结束日期
     */
    private String enddate;

    /**
     * 文件路径
     */
    private String filepath;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 检测对象类型
     */
    private Integer type;

    /**
     * 机构id
     */
    private String orgid;

    public String getPermitid() {
        return permitid;
    }

    public void setPermitid(String permitid) {
        this.permitid = permitid == null ? null : permitid.trim();
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

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public String getOrgid() {
        return orgid;
    }

    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath == null ? null : filepath.trim();
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename == null ? null : filename.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getTypeid() {
        return typeid;
    }

    public void setTypeid(Integer typeid) {
        this.typeid = typeid;
    }

    @Override
    public String toString() {
        return "PermitsPo{" +
                "permitid='" + permitid + '\'' +
                ", name='" + name + '\'' +
                ", typeid=" + typeid +
                ", typename=" + typename +
                ", startdate='" + startdate + '\'' +
                ", enddate='" + enddate + '\'' +
                ", filepath='" + filepath + '\'' +
                ", filename='" + filename + '\'' +
                ", type=" + type +
                ", orgid='" + orgid + '\'' +
                '}';
    }
}