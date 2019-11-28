package com.tss.apiservice.service;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.dto.StaffsDto;
import com.tss.apiservice.po.StaffsImagePO;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

public interface StaffsService {
    ReturnMsg addStaffsMsg(String userid, StaffsDto staffsDto, List<StaffsImagePO> staffsImageList);

    ReturnMsg getStaffsMsgList(HttpServletRequest request) throws Exception;

    ReturnMsg deleteStaffsMsg(String userid, StaffsDto staffsDto) throws Exception;

    ReturnMsg updateStaffsMsg(String userid, StaffsDto staffsDto, List<StaffsImagePO> staffsImageList, List<Integer> notDelIds);

//    ReturnMsg getStaffsByCert(HttpServletRequest request);

    ReturnMsg getStaffsPermitsType(HttpServletRequest request);

    ReturnMsg getStaffsMsg(HttpServletRequest request);

    ReturnMsg getExpireDataList(HttpServletRequest request) throws ParseException;

    ReturnMsg getCertType(HttpServletRequest request);

    ReturnMsg getCpStaffs(HttpServletRequest request);

    ReturnMsg getAllNum(String userid);

    ReturnMsg getAllEnName(String userid);

    ReturnMsg getAllChName(String userid);
}
