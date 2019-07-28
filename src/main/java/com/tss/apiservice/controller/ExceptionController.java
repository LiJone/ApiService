package com.tss.apiservice.controller;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.dto.PermitsDto;
import com.tss.apiservice.service.ExceptionSerive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ExceptionController {
    private static Logger logger = LoggerFactory.getLogger(ExceptionController.class);

    @Autowired
    ExceptionSerive exceptionSerive;

    @RequestMapping(value = "/app/exception/getExceptionLog", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getExceptionLog(HttpServletRequest request) {
        ReturnMsg<Object> returnMsg = null;
        try {
            returnMsg = exceptionSerive.getExceptionLog(request);
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "異常日誌...");
            logger.info("/app/exception/getExceptionLog 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }
}
