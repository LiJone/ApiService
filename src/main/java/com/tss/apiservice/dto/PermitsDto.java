package com.tss.apiservice.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 许可证信息传输类
 * @author 壮Jone
 */
public class PermitsDto implements Serializable {
    private static final long serialVersionUID = 1552514575083269439L;

    /**
     * 许可证类型名称
     */
    private String typeName;

    /**
     * 许可证编号
     */
    private String permitid;

    /**
     * 许可证名称
     */
    private String name;

    /**
     * 许可证开始日期
     */
    private String startDate;

    /**
     * 许可证结束日期
     */
    private String endDate;

    /**
     * 标签
     */
    private List<Map<String,String>> tag;

    /**
     * 许可证图片
     */
    private List<Map<String,String>> filesDataArr;

    public PermitsDto() {
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getPermitid() {
        return permitid;
    }

    public void setPermitid(String permitid) {
        this.permitid = permitid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }


    public List<Map<String, String>> getTag() {
        return tag;
    }

    public void setTag(List<Map<String, String>> tag) {
        this.tag = tag;
    }

    public List<Map<String, String>> getFilesDataArr() {
        return filesDataArr;
    }

    public void setFilesDataArr(List<Map<String, String>> filesDataArr) {
        this.filesDataArr = filesDataArr;
    }

    @Override
    public String toString() {
        return "PermitsDto{" +
                "typeName='" + typeName + '\'' +
                ", permitid='" + permitid + '\'' +
                ", name='" + name + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", tag=" + tag +
                ", filesDataArr=" + filesDataArr +
                '}';
    }
}
