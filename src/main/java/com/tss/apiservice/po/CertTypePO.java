package com.tss.apiservice.po;

import lombok.Data;

/**
 * @author 壮Jone
 */
@Data
public class CertTypePO {

    /**
     * 证件id
     */
    private Integer id;

    /**
     * 证件种类缩写
     */
    private String abbreviation;

    /**
     * 证件种类名称
     */
    private String typename;
}
