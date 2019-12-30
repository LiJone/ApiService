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
import java.util.Map;

@Controller
//@RequiredPermission(PermissionConstants.ADMIN_PERSONG_COMPANY_ADMIN)
public class GatewaysController {
    private static Logger logger = LoggerFactory.getLogger(GatewaysController.class);

    @Autowired
    GatewaysService gatewaysService;

    @RequestMapping(value = "/app/gateWays/addGateWaysMsg/{userid}", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMsg addGateWaysMsg(@PathVariable("userid") String userid, @RequestBody GatewaysDto gatewaysDto) {
        ReturnMsg returnMsg;
        try {
            returnMsg = gatewaysService.addGateways(userid, gatewaysDto);
            if (returnMsg.getCode() == 1) {
                gatewaysService.sendAddMq(gatewaysDto);
            }
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "網關錄入失敗...");
            logger.info("/app/tools/addGateWaysMsg/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/gateWays/getGateWaysMsgList", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getGateWaysMsgList(HttpServletRequest request) {
        ReturnMsg returnMsg;
        try {
            returnMsg = gatewaysService.getGateWaysMsgList(request);
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "未獲取到相關數據...");
            logger.info("/app/gateWays/getGateWaysMsgList 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/gateWays/reloadGateWay/{number}", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMsg reloadGateWay(@PathVariable("number") String number) {
        ReturnMsg returnMsg;
        try {
            returnMsg = gatewaysService.reloadGateWay(number);
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "重啟網關失敗...");
            logger.info("/app/gateWays/reloadGateWay 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/gateWays/deleteGateWaysMsg/{userid}", method = RequestMethod.DELETE)
    @ResponseBody
    public ReturnMsg deleteGateWaysMsg(@PathVariable("userid") String userid , @RequestBody GatewaysDto gatewaysDto) {
        ReturnMsg returnMsg;
        try {
            returnMsg = gatewaysService.deleteGateWaysMsg(userid , gatewaysDto);
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "網關刪除失敗...");
            logger.info("/app/gateWays/deleteGateWaysMsg 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/gateWays/updateGateWaysMsg/{userid}", method = RequestMethod.PUT)
    @ResponseBody
    public ReturnMsg updateGateWaysMsg(@PathVariable("userid") String userid , @RequestBody GatewaysDto gatewaysDto) {
        ReturnMsg returnMsg;
        try {
            returnMsg = gatewaysService.updateGateWaysMsg(userid , gatewaysDto);
            if (returnMsg.getCode() == 1) {
                gatewaysService.sendUpdateMq(gatewaysDto);
            }
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "網關修改失敗...");
            logger.info("/app/gateWays/updateGateWaysMsg 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/gateWays/addGateWaysSetting/{userid}", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMsg addGateWaysSetting(@PathVariable("userid") String userid ,
                                        @RequestBody GatewaysDto gatewaysDto) {
        ReturnMsg returnMsg;
        try {
            returnMsg = gatewaysService.addGateWaysSetting(userid , gatewaysDto.getNumber(), gatewaysDto.getDistance());
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "網關設置失敗...");
            logger.info("/app/gateWays/addGateWaysSetting 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/gateWays/getGateWaysSetting/{number}", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMsg getGateWaysSetting(@PathVariable("number") String number ) {
        ReturnMsg<Object> returnMsg;
        try {
            returnMsg = gatewaysService.getGateWaysSetting(number);
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "未獲取到相關數據...");
            logger.info("/app/gateWays/getGateWaysSetting 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/gateWays/getAllName/{userid}", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getAllName(@PathVariable("userid") String userid) {
        ReturnMsg returnMsg;
        try {
            returnMsg = gatewaysService.getAllName(userid);
        } catch (Exception e) {
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "未獲取到相關數據...");
            logger.info("/app/gateWays/getAllName/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/gateWays/getGateWayType", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getToolType(HttpServletRequest request) {
        ReturnMsg returnMsg;
        try {
            returnMsg = gatewaysService.getGateWayType(request);
        } catch (Exception e) {
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "未獲取到相關數據...");
            logger.info("/app/gateWays/getGateWayType/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }
}
