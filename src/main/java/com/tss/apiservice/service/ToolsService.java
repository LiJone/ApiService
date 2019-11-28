package com.tss.apiservice.service;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.dto.ToolsDto;
import com.tss.apiservice.po.ToolsImagePO;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.List;

public interface ToolsService {
    ReturnMsg getToolsMsgList(HttpServletRequest request) throws ParseException;

    ReturnMsg addToolsMsg(String userid, ToolsDto toolsDto, List<ToolsImagePO> toolsImageList) throws ParseException;

    ReturnMsg deleteToolsMsg(String userid, ToolsDto toolsDto);

    ReturnMsg updateToolsMsg(String userid, ToolsDto toolsDto, List<ToolsImagePO> toolsImageList, List<Integer> notDelIds) throws ParseException;

    ReturnMsg  getToolsMsg(HttpServletRequest request);

    ReturnMsg getExpireDataList(HttpServletRequest request) throws ParseException;

    ReturnMsg getToolType(HttpServletRequest request);

    ReturnMsg getAllNum(String userid);

    ReturnMsg getAllName(String userid);
}
