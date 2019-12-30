package com.tss.apiservice.service;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.dto.GatewaysDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface GatewaysService {

    ReturnMsg addGateways(String userid, GatewaysDto gatewaysDto);

    ReturnMsg getGateWaysMsgList(HttpServletRequest request) throws IOException;

    ReturnMsg deleteGateWaysMsg(String userid, GatewaysDto gatewaysDto);

    ReturnMsg updateGateWaysMsg( String userid, GatewaysDto gatewaysDto);

    ReturnMsg addGateWaysSetting(String userid , String number, Integer distance) throws IOException;

    ReturnMsg reloadGateWay(String number) throws IOException;

    ReturnMsg getGateWaysSetting(String number) throws IOException;

    ReturnMsg getAllName(String userid);

    void sendUpdateMq(GatewaysDto gatewaysDto);

    void sendAddMq(GatewaysDto gatewaysDto);

    ReturnMsg getGateWayType(HttpServletRequest request);
}
