package com.tss.apiservice.service;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.dto.ToolsDto;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

public interface ToolsService {
    ReturnMsg getToolsMsgList(HttpServletRequest request);

    ReturnMsg addToolsMsg(String userid, ToolsDto toolsDto, String filePathStr, String fileNameStr) throws ParseException;

    ReturnMsg deleteToolsMsg(String userid, ToolsDto toolsDto);

    ReturnMsg updateToolsMsg(String userid, ToolsDto toolsDto, String filePathStr, String fileNameStr) throws ParseException;

    ReturnMsg  getToolsMsg(HttpServletRequest request);
}
