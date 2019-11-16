package com.tss.apiservice.controller;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.common.utils.FilesUtils;
import com.tss.apiservice.dto.PermitsDto;
import com.tss.apiservice.po.PermitsImagePO;
import com.tss.apiservice.po.ToolsImagePO;
import com.tss.apiservice.service.PermitsService;
import net.coobird.thumbnailator.Thumbnails;
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
public class PermitsController {
    private static Logger logger = LoggerFactory.getLogger(ToolsController.class);
    private static final String THUMBNAIL = "_thumbnail";
    @Autowired
    private PermitsService permitsService;

    @Value("${filePath}")
    private String filePath;

    @RequestMapping(value = "/app/permits/addPermitsMsg/{userid}", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMsg addPermitsMsg(@PathVariable("userid") String userid, @RequestBody PermitsDto permitsDto) {
        ReturnMsg returnMsg = new ReturnMsg();
        if (permitsDto.getTypeid() == null) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "許可證類型id為空");
            return returnMsg;
        }
        List<PermitsImagePO> permitsImageList = new ArrayList<>();
        boolean status = true;
        try {
            //保存文件
            List filesDataArr = permitsDto.getFilesDataArr();
            if (filesDataArr != null && filesDataArr.size() > 0) {
                int t = 1;
                for (Object o : filesDataArr) {
                    Map map = (HashMap) o;
                    if (!StringUtils.isEmpty(map.get("base64").toString()) && !StringUtils.isEmpty(map.get("type").toString())) {
                        String fileAllPath = FilesUtils.base64StringToFile(map.get("base64").toString(), filePath, map.get("type").toString());
                        if ("".equals(fileAllPath)) {
                            status = false;
                            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "图片上传失敗...");
                        } else {
                            PermitsImagePO permitsImage = new PermitsImagePO();
                            permitsImage.setPermitId(permitsDto.getPermitid());
                            permitsImage.setImageIndex(t);
                            permitsImage.setImageType(map.get("type").toString());
                            permitsImage.setImageDir(fileAllPath.substring(0, fileAllPath.lastIndexOf("/") + 1));
                            permitsImage.setName(map.get("name").toString());
                            if ("pdf".equals(map.get("type"))) {
                                permitsImage.setImageName(fileAllPath.substring(fileAllPath.lastIndexOf("/") + 1));
                            } else {
                                String[] split = fileAllPath.split("\\.");
                                String thumbnail = split[0] + THUMBNAIL;
                                String thumbnailPath = thumbnail + "." + split[1];
                                //生成缩略图
                                Thumbnails.of(filePath + fileAllPath).scale(0.2f).toFile(filePath + thumbnailPath);
                                permitsImage.setImageName(thumbnailPath.substring(thumbnailPath.lastIndexOf("/") + 1));
                            }
                            permitsImageList.add(permitsImage);
                            t++;
                        }
                    }
                }
            }
            if (status) {
                returnMsg = permitsService.addPermits(userid, permitsDto, permitsImageList);
            }
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "許可證錄入失敗...");
            logger.info("/app/permits/addPermitsMsg/{userid} 异常");
            e.printStackTrace();
        }
        if (returnMsg.getCode() == ReturnMsg.FAIL) {
            for (PermitsImagePO permitsImage : permitsImageList) {
                FilesUtils.deleteFile(permitsImage.getImageName(), filePath + permitsImage.getImageDir());
            }
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/permits/getPermitsMsgList", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getPermitsMsgList(HttpServletRequest request) {
        ReturnMsg returnMsg;
        try {
            returnMsg = permitsService.getPermitsMsgList(request);
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "未獲取到相關數據...");
            logger.info("/app/permits/getPermitsMsgList/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/permits/deletePermitsMsg/{userid}", method = RequestMethod.DELETE)
    @ResponseBody
    public ReturnMsg deletePermitsMsg(@PathVariable("userid") String userid, @RequestBody PermitsDto permitsDto) {
        ReturnMsg returnMsg;
        try {
            returnMsg = permitsService.deletePermitsMsg(userid, permitsDto);
        } catch (Exception e) {
            if (e.getMessage().contains("使用中")) {
                returnMsg = new ReturnMsg(ReturnMsg.FAIL, e.getMessage());
            } else {
                returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "許可證刪除失敗...");
            }
            logger.info("/app/permits/deletePermitsMsg/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/permits/updatePermitsMsg/{userid}", method = RequestMethod.PUT)
    @ResponseBody
    public ReturnMsg updatePermitsMsg(@PathVariable("userid") String userid, @RequestBody PermitsDto permitsDto) {
        ReturnMsg returnMsg = new ReturnMsg();
        if (permitsDto.getTypeid() == null) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "許可證類型id為空");
            return returnMsg;
        }
        List<PermitsImagePO> permitsImageList = new ArrayList<>();
        List<Integer> notDelIds = new ArrayList<>();
        boolean status = true;
        try {
            //保存文件
            List filesDataArr = permitsDto.getFilesDataArr();
            if (filesDataArr != null && filesDataArr.size() > 0) {
                int t = 1;
                for (Object o : filesDataArr) {
                    Map map = (HashMap) o;
                    if (!StringUtils.isEmpty(map.get("base64").toString()) && !StringUtils.isEmpty(map.get("type").toString())) {
                        String fileAllPath = FilesUtils.base64StringToFile(map.get("base64").toString(), filePath, map.get("type").toString());
                        if ("".equals(fileAllPath)) {
                            status = false;
                            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "圖片上傳失敗...");
                        } else {
                            PermitsImagePO permitsImage = new PermitsImagePO();
                            permitsImage.setPermitId(permitsDto.getPermitid());
                            permitsImage.setImageIndex(t);
                            permitsImage.setImageType(map.get("type").toString());
                            permitsImage.setImageDir(fileAllPath.substring(0, fileAllPath.lastIndexOf("/") + 1));
                            permitsImage.setName(map.get("name").toString());
                            if ("pdf".equals(map.get("type"))) {
                                permitsImage.setImageName(fileAllPath.substring(fileAllPath.lastIndexOf("/") + 1));
                            } else {
                                String[] split = fileAllPath.split("\\.");
                                String thumbnail = split[0] + THUMBNAIL;
                                String thumbnailPath = thumbnail + "." + split[1];
                                //生成缩略图
                                Thumbnails.of(filePath + fileAllPath).scale(0.2f).toFile(filePath + thumbnailPath);
                                permitsImage.setImageName(thumbnailPath.substring(thumbnailPath.lastIndexOf("/") + 1));
                            }
                            permitsImageList.add(permitsImage);
                            t++;
                        }
                    } else if (!StringUtils.isEmpty(map.get("id"))) {
                        notDelIds.add(Integer.parseInt(map.get("id").toString()));
                        PermitsImagePO permitsImage = new PermitsImagePO();
                        permitsImage.setId(Integer.parseInt(map.get("id").toString()));
                        permitsImage.setImageIndex(t);
                        permitsImageList.add(permitsImage);
                        t++;
                    }
                }
            }
            if (status) {
                returnMsg = permitsService.updatePermitsMsg(userid, permitsDto, permitsImageList, notDelIds);
            }
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "許可證修改失敗...");
            logger.info("/app/permits/updatePermitsMsg/{userid} 异常");
            e.printStackTrace();
        }
        if (returnMsg.getCode() == ReturnMsg.FAIL) {
            for (PermitsImagePO permitsImage : permitsImageList) {
                if (permitsImage.getImageName() != null) {
                    FilesUtils.deleteFile(permitsImage.getImageName(), filePath + permitsImage.getImageDir());
                }
            }
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/permits/getPermitsMsg", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getPermitsMsg(HttpServletRequest request) {
        ReturnMsg returnMsg;
        try {
            returnMsg = permitsService.getPermitsMsg(request);
        } catch (Exception e) {
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "未獲取到相關數據...");
            logger.info("/app/permits/getPermitsMsg/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/permits/getPermitType", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getToolType(HttpServletRequest request) {
        ReturnMsg returnMsg;
        try {
            returnMsg = permitsService.getPermitType(request);
        } catch (Exception e) {
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "未獲取到相關數據...");
            logger.info("/app/permits/getPermitType/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/permits/getAllNum/{userid}", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getAllNum(@PathVariable("userid") String userid) {
        ReturnMsg returnMsg;
        try {
            returnMsg = permitsService.getAllNum(userid);
        } catch (Exception e) {
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "未獲取到相關數據...");
            logger.info("/app/permits/getAllNum/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }
}
