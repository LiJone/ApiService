package com.tss.apiservice.dto;

import lombok.Data;

@Data
public class DepositoryStatisticDTO {
    private String date;

    private String osdId;

    private String osdName;

    private String typeName;

    private Integer count;
}
