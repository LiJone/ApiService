package com.tss.apiservice.service;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.dto.JobDto;
import com.tss.apiservice.dto.SafeobjsDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

public interface JobService {

    ReturnMsg addJobMsg(String userid,JobDto jobDto) throws ParseException;

    ReturnMsg getJobList(HttpServletRequest request);

    ReturnMsg updateJobMsg(String userid, JobDto jobDto) throws ParseException;

    ReturnMsg deleteJobMsg(String userid, JobDto jobDto);

    ReturnMsg setJobRunStatus ( String userid, JobDto jobDto);

    ReturnMsg setJobObjLeavtime(@PathVariable("userid") String userid, SafeobjsDto safeobjsDto) throws ParseException;

    ReturnMsg getAllNum(String userid);

    ReturnMsg getAllName(String userid);

    void sendUpdateMq(JobDto jobDto);
}
