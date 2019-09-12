package com.tss.apiservice.po;

import lombok.Data;

/**
 * @author 壮Jone
 */
@Data
public class ToolType {

    /**
     * 工具种类id
     */
    private Integer id;

    /**
     * 工具种类缩写
     */
    private String type;

    /**
     * 工具种类編號
     */
    private String typenum;

    /**
     * 工具种类名称
     */
    private String typename;

    /**
     * 工具种类规格
     */
    private String spec;

    /**
     * 承重重量
     */
    private Float weight;
}
