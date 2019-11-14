package com.tss.apiservice.controller;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.common.utils.FilesUtils;
import com.tss.apiservice.dto.StaffsDto;
import com.tss.apiservice.po.StaffsImagePO;
import com.tss.apiservice.po.ToolsImagePO;
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

        List<StaffsImagePO> staffsImageList = new ArrayList<>();
        try {
            //上传头像
            boolean fileUploadStatus = true;
            List<Map<String, String>> headImageData = staffsDto.getHeadImageData();
            Map<String, String> map;
            if (headImageData != null && headImageData.size() > 0) {
                int t = 1;
                for (Map<String, String> headImageDatum : headImageData) {
                    map = headImageDatum;
                    if (!StringUtils.isEmpty(map.get("base64")) && !StringUtils.isEmpty(map.get("type"))) {
                        String fileAllPath = FilesUtils.base64StringToFile(map.get("base64"), filePath, map.get("type"));
                        if ("".equals(fileAllPath)) {
                            fileUploadStatus = false;
                            returnMsg.setMsgbox("頭像上傳失敗...");
                            break;
                        } else {
                            StaffsImagePO staffsImage = new StaffsImagePO();
                            staffsImage.setStaffId(staffsDto.getStaffid());
                            staffsImage.setImageIndex(t);
                            staffsImage.setImageType(map.get("type"));
                            staffsImage.setImageDir(fileAllPath.substring(0, fileAllPath.lastIndexOf("/") + 1));
                            if ("pdf".equals(map.get("type"))) {
                                staffsImage.setImageName(fileAllPath.substring(fileAllPath.lastIndexOf("/") + 1));
                            } else {
                                String[] split = fileAllPath.split("\\.");
                                String thumbnail = split[0] + THUMBNAIL;
                                String thumbnailPath = thumbnail + "." + split[1];
                                //生成缩略图
                                Thumbnails.of(filePath + fileAllPath).scale(0.2f).toFile(filePath + thumbnailPath);
                                staffsImage.setImageName(thumbnailPath.substring(thumbnailPath.lastIndexOf("/") + 1));
                            }
                            staffsImageList.add(staffsImage);
                            t++;
                        }
                    }
                }
            }
            //添加员工信息
            if (fileUploadStatus) {
                returnMsg = staffsService.addStaffsMsg(userid, staffsDto, staffsImageList);
            }
        } catch (Exception e) {
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "員工添加失敗...");
            logger.info("/app/staffs/addStaffsMsg/{userid} 异常");
            e.printStackTrace();
        }
        if (returnMsg.getCode() == ReturnMsg.FAIL) {
            for (StaffsImagePO staffsImage : staffsImageList) {
                FilesUtils.deleteFile(staffsImage.getImageName(), filePath + staffsImage.getImageDir());
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

        List<StaffsImagePO> staffsImageList = new ArrayList<>();
        List<Integer> notDelIds = new ArrayList<>();
        try {
            //上传头像
            boolean fileUploadStatus = true;
            List<Map<String, String>> headImageData = staffsDto.getHeadImageData();
            Map<String, String> map;
            if (headImageData != null && headImageData.size() > 0) {
                int t = 1;
                for (Map<String, String> headImageDatum : headImageData) {
                    map = headImageDatum;
                    if (!StringUtils.isEmpty(map.get("base64")) && !StringUtils.isEmpty(map.get("type"))) {
                        String fileAllPath = FilesUtils.base64StringToFile(map.get("base64"), filePath, map.get("type"));
                        if ("".equals(fileAllPath)) {
                            fileUploadStatus = false;
                            returnMsg.setMsgbox("頭像上傳失敗...");
                            break;
                        } else {
                            StaffsImagePO staffsImage = new StaffsImagePO();
                            staffsImage.setStaffId(staffsDto.getStaffid());
                            staffsImage.setImageIndex(t);
                            staffsImage.setImageType(map.get("type"));
                            staffsImage.setImageDir(fileAllPath.substring(0, fileAllPath.lastIndexOf("/") + 1));
                            if ("pdf".equals(map.get("type"))) {
                                staffsImage.setImageName(fileAllPath.substring(fileAllPath.lastIndexOf("/") + 1));
                            } else {
                                String[] split = fileAllPath.split("\\.");
                                String thumbnail = split[0] + THUMBNAIL;
                                String thumbnailPath = thumbnail + "." + split[1];
                                //生成缩略图
                                Thumbnails.of(filePath + fileAllPath).scale(0.2f).toFile(filePath + thumbnailPath);
                                staffsImage.setImageName(thumbnailPath.substring(thumbnailPath.lastIndexOf("/") + 1));
                            }
                            staffsImageList.add(staffsImage);
                            t++;
                        }
                    } else if (!StringUtils.isEmpty(map.get("id"))) {
                        StaffsImagePO staffsImage = new StaffsImagePO();
                        staffsImage.setId(Integer.parseInt(map.get("id")));
                        staffsImage.setImageIndex(t);
                        staffsImageList.add(staffsImage);
                        t++;
                        notDelIds.add(Integer.parseInt(map.get("id")));
                    }
                }
            }
            if (fileUploadStatus) {
                returnMsg = staffsService.updateStaffsMsg(userid, staffsDto, staffsImageList, notDelIds);
            }
        } catch (Exception e) {
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "員工修改失敗...");
            logger.info("/app/staffs/updateStaffsMsg/{userid} 异常");
            e.printStackTrace();
        }
        if (returnMsg.getCode() == ReturnMsg.FAIL) {
            for (StaffsImagePO staffsImage : staffsImageList) {
                if (staffsImage.getImageName() != null) {
                    FilesUtils.deleteFile(staffsImage.getImageName(), filePath + staffsImage.getImageDir());
                }
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