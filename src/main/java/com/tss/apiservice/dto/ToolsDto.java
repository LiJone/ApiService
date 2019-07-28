package com.tss.apiservice.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 工具信息传输类
 * @author 壮Jone
 */
public class ToolsDto implements Serializable {

    private static final long serialVersionUID = 5131527417132295093L;

    /**
     * 工具编号
     */
    private String toolid;

    /**
     * 工具名称
     */
    private String name;

    /**
     * 标签编号
     */
    private List<Map<String,String>> tag;

    /**
     * 有效期
     */
    private String valiDity;

    /**
     * 工具图片
     */
    private List<Map<String,String>> filesDataArr;

    public ToolsDto() {
    }

    public String getToolid() {
        return toolid;
    }

    public void setToolid(String toolid) {
        this.toolid = toolid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Map<String, String>> getTag() {
        return tag;
    }

    public void setTag(List<Map<String, String>> tag) {
        this.tag = tag;
    }


    public String getValiDity() {
        return valiDity;
    }

    public void setValiDity(String valiDity) {
        this.valiDity = valiDity;
    }

    public List<Map<String, String>> getFilesDataArr() {
        return filesDataArr;
    }

    public void setFilesDataArr(List<Map<String, String>> filesDataArr) {
        this.filesDataArr = filesDataArr;
    }

    @Override
    public String toString() {
        return "ToolsDto{" +
                "toolid='" + toolid + '\'' +
                ", name='" + name + '\'' +
                ", tag=" + tag +
                ", valiDity='" + valiDity + '\'' +
                ", filesDataArr=" + filesDataArr +
                '}';
    }
}
