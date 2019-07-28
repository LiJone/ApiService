package com.tss.apiservice.controller;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.dto.AttendSetDto;
import com.tss.apiservice.service.OrganAttendSetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 壮Jone
 */
@Controller
public class OrganAttendSetController {
    private static Logger logger = LoggerFactory.getLogger(OrganAttendSetController.class);

    @Autowired
    private OrganAttendSetService organAttendSetService;

    @RequestMapping(value = "/app/organ/getOrganSetting", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getUserSetting(HttpServletRequest request) {
        ReturnMsg returnMsg = null;
        try {
            returnMsg = organAttendSetService.getOrganSetting(request);
        } catch (Exception e) {
            logger.info("/app/organ/getOrganSetting 异常");
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "獲取機構設置異常...");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/organ/setOrganSetting/{userid}", method = RequestMethod.PUT)
    @ResponseBody
    public ReturnMsg setUserSetting(@PathVariable("userid") String userid, @RequestBody AttendSetDto attendSetDto) {
        ReturnMsg returnMsg = null;
        try {
            returnMsg = organAttendSetService.setOrganSetting(userid,attendSetDto);
        } catch (Exception e) {
            logger.info("/app/organ/setOrganSetting 异常");
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "設置機構設置異常...");
            e.printStackTrace();
        }
        return returnMsg;
    }
}
