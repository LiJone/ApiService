package com.tss.apiservice.controller;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ReportController {

    private static Logger logger = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    ReportService reportService;

    @ResponseBody
    @RequestMapping(value = "/app/report/getAttendanceCollect", method = RequestMethod.GET)
    public ReturnMsg getAttendanceCollect(HttpServletRequest request) {
        ReturnMsg<Object> returnMsg = null;
        try {
            returnMsg = reportService.getAttendanceCollect(request);
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "獲取員工考勤匯總異常...");
            logger.info("/app/report/getAttendanceCollect/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @ResponseBody
    @RequestMapping(value = "/app/report/getOneStaffAttendance", method = RequestMethod.GET)
    public ReturnMsg getOneStaffAttendance(HttpServletRequest request) {
        ReturnMsg<Object> returnMsg = null;
        try {
            returnMsg = reportService.getOneStaffAttendance(request);
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "獲取員工考勤異常...");
            logger.info("/app/report /getOneStaffAttendance/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }
}
