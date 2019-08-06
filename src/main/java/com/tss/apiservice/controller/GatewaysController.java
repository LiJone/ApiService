package com.tss.apiservice.controller;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.dto.GatewaysDto;
import com.tss.apiservice.service.GatewaysService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
//@RequiredPermission(PermissionConstants.ADMIN_PERSONG_COMPANY_ADMIN)
public class GatewaysController {
    private static Logger logger = LoggerFactory.getLogger(GatewaysController.class);

    @Autowired
    GatewaysService gatewaysService;

    @RequestMapping(value = "/app/gateWays/addGateWaysMsg/{userid}", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMsg addGateWaysMsg(@PathVariable("userid") String userid, @RequestBody GatewaysDto gatewaysDto) {
        ReturnMsg<Object> returnMsg = null;
        try {
            returnMsg = gatewaysService.addGateways(userid, gatewaysDto);
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "網關錄入異常...");
            logger.info("/app/tools/addGateWaysMsg/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/gateWays/getGateWaysMsgList", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getGateWaysMsgList(HttpServletRequest request) {
        ReturnMsg<Object> returnMsg = null;
        try {
            returnMsg = gatewaysService.getGateWaysMsgList(request);
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "獲取網關列表異常...");
            logger.info("/app/gateWays/getGateWaysMsgList 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/gateWays/getGateWaysStatus/{userid}", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMsg getGateWaysSetting(@PathVariable("userid") String userid) {
        ReturnMsg<Object> returnMsg = null;
        try {
            returnMsg = gatewaysService.getGateWaysStatus(userid);
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "獲取網關狀態異常...");
            logger.info("/app/gateWays/getGateWaysStatus 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/gateWays/reloadGateWay/{number}", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMsg reloadGateWay(@PathVariable("number") String number) {
        ReturnMsg<Object> returnMsg = null;
        try {
            returnMsg = gatewaysService.reloadGateWay(number);
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "重啟網關異常...");
            logger.info("/app/gateWays/reloadGateWay 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/gateWays/deleteGateWaysMsg/{userid}", method = RequestMethod.DELETE)
    @ResponseBody
    public ReturnMsg deleteGateWaysMsg(@PathVariable("userid") String userid , @RequestBody GatewaysDto gatewaysDto) {
        ReturnMsg<Object> returnMsg = null;
        try {
            returnMsg = gatewaysService.deleteGateWaysMsg(userid , gatewaysDto);
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "網關刪除異常...");
            logger.info("/app/gateWays/deleteGateWaysMsg 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/gateWays/updateGateWaysMsg/{userid}", method = RequestMethod.PUT)
    @ResponseBody
    public ReturnMsg updateGateWaysMsg(@PathVariable("userid") String userid , @RequestBody GatewaysDto gatewaysDto) {
        ReturnMsg<Object> returnMsg = null;
        try {
            returnMsg = gatewaysService.updateGateWaysMsg(userid , gatewaysDto);
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "網關修改異常...");
            logger.info("/app/gateWays/updateGateWaysMsg 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/gateWays/addGateWaysSetting/{userid}", method = RequestMethod.PUT)
    @ResponseBody
    public ReturnMsg addGateWaysSetting(@PathVariable("userid") String userid ,
                                        @RequestParam("number") String number,
                                        @RequestParam("distance") String distance) {
        ReturnMsg<Object> returnMsg = null;
        try {
            returnMsg = gatewaysService.addGateWaysSetting(userid , number, distance);
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "網關設置異常...");
            logger.info("/app/gateWays/addGateWaysSetting 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }
}
