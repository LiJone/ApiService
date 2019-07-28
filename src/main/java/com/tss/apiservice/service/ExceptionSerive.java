package com.tss.apiservice.service;

import com.tss.apiservice.common.ReturnMsg;

import javax.servlet.http.HttpServletRequest;

public interface ExceptionSerive {

    ReturnMsg getExceptionLog(HttpServletRequest request);

}
