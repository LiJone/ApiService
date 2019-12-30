package com.tss.apiservice.po;

import java.io.Serializable;

/**
 * @author 壮Jone
 */
public class GateWayTypePO implements Serializable {
    private static final long serialVersionUID = -4041585248308052889L;

    private Integer id;

    /**
     * 网关类型型号
     */
    private String typenum;

    /**
     * 网关类型支持状态 默认1(0不支持,1支持)
     */
    private Integer enable;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTypenum() {
        return typenum;
    }

    public void setTypenum(String typenum) {
        this.typenum = typenum;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    @Override
    public String toString() {
        return "GateWayTypePO{" +
                "id=" + id +
                ", typenum='" + typenum + '\'' +
                ", enable=" + enable +
                '}';
    }
}
