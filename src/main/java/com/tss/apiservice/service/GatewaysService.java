package com.tss.apiservice.service;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.dto.GatewaysDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;

public interface GatewaysService {

    ReturnMsg<Object> addGateways(String userid, GatewaysDto gatewaysDto);

    ReturnMsg getGateWaysMsgList(HttpServletRequest request);

    ReturnMsg deleteGateWaysMsg(String userid, GatewaysDto gatewaysDto);

    ReturnMsg updateGateWaysMsg( String userid, GatewaysDto gatewaysDto);

    ReturnMsg addGateWaysSetting(String userid , GatewaysDto gatewaysDto);
}
