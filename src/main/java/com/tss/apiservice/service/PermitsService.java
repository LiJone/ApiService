package com.tss.apiservice.service;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.dto.PermitsDto;
import com.tss.apiservice.po.PermitsImagePO;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.List;

public interface PermitsService {

    ReturnMsg addPermits(String userid, PermitsDto permitsDto, List<PermitsImagePO> permitsImageList);

    ReturnMsg getPermitsMsgList(HttpServletRequest request) throws ParseException;

    ReturnMsg deletePermitsMsg(String userid, PermitsDto permitsDto) throws Exception;

    ReturnMsg updatePermitsMsg(String userid, PermitsDto permitsDto, List<PermitsImagePO> permitsImageList, List<Integer> notDelIds);

    ReturnMsg getPermitsMsg(HttpServletRequest request);

    ReturnMsg getExpireDataList(HttpServletRequest request);

    ReturnMsg getPermitType(HttpServletRequest request);

    ReturnMsg getAllNum(String userid);
}
