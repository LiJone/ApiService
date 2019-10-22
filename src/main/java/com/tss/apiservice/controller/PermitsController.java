package com.tss.apiservice.controller;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.common.utils.FilesUtils;
import com.tss.apiservice.dto.PermitsDto;
import com.tss.apiservice.service.PermitsService;
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
public class PermitsController {
    private static Logger logger = LoggerFactory.getLogger(ToolsController.class);

    @Autowired
    PermitsService permitsService;

    @Value("${filePath}")
    private String filePath;

    @RequestMapping(value = "/app/permits/addPermitsMsg/{userid}", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMsg addPermitsMsg(@PathVariable("userid") String userid, @RequestBody PermitsDto permitsDto) {
        ReturnMsg<Object> returnMsg = null;

        StringBuffer fileNameStr = new StringBuffer();
        String filePathStr = null;
        List filesDataArr = null;
        try {
            //保存文件
            boolean status = true;
            String fileAllPath = null;
            filesDataArr = permitsDto.getFilesDataArr();
            Map map = null;
            if (filesDataArr != null && filesDataArr.size() > 0) {
                for (int i = 0; i < filesDataArr.size(); i++) {
                    map = (HashMap) filesDataArr.get(i);
                    if (StringUtils.isEmpty(map.get("base64").toString()) || StringUtils.isEmpty(map.get("type").toString())) {

                    } else {
                        fileAllPath = FilesUtils.base64StringToFile(map.get("base64").toString(), filePath, map.get("type").toString());
                        if (fileAllPath == null) {
                            status = false;
                            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "图片上传失敗...");
                        } else {
                            filePathStr = fileAllPath.substring(0, fileAllPath.lastIndexOf("/") + 1);
                            String fileName = fileAllPath.substring(fileAllPath.lastIndexOf("/") + 1);
                            fileNameStr.append(fileName).append(";");
                        }
                    }

                }
            }
            if (status) {
                returnMsg = permitsService.addPermits(userid, permitsDto, filePathStr, fileNameStr.toString());
            }
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "許可證錄入異常...");
            logger.info("/app/permits/addPermitsMsg/{userid} 异常");
            e.printStackTrace();
        }
        if (returnMsg.getCode() == ReturnMsg.FAIL) {
            FilesUtils.deleteFile(fileNameStr.toString(), filePath + filePathStr);
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/permits/getPermitsMsgList", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getPermitsMsgList(HttpServletRequest request) {
        ReturnMsg<Object> returnMsg = null;
        try {
            returnMsg = permitsService.getPermitsMsgList(request);
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "獲取許可證列表異常...");
            logger.info("/app/permits/getPermitsMsgList/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/permits/deletePermitsMsg/{userid}", method = RequestMethod.DELETE)
    @ResponseBody
    public ReturnMsg deletePermitsMsg(@PathVariable("userid") String userid, @RequestBody PermitsDto permitsDto) {
        ReturnMsg<Object> returnMsg = null;
        try {
            returnMsg = permitsService.deletePermitsMsg(userid, permitsDto);
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "許可證刪除異常...");
            logger.info("/app/permits/deletePermitsMsg/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/permits/updatePermitsMsg/{userid}", method = RequestMethod.PUT)
    @ResponseBody
    public ReturnMsg updatePermitsMsg(@PathVariable("userid") String userid, @RequestBody PermitsDto permitsDto) {
        ReturnMsg<Object> returnMsg = null;

        StringBuffer fileNameStr = new StringBuffer();
        String filePathStr = null;
        List filesDataArr = null;
        try {
            //保存文件
            boolean status = true;
            String fileAllPath = null;
            filesDataArr = permitsDto.getFilesDataArr();
            Map map = null;
            if (filesDataArr != null && filesDataArr.size() > 0) {
                for (int i = 0; i < filesDataArr.size(); i++) {
                    map = (HashMap) filesDataArr.get(i);
                    if (StringUtils.isEmpty(map.get("base64").toString()) || StringUtils.isEmpty(map.get("type").toString())) {

                    } else {
                        fileAllPath = FilesUtils.base64StringToFile(map.get("base64").toString(), filePath, map.get("type").toString());
                        if (fileAllPath == null) {
                            status = false;
                            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "圖片上傳失敗...");
                        } else {
                            filePathStr = fileAllPath.substring(0, fileAllPath.lastIndexOf("/") + 1);
                            String fileName = fileAllPath.substring(fileAllPath.lastIndexOf("/") + 1, fileAllPath.length());
                            fileNameStr.append(fileName).append(";");
                        }
                    }

                }
            }
            if (status) {
                returnMsg = permitsService.updatePermitsMsg(userid, permitsDto, filePathStr, fileNameStr.toString());
            }
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "許可證修改異常...");
            logger.info("/app/permits/updatePermitsMsg/{userid} 异常");
            e.printStackTrace();
        }
        if (returnMsg.getCode() == ReturnMsg.FAIL) {
            FilesUtils.deleteFile(fileNameStr.toString(), filePath + filePathStr);
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/permits/getPermitsMsg", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getPermitsMsg(HttpServletRequest request) {
        ReturnMsg returnMsg = null;
        try {
            returnMsg = permitsService.getPermitsMsg(request);
        } catch (Exception e) {
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "獲取許可證異常...");
            logger.info("/app/permits/getPermitsMsg/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/permits/getPermitType" , method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getToolType(HttpServletRequest request){
        ReturnMsg returnMsg = null;
        try {
            returnMsg = permitsService.getPermitType(request);
        } catch (Exception e) {
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "獲取許可證類型異常...");
            logger.info("/app/permits/getPermitType/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }
}
