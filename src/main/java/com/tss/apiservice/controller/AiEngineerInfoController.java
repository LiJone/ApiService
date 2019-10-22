package com.tss.apiservice.controller;

import com.alibaba.fastjson.JSON;
import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.dto.JobDto;
import com.tss.apiservice.form.*;
import com.tss.apiservice.service.AiEngineerInfoService;
import org.apache.ibatis.javassist.expr.NewArray;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 壮Jone
 */
@Controller
public class AiEngineerInfoController {
    private static Logger logger = LoggerFactory.getLogger(AiEngineerInfoController.class);

    @Autowired
    private AiEngineerInfoService aiEngineerInfoService;

    @RequestMapping(value = "/app/engineer/addEngineerMsg/{userid}", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMsg addEngineerMsg(@PathVariable("userid") String userid, @RequestBody AiEngineerInfoForm aiEngineerInfoForm) {
        ReturnMsg returnMsg = null;
        try {
            returnMsg = aiEngineerInfoService.addEngineerMsg(userid, aiEngineerInfoForm);
        } catch (Exception e) {
            if (e.getMessage().contains("已綁定") || e.getMessage().contains("已存在")) {
                returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "智能工程錄入異常..." + e.getMessage());
            } else {
                returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "智能工程錄入異常...");
            }
            logger.info("/app/engineer/addEngineerMsg/{userid} 异常{}" + e.getMessage(), e);
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/engineer/getEngineerList", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getEngineerList(HttpServletRequest request) {
        ReturnMsg returnMsg = null;
        try {
            returnMsg = aiEngineerInfoService.getEngineerList(request);
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "智能工程列表異常...");
            logger.info("/app/engineer/getEngineerList 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/engineer/updateEngineerMsg/{userid}", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMsg updateEngineerMsg(@PathVariable("userid") String userid, @RequestBody AiEngineerInfoForm aiEngineerInfoForm) {
        ReturnMsg returnMsg = null;
        try {
            returnMsg = aiEngineerInfoService.updateEngineerMsg(userid, aiEngineerInfoForm);
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "智能工程修改異常...");
            logger.info("/app/engineer/updateEngineerMsg/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/engineer/deleteEngineerMsg/{userid}", method = RequestMethod.DELETE)
    @ResponseBody
    public ReturnMsg deleteEngineerMsg(@PathVariable("userid") String userid, @RequestBody AiEngineerInfoForm aiEngineerInfoForm) {
        ReturnMsg returnMsg = null;
        try {
            returnMsg = aiEngineerInfoService.deleteEngineerMsg(userid, aiEngineerInfoForm);
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "智能工程刪除異常...");
            logger.info("/app/engineer/deleteEngineerMsg/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

}
