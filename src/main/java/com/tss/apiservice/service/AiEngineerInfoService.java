package com.tss.apiservice.service;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.form.AiEngineerInfoForm;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 壮Jone
 */
public interface AiEngineerInfoService {
    /**
     * 添加智能工程
     * @param userid 用戶id
     * @param aiEngineerInfoForm 智能工程信息
     * @return
     * @throws Exception
     */
    ReturnMsg addEngineerMsg(String userid, AiEngineerInfoForm aiEngineerInfoForm) throws Exception;

    /**
     * 獲取智能工程列表
     * @param request
     * @return
     */
    ReturnMsg getEngineerList(HttpServletRequest request);

    /**
     * 修改智能工程
     * @param aiEngineerInfoForm 智能工程信息
     * @return
     * @throws Exception
     */
    ReturnMsg updateEngineerMsg(AiEngineerInfoForm aiEngineerInfoForm) throws Exception;

    /**
     * 刪除智能工程
     * @param jobNum 智能工程编号
     * @return
     * @throws Exception
     */
    ReturnMsg deleteEngineerMsg(String jobNum) throws Exception;

    /**
     * 修改智能工程状态
     * @param jobNum 工程编号
     * @return
     */
    ReturnMsg setJobRunStatus(String jobNum);

    /**
     * 获取所有智能工程编号
     * @param userid
     * @return
     */
    ReturnMsg getAllNum(String userid);

    /**
     * 获取所有智能工程名称
     * @param userid
     * @return
     */
    ReturnMsg getAllName(String userid);
}
