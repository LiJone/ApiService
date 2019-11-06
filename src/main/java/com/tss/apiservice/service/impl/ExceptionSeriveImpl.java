package com.tss.apiservice.service.impl;

import com.tss.apiservice.common.PageUtil;
import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.dao.*;
import com.tss.apiservice.po.*;
import com.tss.apiservice.po.vo.AbnormalExceptionVo;
import com.tss.apiservice.service.ExceptionSerive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ExceptionSeriveImpl implements ExceptionSerive {

    @Autowired
    AbnormalPoMapper abnormalPoMapper;

    @Autowired
    StaffsPoMapper staffsPoMapper;

    @Autowired
    ToolsPoMapper toolsPoMapper;

    @Autowired
    UsersPoMapper usersPoMapper;

    @Autowired
    PermitsPoMapper permitsPoMapper;

    @Override
    public ReturnMsg getExceptionLog(HttpServletRequest request) {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        String userid = request.getParameter("userid");
        String timeBegin = request.getParameter("timeBegin");
        String timeEnd = request.getParameter("timeEnd");
        String pageSize = request.getParameter("pageSize");
        String currentPage = request.getParameter("currentPage");
        String jobnum = request.getParameter("jobnum");
        String order = (String) request.getAttribute("order");
        String excel = (String) request.getAttribute("excel");
        if (StringUtils.isEmpty(userid)) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //获取机构id
            String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
            //获取这个人的记录
            HashMap<Object, Object> map = new HashMap<>();
            map.put("orgid", orgid);
            if(!StringUtils.isEmpty(jobnum)){
                map.put("jobnum", jobnum);
            }
            if(!StringUtils.isEmpty(timeBegin)){
                map.put("timeBegin", timeBegin);
            }
            if(!StringUtils.isEmpty(timeEnd)){
                map.put("timeEnd", timeEnd);
            }
            if(!StringUtils.isEmpty(order)){
                map.put("order", order);
            }
            if (StringUtils.isEmpty(pageSize)) {
                pageSize = "10";
            }
            if (StringUtils.isEmpty(currentPage)) {
                currentPage = "1";
            }
            PageUtil pageUtil = null;
            if (StringUtils.isEmpty(excel)) {
                List<AbnormalExceptionVo> count = abnormalPoMapper.selectByHashMap(map);
                pageUtil = new PageUtil(count.size(), Integer.parseInt(pageSize), Integer.parseInt(currentPage), 5);
                int startrow = pageUtil.getStartrow();
                int pagesize = pageUtil.getPagesize();
                map.put("startrow", startrow);
                map.put("pagesize", pagesize);
            }
            List<AbnormalExceptionVo> abnormalPos = abnormalPoMapper.selectByHashMap(map);
            StaffsPo staffsPo;
            ToolsPo toolsPo;
            PermitsPo permitsPo;
            String number;
            ArrayList<Object> list = new ArrayList<>();
            for (AbnormalExceptionVo abnormalPo : abnormalPos) {
                map = new HashMap<>();
                map.put("abnormalPo", abnormalPo);
                number = abnormalPo.getNumber();
                if (abnormalPo.getType() == 0) {
                    permitsPo = permitsPoMapper.selectByPrimaryKey(number);
                    if (permitsPo != null) {
                        map.put("objName", permitsPo.getName());
                    }
                } else if (abnormalPo.getType() == 1) {
                    staffsPo = staffsPoMapper.selectByPrimaryKey(number);
                    if (staffsPo != null) {
                        map.put("objName", staffsPo.getEnname());
                        map.put("objNameCh", staffsPo.getChname());
                    }
                } else if (abnormalPo.getType() == 2) {
                    toolsPo = toolsPoMapper.selectByPrimaryKey(number);
                    if (toolsPo != null) {
                        map.put("objName", toolsPo.getName());
                    }
                }
                list.add(map);
            }
            returnMsg.setCode(ReturnMsg.SUCCESS);
            returnMsg.setMsgbox("成功");
            if (StringUtils.isEmpty(excel)) {
                pageUtil.setPageData(list);
                returnMsg.setData(pageUtil);
            } else {
                returnMsg.setData(list);
            }
        }
        return returnMsg;
    }
}
