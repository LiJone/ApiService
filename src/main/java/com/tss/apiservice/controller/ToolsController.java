package com.tss.apiservice.controller;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.common.utils.FilesUtils;
import com.tss.apiservice.dto.ToolsDto;
import com.tss.apiservice.service.ToolsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ToolsController {
    private static Logger logger = LoggerFactory.getLogger(ToolsController.class);

    @Autowired
    ToolsService toolsService;

    @Value("${filePath}")
    private String filePath;

    @RequestMapping(value = "/app/tools/addToolsMsg/{userid}", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMsg addToolsMsg(@PathVariable("userid") String userid, @RequestBody ToolsDto toolsDto) {
        ReturnMsg<Object> returnMsg = null;

        StringBuffer fileNameStr = new StringBuffer();//证件表名字
        String filePathStr = null;//证件表路径
        boolean status = true;
        Map map = null;
        String fileAllPath = null;
        try {
            //保存文件
            List filesDataArr = toolsDto.getFilesDataArr();
            if (filesDataArr != null && filesDataArr.size() > 0) {
                for (int i = 0; i < filesDataArr.size(); i++) {
                    map = (HashMap) filesDataArr.get(i);
                    if (StringUtils.isEmpty(map.get("base64").toString()) || StringUtils.isEmpty(map.get("type").toString())) {

                    } else {
                        fileAllPath = FilesUtils.base64StringToFile(map.get("base64").toString(), filePath, map.get("type").toString());
                        if (fileAllPath == null) {
                            status = false;
                            returnMsg.setMsgbox("圖片上傳失敗...");
                            break;
                        } else {
                            filePathStr = fileAllPath.substring(0, fileAllPath.lastIndexOf("/") + 1);
                            String fileName = fileAllPath.substring(fileAllPath.lastIndexOf("/") + 1);
                            fileNameStr.append(fileName).append(";");
                        }
                    }
                }
            }
            if (status) {
                returnMsg = toolsService.addToolsMsg(userid, toolsDto, filePathStr, fileNameStr.toString());
            }
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "工具添加異常...");
            logger.info("/app/tools/addToolsMsg/{userid} 异常");
            e.printStackTrace();
        }
        if (returnMsg.getCode() == ReturnMsg.FAIL) {
            FilesUtils.deleteFile(fileNameStr.toString(), filePath + filePathStr);
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/tools/getToolsMsgList", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getToolsMsgList(HttpServletRequest request) {
        ReturnMsg<Object> returnMsg = null;
        try {
            returnMsg = toolsService.getToolsMsgList(request);
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "獲取工具列表異常...");
            logger.info("/app/tools/getToolsMsgList/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/tools/deleteToolsMsg/{userid}", method = RequestMethod.DELETE)
    @ResponseBody
    public ReturnMsg deleteToolsMsg(@PathVariable("userid") String userid, @RequestBody ToolsDto toolsDto) {
        ReturnMsg<Object> returnMsg = null;
        try {
            returnMsg = toolsService.deleteToolsMsg(userid, toolsDto);
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "工具刪除異常...");
            logger.info("/app/tools/deleteToolsMsg/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/tools/updateToolsMsg/{userid}", method = RequestMethod.PUT)
    @ResponseBody
    public ReturnMsg updateToolsMsg(@PathVariable("userid") String userid, @RequestBody ToolsDto toolsDto) {
        ReturnMsg<Object> returnMsg = null;

        StringBuffer fileNameStr = new StringBuffer();//证件表名字
        String filePathStr = null;//证件表路径
        boolean status = true;
        Map map = null;
        String fileAllPath = null;
        try {

            //保存文件
            List filesDataArr = toolsDto.getFilesDataArr();
            if (filesDataArr != null && filesDataArr.size() > 0) {
                for (int i = 0; i < filesDataArr.size(); i++) {
                    map = (HashMap) filesDataArr.get(i);
                    if (StringUtils.isEmpty(map.get("base64").toString()) || StringUtils.isEmpty(map.get("type").toString())) {

                    } else {
                        fileAllPath = FilesUtils.base64StringToFile(map.get("base64").toString(), filePath, map.get("type").toString());
                        if (fileAllPath == null) {
                            status = false;
                            returnMsg.setMsgbox("圖片上傳失敗...");
                            break;
                        } else {
                            filePathStr = fileAllPath.substring(0, fileAllPath.lastIndexOf("/") + 1);
                            String fileName = fileAllPath.substring(fileAllPath.lastIndexOf("/") + 1, fileAllPath.length());
                            fileNameStr.append(fileName).append(";");
                        }
                    }
                }
            }
            if (status) {
                returnMsg = toolsService.updateToolsMsg(userid, toolsDto, filePathStr, fileNameStr.toString());
            }
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "工具修改異常...");
            logger.info("/app/tools/updateToolsMsg/{userid} 异常");
            e.printStackTrace();
        }
        if (returnMsg.getCode() == ReturnMsg.FAIL) {
            FilesUtils.deleteFile(fileNameStr.toString(), filePath + filePathStr);
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/tools/getToolsMsg" , method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getToolsMsg(HttpServletRequest request){
        ReturnMsg returnMsg = null;
        try {
            returnMsg = toolsService.getToolsMsg(request);
        } catch (Exception e) {
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "獲取員工異常...");
            logger.info("/app/tools/getToolsMsg/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }
}
