package com.tss.apiservice.service;

import com.tss.apiservice.common.ReturnMsg;

import javax.servlet.http.HttpServletRequest;

public interface ReportService {
    ReturnMsg getAttendanceCollect(HttpServletRequest request) throws Exception;

    ReturnMsg getOneStaffAttendance(HttpServletRequest request) throws Exception;
}
