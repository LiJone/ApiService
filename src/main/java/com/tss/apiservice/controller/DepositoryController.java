package com.tss.apiservice.controller;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.service.DepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 壮Jone
 */
@Controller
@RequestMapping("/app/depository")
public class DepositoryController {
    private static Logger logger = LoggerFactory.getLogger(DepositoryController.class);

    @Autowired
    private DepositoryService depositoryService;

    @RequestMapping(value = "/getDepositoryMsgList", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getDepositoryMsgList(HttpServletRequest request) {
        ReturnMsg<Object> returnMsg;
        try {
            returnMsg = depositoryService.getDepositoryMsgList(request);
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "未獲取到相關數據......");
            logger.info("/app/depository/getDepositoryMsgList/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/getDepositoryStatisticMsgList", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getDepositoryStatisticMsgList(HttpServletRequest request) {
        ReturnMsg<Object> returnMsg;
        try {
            returnMsg = depositoryService.getDepositoryStatisticMsgList(request);
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "未獲取到相關數據......");
            logger.info("/app/depository/getDepositoryStatisticMsgList/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/getAllTypeName/{userid}", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getAllTypeName(@PathVariable("userid") String userid) {
        ReturnMsg returnMsg;
        try {
            returnMsg = depositoryService.getAllTypeName(userid);
        } catch (Exception e) {
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "未獲取到相關數據...");
            logger.info("/app/depository/getAllTypeName/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }
}
