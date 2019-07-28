package com.tss.apiservice.service;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.dto.AttendSetDto;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 壮Jone
 */
public interface OrganAttendSetService {

    /**
     * 获取机构设置
     * @param request
     * @return
     */
    ReturnMsg getOrganSetting(HttpServletRequest request);

    /**
     * 修改机构设置
     * @param userid
     * @param attendSetDto
     * @return
     */
    ReturnMsg setOrganSetting(String userid, AttendSetDto attendSetDto) throws Exception;
}
