package com.tss.apiservice.dto.mqDto;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.List;

public class MQDto {
    @JSONField(name="Code")
    private int Code;//消息编码

    @JSONField(name="Data")
    private List Data;

    public MQDto() {
    }

    public MQDto(int Code, List Data) {
        this.Code = Code;
        this.Data = Data;
    }


    public int getCode() {
        return Code;
    }

    public void setCode(int code) {
        Code = code;
    }

    public List getData() {
        return Data;
    }

    public void setData(ArrayList data) {
        Data = data;
    }

}
