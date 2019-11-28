package com.tss.apiservice.service;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.dto.OSDsDto;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;

public interface OSDInfosService {
    ReturnMsg<Object> addOSDinfos(String userid, OSDsDto osDsDto);

    ReturnMsg updateOSDsMsg(String userid, OSDsDto osDsDto);

    ReturnMsg deleteOSDsMsg(String userid,OSDsDto osDsDto);

    ReturnMsg getOSDsMsgList(HttpServletRequest request);

    ReturnMsg getAllNum(String userid);

    ReturnMsg getAllName(String userid);
}
