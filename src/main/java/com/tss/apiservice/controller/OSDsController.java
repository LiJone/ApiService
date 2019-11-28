package com.tss.apiservice.controller;

import com.tss.apiservice.annotation.RequiredPermission;
import com.tss.apiservice.common.PermissionConstants;
import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.dto.OSDsDto;
import com.tss.apiservice.service.OSDInfosService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
//@RequiredPermission(PermissionConstants.ADMIN_PERSONG_COMPANY_ADMIN)
public class OSDsController {
    private static Logger logger = LoggerFactory.getLogger(OSDsController.class);

    @Autowired
    OSDInfosService osdInfosService;

    @RequestMapping(value = "/app/OSDs/addOSDsMsg/{userid}", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMsg addOSDsMsg(@PathVariable("userid") String userid, @RequestBody OSDsDto osDsDto) {
        ReturnMsg<Object> returnMsg = null;
        try {
            returnMsg = osdInfosService.addOSDinfos(userid, osDsDto);
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "OSD盒子錄入失敗...");
            logger.info("/app/tools/addOSDsMsg/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/OSDs/updateOSDsMsg/{userid}", method = RequestMethod.PUT)
    @ResponseBody
    public ReturnMsg updateOSDsMsg(@PathVariable("userid") String userid, @RequestBody OSDsDto osDsDto) {
        ReturnMsg<Object> returnMsg = null;
        try {
            returnMsg = osdInfosService.updateOSDsMsg(userid, osDsDto);
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "OSD盒子修改失敗...");
            logger.info("/app/OSDs/updateOSDsMsg/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }
    @RequestMapping(value = "/app/OSDs/deleteOSDsMsg/{userid}", method = RequestMethod.DELETE)
    @ResponseBody
    public ReturnMsg deleteOSDsMsg(@PathVariable("userid") String userid, @RequestBody OSDsDto osDsDto) {
        ReturnMsg<Object> returnMsg = null;
        try {
            returnMsg = osdInfosService.deleteOSDsMsg(userid, osDsDto);
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "OSD盒子刪除失敗...");
            logger.info("/app/OSDs/updateOSDsMsg/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }
    @RequestMapping(value = "/app/OSDs/getOSDsMsgList", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getOSDsMsgList(HttpServletRequest request) {
        ReturnMsg<Object> returnMsg = null;
        try {
            returnMsg = osdInfosService.getOSDsMsgList(request);
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "未獲取到相關數據...");
            logger.info("/app/OSDs/updateOSDsMsg/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/OSDs/getAllNum/{userid}", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getAllNum(@PathVariable("userid") String userid) {
        ReturnMsg returnMsg;
        try {
            returnMsg = osdInfosService.getAllNum(userid);
        } catch (Exception e) {
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "未獲取到相關數據...");
            logger.info("/app/OSDs/getAllNum/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/OSDs/getAllName/{userid}", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getAllName(@PathVariable("userid") String userid) {
        ReturnMsg returnMsg;
        try {
            returnMsg = osdInfosService.getAllName(userid);
        } catch (Exception e) {
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "未獲取到相關數據...");
            logger.info("/app/OSDs/getAllName/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }
}
