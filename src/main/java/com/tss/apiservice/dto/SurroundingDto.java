package com.tss.apiservice.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 壮Jone
 */
@Data
public class SurroundingDto implements Serializable {
    private static final long serialVersionUID = 3705673289140907586L;

    /**
     * 盒子编号
     */
    private String devsn;

    /**
     * 温度
     */
    private Float temp;

    /**
     * 湿度
     */
    private Float humi;

    /**
     * 风速
     */
    private Float wind;

    /**
     * 水震
     */
    private Boolean water;
}