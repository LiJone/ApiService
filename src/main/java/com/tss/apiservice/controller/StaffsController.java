package com.tss.apiservice.controller;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.common.utils.FilesUtils;
import com.tss.apiservice.dto.StaffsDto;
import com.tss.apiservice.service.StaffsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class StaffsController {
    private static Logger logger = LoggerFactory.getLogger(StaffsController.class);

    @Autowired
    StaffsService staffsService;

    @Value("${filePath}")
    private String filePath;

    @RequestMapping(value = "/app/staffs/addStaffsMsg/{userid}", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMsg addStaffsMsg(@PathVariable("userid") String userid, @RequestBody StaffsDto staffsDto) {
        ReturnMsg returnMsg = null;
        String headImagePath;
        StringBuilder headNameSb = new StringBuilder();
        String headPath = null;
        ArrayList<Object> filesList = new ArrayList<>();
        try {
            //上传头像
            boolean fileUploadStatus = true;
            List<Map<String, String>> headImageData = staffsDto.getHeadImageData();
            Map<String, String> map;
            if (headImageData != null && headImageData.size() > 0) {
                for (int i = 0; i < headImageData.size(); i++) {
                    map = headImageData.get(i);
                    if (StringUtils.isEmpty(map.get("base64")) || StringUtils.isEmpty(map.get("type"))) {
                        continue;
                    } else {
                        headImagePath = FilesUtils.base64StringToFile(map.get("base64"), filePath, map.get("type"));
                        if (headImagePath == null) {
                            fileUploadStatus = false;
                            returnMsg.setMsgbox("頭像上傳失敗...");
                        } else {
                            headNameSb.append(headImagePath.substring(headImagePath.lastIndexOf("/") + 1)).append(";");
                            headPath = headImagePath.substring(0, headImagePath.lastIndexOf("/") + 1);
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
                returnMsg = staffsService.addStaffsMsg(userid, staffsDto, hashMap, headNameSb.toString(), headPath);
            }
        } catch (Exception e) {
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "員工添加異常...");
            logger.info("/app/staffs/addStaffsMsg/{userid} 异常");
            e.printStackTrace();
        }
        if (returnMsg.getCode() == ReturnMsg.FAIL) {
            String filePathTmp = headPath;
            String fileNameTmp = headNameSb.toString();
            FilesUtils.deleteFile(fileNameTmp, filePath + filePathTmp);

            String files = null;
            for (int i = 0; i < filesList.size(); i++) {
                files = filesList.get(i).toString();
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
        ReturnMsg returnMsg = null;
        try {
            returnMsg = staffsService.getStaffsMsgList(request);
        } catch (Exception e) {
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "獲取員工列表異常...");
            logger.info("/app/staffs/getStaffsMsgList/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/staffs/deleteStaffsMsg/{userid}", method = RequestMethod.DELETE)
    @ResponseBody
    public ReturnMsg deleteStaffsMsg(@PathVariable("userid") String userid, @RequestBody StaffsDto staffsDto) {
        ReturnMsg returnMsg = null;
        try {
            returnMsg = staffsService.deleteStaffsMsg(userid, staffsDto);
        } catch (Exception e) {
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "員工刪除異常...");
            logger.info("/app/staffs/deleteStaffsMsg/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/staffs/updateStaffsMsg/{userid}", method = RequestMethod.PUT)
    @ResponseBody
    public ReturnMsg updateStaffsMsg(@PathVariable("userid") String userid, @RequestBody StaffsDto staffsDto) {
        ReturnMsg returnMsg = null;
        String headImagePath = null;
        StringBuffer headNameSb = new StringBuffer();
        String headPath = null;
        ArrayList<Object> filesList = new ArrayList<>();
        try {
            //上传头像
            boolean fileUploadStatus = true;
            List<Map<String, String>> headImageData = staffsDto.getHeadImageData();
            Map<String, String> map = null;
            if (headImageData != null && headImageData.size() > 0) {
                for (int i = 0; i < headImageData.size(); i++) {
                    map = headImageData.get(i);
                    if (StringUtils.isEmpty(map.get("base64").toString()) || StringUtils.isEmpty(map.get("type").toString())) {
                        continue;
                    } else {
                        headImagePath = FilesUtils.base64StringToFile(map.get("base64").toString(), filePath, map.get("type").toString());
                        if (headImagePath == null) {
                            fileUploadStatus = false;
                            returnMsg.setMsgbox("頭像上傳失敗...");
                        } else {
                            headNameSb.append(headImagePath.substring(headImagePath.lastIndexOf("/") + 1, headImagePath.length())).append(";");
                            headPath = headImagePath.substring(0, headImagePath.lastIndexOf("/") + 1);
                        }
                    }
                }
            }

            //上传绿卡
            List<Map<String, String>> carDataArr = staffsDto.getCarDataArr();
            String fileAllPath = null;
            HashMap<String, String> hashMap = new HashMap<>();
            if (carDataArr != null && carDataArr.size() > 0) {
                for (int i = 0; i < carDataArr.size(); i++) {
                    map = (HashMap) carDataArr.get(i);
                    if (StringUtils.isEmpty(map.get("base64").toString()) || StringUtils.isEmpty(map.get("type").toString())) {

                    } else {
                        fileAllPath = FilesUtils.base64StringToFile(map.get("base64").toString(), filePath, map.get("type").toString());
                        if (fileAllPath == null) {
                            fileUploadStatus = false;
                            returnMsg.setMsgbox("員工證件上傳失敗...");
                            break;
                        } else {
                            hashMap.put(map.get("certid").toString().trim(), fileAllPath);
                            filesList.add(fileAllPath);
                        }
                    }
                }
            }
            if (fileUploadStatus) {
                returnMsg = staffsService.updateStaffsMsg(userid, staffsDto, hashMap, headNameSb.toString(), headPath);
            }
        } catch (Exception e) {
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "員工修改異常...");
            logger.info("/app/staffs/updateStaffsMsg/{userid} 异常");
            e.printStackTrace();
        }
        if (returnMsg.getCode() == ReturnMsg.FAIL) {
            String filePathTmp = headPath;
            String fileNameTmp = headNameSb.toString();
            FilesUtils.deleteFile(fileNameTmp, filePath + filePathTmp);

            String files = null;
            for (int i = 0; i < filesList.size(); i++) {
                files = filesList.get(i).toString();
                filePathTmp = files.substring(0, files.lastIndexOf("/") + 1);
                fileNameTmp = files.substring(files.lastIndexOf("/") + 1, files.length());
                FilesUtils.deleteFile(fileNameTmp, filePath + filePathTmp);
            }
        }
        return returnMsg;
    }


    @RequestMapping(value = "/app/staffs/getStaffsPermitsType", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getStaffsPermitsType(HttpServletRequest request) {
        ReturnMsg returnMsg = null;
        try {
            returnMsg = staffsService.getStaffsPermitsType(request);
        } catch (Exception e) {
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "獲取員工異常...");
            logger.info("/app/staffs/getStaffsPermitsType/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/staffs/getStaffsMsg", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getStaffsMsg(HttpServletRequest request) {
        ReturnMsg returnMsg = null;
        try {
            returnMsg = staffsService.getStaffsMsg(request);
        } catch (Exception e) {
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "獲取員工異常...");
            logger.info("/app/staffs/getStaffsMsg/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }


    @RequestMapping(value = "/app/staffs/getCertType" , method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getCertType(HttpServletRequest request){
        ReturnMsg returnMsg = null;
        try {
            returnMsg = staffsService.getCertType(request);
        } catch (Exception e) {
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "獲取證件類型異常...");
            logger.info("/app/staffs/getCerType/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }
}