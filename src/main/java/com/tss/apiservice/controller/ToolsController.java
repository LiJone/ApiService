package com.tss.apiservice.controller;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.common.utils.FilesUtils;
import com.tss.apiservice.dto.ToolsDto;
import com.tss.apiservice.po.ToolsImagePO;
import com.tss.apiservice.service.ToolsService;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
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
        ReturnMsg returnMsg = new ReturnMsg();
        if (toolsDto.getTypeid() == null) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "工具類型id為空");
            return returnMsg;
        }
        List<ToolsImagePO> toolsImageList = new ArrayList<>();
        boolean status = true;

        try {
            //保存文件
            List filesDataArr = toolsDto.getFilesDataArr();
            if (filesDataArr != null && filesDataArr.size() > 0) {
                int t = 1;
                for (Object o : filesDataArr) {
                    Map map = (HashMap) o;
                    if (!StringUtils.isEmpty(map.get("base64").toString()) && !StringUtils.isEmpty(map.get("type").toString())) {
                        String fileAllPath = FilesUtils.base64StringToFile(map.get("base64").toString(), filePath, map.get("type").toString());
                        if ("".equals(fileAllPath)) {
                            status = false;
                            returnMsg.setMsgbox("圖片上傳失敗...");
                            break;
                        } else {
                            ToolsImagePO toolsImage = new ToolsImagePO();
                            toolsImage.setToolId(toolsDto.getToolid());
                            toolsImage.setImageIndex(t);
                            toolsImage.setImageType(map.get("type").toString());
                            toolsImage.setImageDir(fileAllPath.substring(0, fileAllPath.lastIndexOf("/") + 1));
                            toolsImage.setName(map.get("name").toString());
                            if ("pdf".equals(map.get("type"))) {
                                toolsImage.setImageName(fileAllPath.substring(fileAllPath.lastIndexOf("/") + 1));
                            } else {
                                String[] split = fileAllPath.split("\\.");
                                String thumbnail = split[0] + THUMBNAIL;
                                String thumbnailPath = thumbnail + "." + split[1];
                                //生成缩略图
                                Thumbnails.of(filePath + fileAllPath).scale(0.2f).toFile(filePath + thumbnailPath);
                                toolsImage.setImageName(thumbnailPath.substring(thumbnailPath.lastIndexOf("/") + 1));
                            }
                            toolsImageList.add(toolsImage);
                            t++;
                        }
                    }
                }
            }
            if (status) {
                returnMsg = toolsService.addToolsMsg(userid, toolsDto, toolsImageList);
            }
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "工具添加失敗...");
            logger.info("/app/tools/addToolsMsg/{userid} 异常");
            e.printStackTrace();
        }
        if (returnMsg.getCode() == ReturnMsg.FAIL) {
            for (ToolsImagePO toolsImage : toolsImageList) {
                FilesUtils.deleteFile(toolsImage.getImageName(), filePath + toolsImage.getImageDir());
            }
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
        ReturnMsg returnMsg = new ReturnMsg();
        if (toolsDto.getTypeid() == null) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "工具類型id為空");
            return returnMsg;
        }

        List<ToolsImagePO> toolsImageList = new ArrayList<>();
        List<Integer> notDelIds = new ArrayList<>();
        boolean status = true;
        try {
            //保存文件
            List filesDataArr = toolsDto.getFilesDataArr();
            if (filesDataArr != null && filesDataArr.size() > 0) {
                int t = 1;
                for (Object o : filesDataArr) {
                    Map map = (HashMap) o;
                    if (!StringUtils.isEmpty(map.get("base64").toString()) && !StringUtils.isEmpty(map.get("type").toString())) {
                        String fileAllPath = FilesUtils.base64StringToFile(map.get("base64").toString(), filePath, map.get("type").toString());
                        if ("".equals(fileAllPath)) {
                            status = false;
                            returnMsg.setMsgbox("圖片上傳失敗...");
                            break;
                        } else {
                            ToolsImagePO toolsImage = new ToolsImagePO();
                            toolsImage.setToolId(toolsDto.getToolid());
                            toolsImage.setImageIndex(t);
                            toolsImage.setImageType(map.get("type").toString());
                            toolsImage.setImageDir(fileAllPath.substring(0, fileAllPath.lastIndexOf("/") + 1));
                            toolsImage.setName(map.get("name").toString());
                            if ("pdf".equals(map.get("type"))) {
                                toolsImage.setImageName(fileAllPath.substring(fileAllPath.lastIndexOf("/") + 1));
                            } else {
                                String[] split = fileAllPath.split("\\.");
                                String thumbnail = split[0] + THUMBNAIL;
                                String thumbnailPath = thumbnail + "." + split[1];
                                //生成缩略图
                                Thumbnails.of(filePath + fileAllPath).scale(0.2f).toFile(filePath + thumbnailPath);
                                toolsImage.setImageName(thumbnailPath.substring(thumbnailPath.lastIndexOf("/") + 1));
                            }
                            toolsImageList.add(toolsImage);
                            t++;
                        }
                    } else if (!StringUtils.isEmpty(map.get("id"))) {
                        ToolsImagePO toolsImage = new ToolsImagePO();
                        toolsImage.setId(Integer.parseInt(map.get("id").toString()));
                        toolsImage.setImageIndex(t);
                        toolsImageList.add(toolsImage);
                        t++;
                        notDelIds.add(Integer.parseInt(map.get("id").toString()));
                    }
                }
            }
            if (status) {
                returnMsg = toolsService.updateToolsMsg(userid, toolsDto, toolsImageList, notDelIds);
                if (returnMsg.getCode() == 1) {
                    toolsService.sendUpdateMq(toolsDto);
                }
            }
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "工具修改失敗...");
            logger.info("/app/tools/updateToolsMsg/{userid} 异常");
            e.printStackTrace();
        }
        if (returnMsg.getCode() == ReturnMsg.FAIL) {
            for (ToolsImagePO toolsImage : toolsImageList) {
                if (toolsImage.getImageName() != null) {
                    FilesUtils.deleteFile(toolsImage.getImageName(), filePath + toolsImage.getImageDir());
                }
            }
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

    @RequestMapping(value = "/app/tools/getAllName/{userid}", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getAllName(@PathVariable("userid") String userid) {
        ReturnMsg returnMsg;
        try {
            returnMsg = toolsService.getAllName(userid);
        } catch (Exception e) {
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "未獲取到相關數據...");
            logger.info("/app/tools/getAllName/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }
}
