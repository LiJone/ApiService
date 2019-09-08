package com.tss.apiservice.po;

import lombok.Data;

/**
 * 仓库管理实体类
 * @author 壮Jone
 */
@Data
public class DepositoryPO {
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
     * 记录时间
     */
    private String time;

}
