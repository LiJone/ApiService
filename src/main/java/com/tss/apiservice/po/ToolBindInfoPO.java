package com.tss.apiservice.po;

import java.io.Serializable;

/**
 * @author 壮Jone
 */
public class ToolBindInfoPO implements Serializable {
    private static final long serialVersionUID = -5093658404693484225L;

    private Integer id;

    /**
     * 工具类型编号
     */
    private Integer typeId;

    /**
     * 工具类型名称
     */
    private String typeName;

    /**
     * 工具类型个数
     */
    private Integer count;

    /**
     * 功能编号
     */
    private String funcNum;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getFuncNum() {
        return funcNum;
    }

    public void setFuncNum(String funcNum) {
        this.funcNum = funcNum;
    }

    @Override
    public String toString() {
        return "ToolBindInfoPO{" +
                "id=" + id +
                ", typeId=" + typeId +
                ", typeName='" + typeName + '\'' +
                ", count=" + count +
                ", funcNum='" + funcNum + '\'' +
                '}';
    }
}
