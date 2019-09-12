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
    private Integer id;

    /**
     * 仓库id
     */
    private String osdId;

    /**
     * 工具id
     */
    private String toolsId;

    /**
     * 记录时间
     */
    private String time;

    /**
     * 机构id
     */
    private String orgId;
}
