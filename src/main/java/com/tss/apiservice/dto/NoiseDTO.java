package com.tss.apiservice.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 壮Jone
 */
@Data
public class NoiseDTO implements Serializable {
    private static final long serialVersionUID = 178089031926067296L;

    /**
     * 盒子编号
     */
    private String devsn;

    /**
     * 盒子编号
     */
    private Float noise;
}
