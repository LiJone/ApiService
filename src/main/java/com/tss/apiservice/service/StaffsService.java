package com.tss.apiservice.service;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.dto.StaffsDto;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.HashMap;

public interface StaffsService {
    ReturnMsg addStaffsMsg(String userid, StaffsDto staffsDto, HashMap<String, String> hashMap, String headImageName , String headPath) throws ParseException;

    ReturnMsg getStaffsMsgList(HttpServletRequest request) throws Exception;

    ReturnMsg deleteStaffsMsg(String userid, StaffsDto staffsDto);

    ReturnMsg updateStaffsMsg(String userid, StaffsDto staffsDto, HashMap<String, String> hashMap, String headImageName , String headPath) throws ParseException;

//    ReturnMsg getStaffsByCert(HttpServletRequest request);

    ReturnMsg getStaffsPermitsType(HttpServletRequest request);

    ReturnMsg  getStaffsMsg(HttpServletRequest request);

    ReturnMsg getExpireDataList(HttpServletRequest request) throws ParseException;
}
