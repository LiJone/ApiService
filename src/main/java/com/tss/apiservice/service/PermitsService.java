package com.tss.apiservice.service;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.dto.PermitsDto;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

public interface PermitsService {

    ReturnMsg addPermits(String userid, PermitsDto permitsDto, String filePathStr, String fileNameStr);

    ReturnMsg getPermitsMsgList(HttpServletRequest request) throws ParseException;

    ReturnMsg deletePermitsMsg(String userid, PermitsDto permitsDto) throws Exception;

    ReturnMsg updatePermitsMsg(String userid, PermitsDto permitsDto, String filePathStr, String fileNameStr);

    ReturnMsg getPermitsMsg(HttpServletRequest request);

    ReturnMsg getExpireDataList(HttpServletRequest request);

    ReturnMsg getPermitType(HttpServletRequest request);

    ReturnMsg getAllNum(String userid);
}
