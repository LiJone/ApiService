package com.tss.apiservice.po;

import java.io.Serializable;

/**
 * @author 壮Jone
 */
public class PermitTypePO implements Serializable {
    private static final long serialVersionUID = -2325696092577697634L;

    private Integer id;

    /**
     * 许可证种类缩写
     */
    private String abbreviation;

    /**
     * 许可证种类名称
     */
    private String typename;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }
}
