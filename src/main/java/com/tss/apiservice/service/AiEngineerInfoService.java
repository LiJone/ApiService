package com.tss.apiservice.service;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.form.AiEngineerInfoForm;

import javax.servlet.http.HttpServletRequest;

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
     * @param userid 用戶id
     * @param aiEngineerInfoForm 智能工程信息
     * @return
     * @throws Exception
     */
    ReturnMsg updateEngineerMsg(String userid, AiEngineerInfoForm aiEngineerInfoForm);

    /**
     * 刪除智能工程
     * @param userid 用戶id
     * @param aiEngineerInfoForm 智能工程信息
     * @return
     * @throws Exception
     */
    ReturnMsg deleteEngineerMsg(String userid, AiEngineerInfoForm aiEngineerInfoForm);
}
