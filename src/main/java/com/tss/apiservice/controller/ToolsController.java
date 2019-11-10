package com.tss.apiservice.controller;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.common.utils.FilesUtils;
import com.tss.apiservice.dto.ToolsDto;
import com.tss.apiservice.service.ToolsService;
import net.coobird.thumbnailator.Thumbnails;
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
    private static final String THUMBNAIL = "_thumbnail";

    @Autowired
    ToolsService toolsService;

    @Value("${filePath}")
    private String filePath;

    @RequestMapping(value = "/app/tools/addToolsMsg/{userid}", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMsg addToolsMsg(@PathVariable("userid") String userid, @RequestBody ToolsDto toolsDto) {
        ReturnMsg returnMsg = null;
        if (toolsDto.getTypeid() == null) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "工具類型id為空");
            return returnMsg;
        }
        //证件表名字
        StringBuilder fileNameStr = new StringBuilder();
        //证件表路径
        String filePathStr = null;
        String thumbnailPath;
        StringBuilder thumbnailHeadNameSb = new StringBuilder();
        boolean status = true;
        Map map;
        String fileAllPath;
        try {
            //保存文件
            List filesDataArr = toolsDto.getFilesDataArr();
            if (filesDataArr != null && filesDataArr.size() > 0) {
                for (Object o : filesDataArr) {
                    map = (HashMap) o;
                    if (!StringUtils.isEmpty(map.get("base64").toString()) && !StringUtils.isEmpty(map.get("type").toString())) {
                        fileAllPath = FilesUtils.base64StringToFile(map.get("base64").toString(), filePath, map.get("type").toString());
                        String[] split = fileAllPath.split("\\.");
                        String thumbnail = split[0] + THUMBNAIL;
                        thumbnailPath = thumbnail + "." + split[1];
                        //生成缩略图
                        Thumbnails.of(filePath + fileAllPath).scale(0.2f).toFile(filePath + thumbnailPath);
                        if ("".equals(fileAllPath)) {
                            status = false;
                            returnMsg.setMsgbox("圖片上傳失敗...");
                            break;
                        } else {
                            filePathStr = fileAllPath.substring(0, fileAllPath.lastIndexOf("/") + 1);
                            String fileName = fileAllPath.substring(fileAllPath.lastIndexOf("/") + 1);
                            fileNameStr.append(fileName).append(";");
                            thumbnailHeadNameSb.append(thumbnailPath.substring(thumbnailPath.lastIndexOf("/") + 1)).append(";");
                        }
                    }
                }
            }
            if (status) {
                returnMsg = toolsService.addToolsMsg(userid, toolsDto, filePathStr, thumbnailHeadNameSb.toString());
            }
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "工具添加失敗...");
            logger.info("/app/tools/addToolsMsg/{userid} 异常");
            e.printStackTrace();
        }
        if (returnMsg.getCode() == ReturnMsg.FAIL) {
            FilesUtils.deleteFile(fileNameStr.toString(), filePath + filePathStr);
            FilesUtils.deleteFile(thumbnailHeadNameSb.toString(), filePath + filePathStr);
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/tools/getToolsMsgList", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getToolsMsgList(HttpServletRequest request) {
        ReturnMsg returnMsg;
        try {
            returnMsg = toolsService.getToolsMsgList(request);
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "未獲取到相關數據...");
            logger.info("/app/tools/getToolsMsgList/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/tools/deleteToolsMsg/{userid}", method = RequestMethod.DELETE)
    @ResponseBody
    public ReturnMsg deleteToolsMsg(@PathVariable("userid") String userid, @RequestBody ToolsDto toolsDto) {
        ReturnMsg returnMsg;
        try {
            returnMsg = toolsService.deleteToolsMsg(userid, toolsDto);
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "工具刪除失敗...");
            logger.info("/app/tools/deleteToolsMsg/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/tools/updateToolsMsg/{userid}", method = RequestMethod.PUT)
    @ResponseBody
    public ReturnMsg updateToolsMsg(@PathVariable("userid") String userid, @RequestBody ToolsDto toolsDto) {
        ReturnMsg returnMsg = null;
        if (toolsDto.getTypeid() == null) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "工具類型id為空");
            return returnMsg;
        }
        //证件表名字
        StringBuilder fileNameStr = new StringBuilder();
        //证件表路径
        String filePathStr = null;
        String thumbnailPath;
        StringBuilder thumbnailHeadNameSb = new StringBuilder();
        boolean status = true;
        Map map;
        String fileAllPath;
        try {

            //保存文件
            List filesDataArr = toolsDto.getFilesDataArr();
            if (filesDataArr != null && filesDataArr.size() > 0) {
                for (Object o : filesDataArr) {
                    map = (HashMap) o;
                    if (!StringUtils.isEmpty(map.get("base64").toString()) && !StringUtils.isEmpty(map.get("type").toString())) {
                        fileAllPath = FilesUtils.base64StringToFile(map.get("base64").toString(), filePath, map.get("type").toString());
                        String[] split = fileAllPath.split("\\.");
                        String thumbnail = split[0] + THUMBNAIL;
                        thumbnailPath = thumbnail + "." + split[1];
                        //生成缩略图
                        Thumbnails.of(filePath + fileAllPath).scale(0.2f).toFile(filePath + thumbnailPath);
                        if ("".equals(fileAllPath)) {
                            status = false;
                            returnMsg.setMsgbox("圖片上傳失敗...");
                            break;
                        } else {
                            filePathStr = fileAllPath.substring(0, fileAllPath.lastIndexOf("/") + 1);
                            String fileName = fileAllPath.substring(fileAllPath.lastIndexOf("/") + 1);
                            fileNameStr.append(fileName).append(";");
                            thumbnailHeadNameSb.append(thumbnailPath.substring(thumbnailPath.lastIndexOf("/") + 1)).append(";");
                        }
                    }
                }
            }
            if (status) {
                returnMsg = toolsService.updateToolsMsg(userid, toolsDto, filePathStr, thumbnailHeadNameSb.toString());
            }
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "工具修改失敗...");
            logger.info("/app/tools/updateToolsMsg/{userid} 异常");
            e.printStackTrace();
        }
        if (returnMsg.getCode() == ReturnMsg.FAIL) {
            FilesUtils.deleteFile(fileNameStr.toString(), filePath + filePathStr);
            FilesUtils.deleteFile(thumbnailHeadNameSb.toString(), filePath + filePathStr);
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/tools/getToolsMsg" , method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getToolsMsg(HttpServletRequest request){
        ReturnMsg returnMsg;
        try {
            returnMsg = toolsService.getToolsMsg(request);
        } catch (Exception e) {
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "未獲取到相關數據...");
            logger.info("/app/tools/getToolsMsg/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/tools/getToolType" , method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getToolType(HttpServletRequest request){
        ReturnMsg returnMsg;
        try {
            returnMsg = toolsService.getToolType(request);
        } catch (Exception e) {
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "未獲取到相關數據...");
            logger.info("/app/tools/getToolType/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/tools/getAllNum/{userid}", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getAllNum(@PathVariable("userid") String userid) {
        ReturnMsg returnMsg;
        try {
            returnMsg = toolsService.getAllNum(userid);
        } catch (Exception e) {
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "未獲取到相關數據...");
            logger.info("/app/tools/getAllNum/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }
}
