package com.tss.apiservice.controller;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.dto.UsersDto;
import com.tss.apiservice.dto.UsersSetDto;
import com.tss.apiservice.service.UsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
public class UserController {
    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UsersService usersService;

    @RequestMapping(value = "/app/user/userLogin", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMsg userLogin(@RequestBody UsersDto usersDto) {
        ReturnMsg returnMsg = null;
        try {
            returnMsg = usersService.userLogin(usersDto.getUsername(), usersDto.getPassword());
        } catch (Exception e) {
            logger.info("/app/user/userLogin 异常 username={}", usersDto.getUsername());
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "用戶登錄失敗...");
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/user/getUserList", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getUserList(HttpServletRequest request) {
        ReturnMsg returnMsg = null;
        try {
            returnMsg = usersService.getUserList(request);
        } catch (Exception e) {
            logger.info("/app/user/getUserList 异常");
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "未獲取到相關數據...");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/user/getUserPower", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getUserPower(HttpServletRequest request) {
        ReturnMsg returnMsg = null;
        try {
            returnMsg = usersService.getUserPower(request);
        } catch (Exception e) {
            logger.info("/app/user/getUserPower 异常");
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "未獲取到相關數據...");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/user/getUserSetting", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getUserSetting(HttpServletRequest request) {
        ReturnMsg returnMsg = null;
        try {
            returnMsg = usersService.getUserSetting(request);
        } catch (Exception e) {
            logger.info("/app/user/getUserSetting 异常");
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "未獲取到相關數據...");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/user/setUserSetting/{userid}", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMsg setUserSetting(@PathVariable("userid") String userid, @RequestBody UsersSetDto usersSetDto) {
        ReturnMsg returnMsg = null;
        try {
            returnMsg = usersService.setUserSetting(userid, usersSetDto);
        } catch (Exception e) {
            logger.info("/app/user/setUserSetting 异常");
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "設置用戶設置失敗...");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/user/addUser/{userid}", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMsg addUser(@PathVariable("userid") String userid, @RequestBody UsersDto usersDto) {
        ReturnMsg returnMsg = null;
        try {
            returnMsg = usersService.addUser(userid, usersDto);
        } catch (Exception e) {
            logger.info("/app/user/addUser 异常");
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "用戶添加失敗...");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/user/updateUser/{userid}", method = RequestMethod.PUT)
    @ResponseBody
    public ReturnMsg updateUser(@PathVariable("userid") String userid, @RequestBody UsersDto usersDto) {
        ReturnMsg returnMsg = null;
        try {
            returnMsg = usersService.updateUser(userid, usersDto);
        } catch (Exception e) {
            logger.info("/app/user/updateUser 异常");
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "用戶修改失敗...");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/user/deleteUser/{userid}", method = RequestMethod.DELETE)
    @ResponseBody
    public ReturnMsg deleteUser(@PathVariable("userid") String userid, @RequestBody UsersDto usersDto) {
        ReturnMsg returnMsg = null;
        try {
            returnMsg = usersService.deleteUser(userid, usersDto);
        } catch (Exception e) {
            logger.info("/app/user/deleteUser 异常");
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "用戶刪除失敗...");
            e.printStackTrace();
        }
        return returnMsg;
    }
}
