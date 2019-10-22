package com.tss.apiservice.service;

import com.tss.apiservice.common.ReturnMsg;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

/**
 * @author 壮Jone
 */
public interface DepositoryService {

    /**
     * 根据条件获取所有仓库记录
     * @param request 条件信息
     * @return 返回集合
     * @throws ParseException
     */
    ReturnMsg<Object> getDepositoryMsgList(HttpServletRequest request) throws ParseException;

    /**
     * 根据条件获取所有仓库统计记录
     * @param request 条件信息
     * @return 返回集合
     */
    ReturnMsg<Object> getDepositoryStatisticMsgList(HttpServletRequest request);
}
