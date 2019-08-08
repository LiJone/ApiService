package com.tss.apiservice.service.impl;

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
        String order = (String) request.getAttribute("order");
        if (StringUtils.isEmpty(userid)) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //获取机构id
            String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
            //获取这个人的记录
            HashMap<Object, Object> map = new HashMap<>();
            map.put("orgid", orgid);

            if(!StringUtils.isEmpty(timeBegin)){
                map.put("timeBegin", timeBegin);
            }
            if(!StringUtils.isEmpty(timeEnd)){
                map.put("timeEnd", timeEnd);
            }
            if(!StringUtils.isEmpty(order)){
                map.put("order", order);
            }
            List<AbnormalExceptionVo> abnormalPos = abnormalPoMapper.selectByHashMap(map);
            StaffsPo staffsPo = null;
            ToolsPo toolsPo = null;
            PermitsPo permitsPo;
            String number = null;
            ArrayList<Object> list = new ArrayList<>();
            for (int i = 0 ; i < abnormalPos.size() ; i++){
                map = new HashMap<>();
                map.put("abnormalPo" ,abnormalPos.get(i) );
                number = abnormalPos.get(i).getNumber();
                if (abnormalPos.get(i).getType() == 0){
                    permitsPo = permitsPoMapper.selectByPrimaryKey(number);
                    map.put("objName" , permitsPo.getName());
                }else if(abnormalPos.get(i).getType() == 1){
                    staffsPo = staffsPoMapper.selectByPrimaryKey(number);
                    map.put("objName" , staffsPo.getEnname());
                    map.put("objNameCh" , staffsPo.getChname());
                }else if(abnormalPos.get(i).getType() == 2){
                    toolsPo = toolsPoMapper.selectByPrimaryKey(number);
                    map.put("objName" , toolsPo.getName());
                }
                list.add(map);
            }
            returnMsg.setCode(ReturnMsg.SUCCESS);
            returnMsg.setMsgbox("成功");
            returnMsg.setData(list);
        }
        return returnMsg;
    }
}
