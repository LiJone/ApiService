package com.tss.apiservice.service;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.dto.PermitsDto;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

public interface PermitsService {

    ReturnMsg<Object> addPermits(String userid, PermitsDto permitsDto, String filePathStr, String fileNameStr) throws ParseException;

    ReturnMsg<Object> getPermitsMsgList(HttpServletRequest request) throws ParseException;

    ReturnMsg deletePermitsMsg(String userid, PermitsDto permitsDto);

    ReturnMsg updatePermitsMsg(String userid, PermitsDto permitsDto, String filePathStr, String fileNameStr) throws ParseException;

    ReturnMsg getPermitsMsg(HttpServletRequest request);

    ReturnMsg getExpireDataList(HttpServletRequest request) throws ParseException;
}
