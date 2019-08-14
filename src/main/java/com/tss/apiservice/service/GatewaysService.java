package com.tss.apiservice.service;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.dto.GatewaysDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface GatewaysService {

    ReturnMsg<Object> addGateways(String userid, GatewaysDto gatewaysDto);

    ReturnMsg getGateWaysMsgList(HttpServletRequest request) throws IOException;

    ReturnMsg deleteGateWaysMsg(String userid, GatewaysDto gatewaysDto);

    ReturnMsg updateGateWaysMsg( String userid, GatewaysDto gatewaysDto);

    ReturnMsg addGateWaysSetting(String userid , String number, Integer distance) throws IOException;

    ReturnMsg<Object> reloadGateWay(String number) throws IOException;

    ReturnMsg<Object> getGateWaysSetting(String number) throws IOException;
}
