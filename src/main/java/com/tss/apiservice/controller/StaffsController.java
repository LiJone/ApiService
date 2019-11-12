package com.tss.apiservice.controller;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.common.utils.FilesUtils;
import com.tss.apiservice.dto.StaffsDto;
import com.tss.apiservice.service.StaffsService;
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

/**
 * @author 壮Jone
 */
@Controller
public class StaffsController {
    private static Logger logger = LoggerFactory.getLogger(StaffsController.class);

    private static final String THUMBNAIL = "_thumbnail";

    @Autowired
    StaffsService staffsService;

    @Value("${filePath}")
    private String filePath;

    @RequestMapping(value = "/app/staffs/addStaffsMsg/{userid}", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMsg addStaffsMsg(@PathVariable("userid") String userid, @RequestBody StaffsDto staffsDto) {
        ReturnMsg returnMsg = new ReturnMsg();
        String headImagePath;
        StringBuilder headNameSb = new StringBuilder();
        String thumbnailPath;
        StringBuilder thumbnailHeadNameSb = new StringBuilder();
        String headPath = null;
        ArrayList<Object> filesList = new ArrayList<>();
        try {
            //上传头像
            boolean fileUploadStatus = true;
            List<Map<String, String>> headImageData = staffsDto.getHeadImageData();
            Map<String, String> map;
            if (headImageData != null && headImageData.size() > 0) {
                for (Map<String, String> headImageDatum : headImageData) {
                    map = headImageDatum;
                    if (!StringUtils.isEmpty(map.get("base64")) && !StringUtils.isEmpty(map.get("type"))) {
                        headImagePath = FilesUtils.base64StringToFile(map.get("base64"), filePath, map.get("type"));
                        String[] split = headImagePath.split("\\.");
                        String thumbnail = split[0] + THUMBNAIL;
                        thumbnailPath = thumbnail + "." + split[1];
                        //生成缩略图
                        Thumbnails.of(filePath + headImagePath).scale(0.2f).toFile(filePath + thumbnailPath);
                        if ("".equals(headImagePath)) {
                            fileUploadStatus = false;
                            returnMsg.setMsgbox("頭像上傳失敗...");
                        } else {
                            headNameSb.append(headImagePath.substring(headImagePath.lastIndexOf("/") + 1)).append(";");
                            headPath = headImagePath.substring(0, headImagePath.lastIndexOf("/") + 1);
                            thumbnailHeadNameSb.append(thumbnailPath.substring(thumbnailPath.lastIndexOf("/") + 1)).append(";");
                        }
                    }
                }
            }

            //上传绿卡
            List<Map<String, String>> carDataArr = staffsDto.getCarDataArr();
            String fileAllPath;
            HashMap<String, String> hashMap = new HashMap<>();
            if (carDataArr != null && carDataArr.size() > 0) {
                for (Map<String, String> map1 : carDataArr) {
                    map = map1;
                    if (!StringUtils.isEmpty(map.get("base64")) && !StringUtils.isEmpty(map.get("type"))) {
                        fileAllPath = FilesUtils.base64StringToFile(map.get("base64"), filePath, map.get("type"));
                        if (fileAllPath == null) {
                            fileUploadStatus = false;
                            returnMsg.setMsgbox("員工證件上傳失敗...");
                            break;
                        } else {
                            hashMap.put(map.get("certid").trim(), fileAllPath);
                            filesList.add(fileAllPath);
                        }
                    }
                }
            }
            //添加员工信息
            if (fileUploadStatus) {
                returnMsg = staffsService.addStaffsMsg(userid, staffsDto, hashMap, thumbnailHeadNameSb.toString(), headPath);
            }
        } catch (Exception e) {
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "員工添加失敗...");
            logger.info("/app/staffs/addStaffsMsg/{userid} 异常");
            e.printStackTrace();
        }
        if (returnMsg.getCode() == ReturnMsg.FAIL) {
            String filePathTmp = headPath;
            String fileNameTmp = headNameSb.toString();
            FilesUtils.deleteFile(fileNameTmp, filePath + filePathTmp);
            FilesUtils.deleteFile(thumbnailHeadNameSb.toString(), filePath + filePathTmp);
            String files;
            for (Object o : filesList) {
                files = o.toString();
                filePathTmp = files.substring(0, files.lastIndexOf("/") + 1);
                fileNameTmp = files.substring(files.lastIndexOf("/") + 1);
                FilesUtils.deleteFile(fileNameTmp, filePath + filePathTmp);
            }
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/staffs/getStaffsMsgList", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getStaffsMsgList(HttpServletRequest request) {
        ReturnMsg returnMsg;
        try {
            returnMsg = staffsService.getStaffsMsgList(request);
        } catch (Exception e) {
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "未獲取到相關數據...");
            logger.info("/app/staffs/getStaffsMsgList/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/staffs/deleteStaffsMsg/{userid}", method = RequestMethod.DELETE)
    @ResponseBody
    public ReturnMsg deleteStaffsMsg(@PathVariable("userid") String userid, @RequestBody StaffsDto staffsDto) {
        ReturnMsg returnMsg;
        try {
            returnMsg = staffsService.deleteStaffsMsg(userid, staffsDto);
        } catch (Exception e) {
            if (e.getMessage().contains("使用中")) {
                returnMsg = new ReturnMsg(ReturnMsg.FAIL, e.getMessage());
            } else {
                returnMsg = new ReturnMsg(ReturnMsg.FAIL, "員工刪除失敗...");
            }
            logger.info("/app/staffs/deleteStaffsMsg/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/staffs/updateStaffsMsg/{userid}", method = RequestMethod.PUT)
    @ResponseBody
    public ReturnMsg updateStaffsMsg(@PathVariable("userid") String userid, @RequestBody StaffsDto staffsDto) {
        ReturnMsg returnMsg = new ReturnMsg();
        String headImagePath;
        String thumbnailPath;
        StringBuilder headNameSb = new StringBuilder();
        String headPath = null;
        StringBuilder thumbnailHeadNameSb = new StringBuilder();
        ArrayList<Object> filesList = new ArrayList<>();
        try {
            //上传头像
            boolean fileUploadStatus = true;
            List<Map<String, String>> headImageData = staffsDto.getHeadImageData();
            Map<String, String> map;
            if (headImageData != null && headImageData.size() > 0) {
                for (Map<String, String> headImageDatum : headImageData) {
                    map = headImageDatum;
                    if (!StringUtils.isEmpty(map.get("base64")) && !StringUtils.isEmpty(map.get("type"))) {
                        headImagePath = FilesUtils.base64StringToFile(map.get("base64"), filePath, map.get("type"));
                        String[] split = headImagePath.split("\\.");
                        String thumbnail = split[0] + THUMBNAIL;
                        thumbnailPath = thumbnail + "." + split[1];
                        //生成缩略图
                        Thumbnails.of(filePath + headImagePath).scale(0.2f).toFile(filePath + thumbnailPath);
                        if ("".equals(headImagePath)) {
                            fileUploadStatus = false;
                            returnMsg.setMsgbox("頭像上傳失敗...");
                        } else {
                            headNameSb.append(headImagePath.substring(headImagePath.lastIndexOf("/") + 1)).append(";");
                            headPath = headImagePath.substring(0, headImagePath.lastIndexOf("/") + 1);
                            thumbnailHeadNameSb.append(thumbnailPath.substring(thumbnailPath.lastIndexOf("/") + 1)).append(";");
                        }
                    }
                }
            }

            //上传绿卡
            List<Map<String, String>> carDataArr = staffsDto.getCarDataArr();
            String fileAllPath;
            HashMap<String, String> hashMap = new HashMap<>();
            if (carDataArr != null && carDataArr.size() > 0) {
                for (Map<String, String> stringStringMap : carDataArr) {
                    map = stringStringMap;
                    if (!StringUtils.isEmpty(map.get("base64")) && !StringUtils.isEmpty(map.get("type"))) {
                        fileAllPath = FilesUtils.base64StringToFile(map.get("base64"), filePath, map.get("type"));
                        if (fileAllPath == null) {
                            fileUploadStatus = false;
                            returnMsg.setMsgbox("員工證件上傳失敗...");
                            break;
                        } else {
                            hashMap.put(map.get("certid").trim(), fileAllPath);
                            filesList.add(fileAllPath);
                        }
                    }
                }
            }
            if (fileUploadStatus) {
                returnMsg = staffsService.updateStaffsMsg(userid, staffsDto, hashMap, thumbnailHeadNameSb.toString(), headPath);
            }
        } catch (Exception e) {
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "員工修改失敗...");
            logger.info("/app/staffs/updateStaffsMsg/{userid} 异常");
            e.printStackTrace();
        }
        if (returnMsg.getCode() == ReturnMsg.FAIL) {
            String filePathTmp = headPath;
            String fileNameTmp = headNameSb.toString();
            FilesUtils.deleteFile(fileNameTmp, filePath + filePathTmp);
            FilesUtils.deleteFile(thumbnailHeadNameSb.toString(), filePath + filePathTmp);
            String files;
            for (Object o : filesList) {
                files = o.toString();
                filePathTmp = files.substring(0, files.lastIndexOf("/") + 1);
                fileNameTmp = files.substring(files.lastIndexOf("/") + 1);
                FilesUtils.deleteFile(fileNameTmp, filePath + filePathTmp);
            }
        }
        return returnMsg;
    }


    @RequestMapping(value = "/app/staffs/getStaffsPermitsType", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getStaffsPermitsType(HttpServletRequest request) {
        ReturnMsg returnMsg;
        try {
            returnMsg = staffsService.getStaffsPermitsType(request);
        } catch (Exception e) {
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "未獲取到相關數據...");
            logger.info("/app/staffs/getStaffsPermitsType/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/staffs/getStaffsMsg", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getStaffsMsg(HttpServletRequest request) {
        ReturnMsg returnMsg;
        try {
            returnMsg = staffsService.getStaffsMsg(request);
        } catch (Exception e) {
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "未獲取到相關數據...");
            logger.info("/app/staffs/getStaffsMsg/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }


    @RequestMapping(value = "/app/staffs/getCertType" , method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getCertType(HttpServletRequest request){
        ReturnMsg returnMsg;
        try {
            returnMsg = staffsService.getCertType(request);
        } catch (Exception e) {
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "未獲取到相關數據...");
            logger.info("/app/staffs/getCerType/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/staffs/getCpStaffs", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getCpStaffs(HttpServletRequest request) {
        ReturnMsg returnMsg;
        try {
            returnMsg = staffsService.getCpStaffs(request);
        } catch (Exception e) {
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "未獲取到相關數據...");
            logger.info("/app/staffs/getCpStaffs/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/staffs/getAllNum/{userid}", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getAllNum(@PathVariable("userid") String userid) {
        ReturnMsg returnMsg;
        try {
            returnMsg = staffsService.getAllNum(userid);
        } catch (Exception e) {
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "未獲取到相關數據...");
            logger.info("/app/staffs/getAllNum/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }
}