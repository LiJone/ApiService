package com.tss.apiservice.dto;

import lombok.Data;

/**
 * 仓库传输类
 * @author 壮Jone
 */
@Data
public class DepositoryDTO {
    /**
     * 主键
     */
    private String id;

    /**
     * 仓库id
     */
    private String depositoryId;

    /**
     * 工具id
     */
    private String toolsId;

    /**
     * 工具名称
     */
    private String name;

    /**
     * 工具类型
     */
    private String type;

    /**
     * 工具数量
     */
    private Long count;

    /**
     * 记录时间
     */
    private String time;

    /**
     * 记录状态
     */
    private Integer status;
}
