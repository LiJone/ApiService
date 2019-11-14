package com.tss.apiservice.po;

import java.io.Serializable;
import java.util.List;

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
     * 检测对象类型
     */
    private Integer type;

    /**
     * cp类型
     */
    private Integer positionid;

    /**
     * 吊运绳子最低承受重量
     */
    private Float ropeweight;

    /**
     * 机构id
     */
    private String orgid;

    private List<PermitsImagePO> permitsImageList;

    public List<PermitsImagePO> getPermitsImageList() {
        return permitsImageList;
    }

    public void setPermitsImageList(List<PermitsImagePO> permitsImageList) {
        this.permitsImageList = permitsImageList;
    }

    public Integer getPositionid() {
        return positionid;
    }

    public void setPositionid(Integer positionid) {
        this.positionid = positionid;
    }

    public Float getRopeweight() {
        return ropeweight;
    }

    public void setRopeweight(Float ropeweight) {
        this.ropeweight = ropeweight;
    }

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
                ", typename='" + typename + '\'' +
                ", startdate='" + startdate + '\'' +
                ", enddate='" + enddate + '\'' +
                ", type=" + type +
                ", positionid=" + positionid +
                ", ropeweight=" + ropeweight +
                ", orgid='" + orgid + '\'' +
                ", permitsImageList=" + permitsImageList +
                '}';
    }
}