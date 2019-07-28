package com.tss.apiservice.common.utils;

import com.alibaba.fastjson.JSON;
import com.tss.apiservice.dto.mqDto.MQDto;
import com.tss.apiservice.po.GatewaysPo;
import com.tss.apiservice.po.SafeobjsPo;
import com.tss.apiservice.service.ApiServiceMQ;
import com.tss.apiservice.service.impl.ApiServiceMQImpl;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class MQAllSendMessage {

    public static void sendMQDtoToDataEngine(ApiServiceMQ apiServiceMQ, MQDto mqDto) {
        apiServiceMQ.sendMessageToDataEngine(JSON.toJSON(mqDto).toString());
    }

    public static void sendJobMq(String JobNum, String OSDID, int mqCode, ApiServiceMQImpl apiServiceMQ) {
        ArrayList<Object> arrayList = new ArrayList<>();
        HashMap<String, Object> map = new HashMap<>();
        if(!StringUtils.isEmpty(JobNum)){
            map.put("JobNum", String.valueOf(JobNum));
        }
        if(!StringUtils.isEmpty(OSDID)){
            map.put("OSDID", String.valueOf(OSDID));
        }
        arrayList.add(map);
        MQAllSendMessage.sendMQDtoToDataEngine(apiServiceMQ, new MQDto(mqCode, arrayList));
    }

    public static void sendJobObjLeavtime(String UserID, String TimeOut, SafeobjsPo safeobjsPo , int mqCode, ApiServiceMQImpl apiServiceMQ) {
        ArrayList<Object> arrayList = new ArrayList<>();
        HashMap<String, Object> map = new HashMap<>();
        if(!StringUtils.isEmpty(safeobjsPo.getJobnum())){
            map.put("JobNum", safeobjsPo.getJobnum());
        }
        if(!StringUtils.isEmpty(safeobjsPo.getOsdid())){
            map.put("OSDID", safeobjsPo.getOsdid());
        }
        if(!StringUtils.isEmpty(safeobjsPo.getObjnum())){
            map.put("ObjNum", safeobjsPo.getObjnum());
        }
        if(!StringUtils.isEmpty(safeobjsPo.getType())){
            map.put("ObjType", safeobjsPo.getType());
        }
        if(!StringUtils.isEmpty(TimeOut)){
            map.put("LeaveTime", TimeOut);
        }
        arrayList.add(map);
        MQAllSendMessage.sendMQDtoToDataEngine(apiServiceMQ, new MQDto(mqCode, arrayList));
    }

    public static void sendUserSettingToMq(String UserID, Integer TimeOut, int mqCode, ApiServiceMQ apiServiceMQ) {
        ArrayList<Object> arrayList = new ArrayList<>();
        HashMap<String, Object> map = new HashMap<>();
        if(!StringUtils.isEmpty(UserID)){
            map.put("UserID", UserID);
        }
        if(!StringUtils.isEmpty(TimeOut)){
            map.put("TimeOut", TimeOut);
        }
        arrayList.add(map);
        MQAllSendMessage.sendMQDtoToDataEngine(apiServiceMQ, new MQDto(mqCode, arrayList));
    }

    public static void sendGateWaysToMq(GatewaysPo gatewaysPo, int mqCode, ApiServiceMQImpl apiServiceMQ) {
        ArrayList<Object> arrayList = new ArrayList<>();
        HashMap<String, Object> map = new HashMap<>();
        if(!StringUtils.isEmpty(gatewaysPo.getNumber())){
            map.put("GatewayID", gatewaysPo.getNumber());
        }
        if(!StringUtils.isEmpty(gatewaysPo.getProtocol())){
            map.put("ProtocolType", gatewaysPo.getProtocol());
        }
        if(!StringUtils.isEmpty(gatewaysPo.getName())){
            map.put("GatewayName", gatewaysPo.getName());
        }
        arrayList.add(map);
        MQAllSendMessage.sendMQDtoToDataEngine(apiServiceMQ, new MQDto(mqCode, arrayList));
    }

}
