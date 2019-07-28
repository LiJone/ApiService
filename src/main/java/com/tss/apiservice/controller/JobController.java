package com.tss.apiservice.controller;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.dto.JobDto;
import com.tss.apiservice.dto.SafeobjsDto;
import com.tss.apiservice.service.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
public class JobController {

    private static Logger logger = LoggerFactory.getLogger(JobController.class);

    @Autowired
    JobService jobService;

    @RequestMapping(value = "/app/job/addJobMsg/{userid}", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMsg addJobMsg(@PathVariable("userid") String userid, @RequestBody JobDto jobDto) {
        ReturnMsg<Object> returnMsg = null;
        try {
            returnMsg = jobService.addJobMsg(userid, jobDto);
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "工程錄入異常...");
            logger.info("/app/job/addJobMsg/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/job/getJobList", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getJobList(HttpServletRequest request) {
        ReturnMsg<Object> returnMsg = null;
        try {
            returnMsg = jobService.getJobList(request);
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "工程列表異常...");
            logger.info("/app/job/getJobList 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/job/updateJobMsg/{userid}", method = RequestMethod.PUT)
    @ResponseBody
    public ReturnMsg updateJobMsg(@PathVariable("userid") String userid, @RequestBody JobDto jobDto) {
        ReturnMsg<Object> returnMsg = null;
        try {
            returnMsg = jobService.updateJobMsg(userid, jobDto);
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "工程修改異常...");
            logger.info("/app/job/updateJobMsg/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/job/setJobObjLeavtime/{userid}", method = RequestMethod.PUT)
    @ResponseBody
    public ReturnMsg setJobObjLeavtime(@PathVariable("userid") String userid, @RequestBody SafeobjsDto safeobjsDto) {
        ReturnMsg<Object> returnMsg = null;
        try {
            returnMsg = jobService.setJobObjLeavtime(userid, safeobjsDto);
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "授權時間異常...");
            logger.info("/app/job/setJobObjLeavtime/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/job/deleteJobMsg/{userid}", method = RequestMethod.DELETE)
    @ResponseBody
    public ReturnMsg deleteJobMsg(@PathVariable("userid") String userid, @RequestBody JobDto jobDto) {
        ReturnMsg<Object> returnMsg = null;
        try {
            returnMsg = jobService.deleteJobMsg(userid, jobDto);
        } catch (Exception e) {
            returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "工程刪除異常...");
            logger.info("/app/job/deleteJobMsg/{userid} 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping(value = "/app/job/setJobRunStatus/{userid}", method = RequestMethod.PUT)
    @ResponseBody
    public ReturnMsg setJobRunStatus (@PathVariable("userid") String userid, @RequestBody JobDto jobDto) {
            ReturnMsg<Object> returnMsg = null;
            try {
                returnMsg = jobService.setJobRunStatus(userid, jobDto);
            } catch (Exception e) {
                returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "工程狀態修改異常...");
                logger.info("/app/job/setJobRunStatus /{userid} 异常");
                e.printStackTrace();
            }
            return returnMsg;
        }
}
