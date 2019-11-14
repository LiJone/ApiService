package com.tss.apiservice.po;

import java.io.Serializable;

/**
 * @author 壮Jone
 */
public class ToolsImagePO extends ImageBaseModel {
    private static final long serialVersionUID = 1482494582333934469L;

    /**
     * 工具编号
     */
    private String toolId;

    public String getToolId() {
        return toolId;
    }

    public void setToolId(String toolId) {
        this.toolId = toolId;
    }

    @Override
    public String toString() {
        return "ToolsImagePO{" +
                "toolId='" + toolId + '\'' +
                '}';
    }
}
