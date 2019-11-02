package com.tss.apiservice.controller;


import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.dto.JobDto;
import com.tss.apiservice.form.*;
import com.tss.apiservice.service.AiEngineerInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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
    public ReturnMsg addEngineerMsg(@PathVariable("userid") String userid, @Valid @RequestBody AiEngineerInfoForm aiEngineerInfoForm, Errors errors) {
        ReturnMsg returnMsg;
        if (errors.hasErrors()) {
            List<ObjectError> allErrors = errors.getAllErrors();
            StringBuilder sb = new StringBuilder();
            for (ObjectError error : allErrors) {
                sb.append(error.getDefaultMessage()).append("...");
            }
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, sb.toString());
            return returnMsg;
        }
        try {
            returnMsg = aiEngineerInfoService.addEngineerMsg(userid, aiEngineerInfoForm);
        } catch (NullPointerException n) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "數據庫參數存在空值");
        } catch (Exception e) {
            if (e.getMessage().contains("已綁定") || e.getMessage().contains("已存在")) {
                returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "智能工程錄入失敗..." + e.getMessage());
            } else {
                returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "智能工程錄入失敗...");
            }
            logger.info("/app/engineer/addEngineerMsg/{userid} 异常{}" + e.getMessage(), e);
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/engineer/getEngineerList", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getEngineerList(HttpServletRequest request) {
        ReturnMsg returnMsg;
        try {
            returnMsg = aiEngineerInfoService.getEngineerList(request);
        } catch (NullPointerException n) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "數據庫參數存在空值");
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "未獲取到相關數據...");
            logger.info("/app/engineer/getEngineerList 异常" + e.getMessage(), e);
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/engineer/updateEngineerMsg/{userid}", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMsg updateEngineerMsg(@Valid @RequestBody AiEngineerInfoForm aiEngineerInfoForm, Errors errors) {
        ReturnMsg returnMsg;
        if (errors.hasErrors()) {
            List<ObjectError> allErrors = errors.getAllErrors();
            StringBuilder sb = new StringBuilder();
            for (ObjectError error : allErrors) {
                sb.append(error.getDefaultMessage()).append("...");
            }
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, sb.toString());
            return returnMsg;
        }
        try {
            returnMsg = aiEngineerInfoService.updateEngineerMsg(aiEngineerInfoForm);

        } catch (NullPointerException n) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "數據庫參數存在空值");
        } catch (Exception e) {
            if (e.getMessage().contains("已綁定") || e.getMessage().contains("已存在")) {
                returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "智能工程修改失敗..." + e.getMessage());
            } else {
                returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "智能工程修改失敗...");
            }
            logger.info("/app/engineer/updateEngineerMsg/{userid} 异常" + e.getMessage(), e);
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/engineer/deleteEngineerMsg/{jobNum}", method = RequestMethod.DELETE)
    @ResponseBody
    public ReturnMsg deleteEngineerMsg(@PathVariable("jobNum") String jobNum) {
        ReturnMsg returnMsg;
        try {
            returnMsg = aiEngineerInfoService.deleteEngineerMsg(jobNum);
        } catch (NullPointerException n) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "數據庫參數存在空值");
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "智能工程刪除失敗...");
            logger.info("/app/engineer/deleteEngineerMsg/{userid} 异常" + e.getMessage(), e);
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/engineer/setEngineerRunStatus/{jobNum}", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg setEngineerRunStatus(@PathVariable("jobNum") String jobNum) {
        ReturnMsg returnMsg = null;
        try {
            returnMsg = aiEngineerInfoService.setJobRunStatus(jobNum);
        } catch (NullPointerException n) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "數據庫參數存在空值");
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "智能工程狀態修改失敗...");
            logger.info("/app/engineer/setEngineerRunStatus /{jobNum} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

}
