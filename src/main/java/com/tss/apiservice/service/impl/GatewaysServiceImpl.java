package com.tss.apiservice.service.impl;

import com.tss.apiservice.common.PageUtil;
import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.common.utils.MQAllSendMessage;
import com.tss.apiservice.common.utils.MQCode;
import com.tss.apiservice.dao.EngineerinfoPoMapper;
import com.tss.apiservice.dao.GatewaysPoMapper;
import com.tss.apiservice.dao.UsersPoMapper;
import com.tss.apiservice.dto.GatewaysDto;
import com.tss.apiservice.po.EngineerinfoPo;
import com.tss.apiservice.po.GatewaysPo;
import com.tss.apiservice.service.GatewaysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@Service
public class GatewaysServiceImpl implements GatewaysService {

    @Autowired
    EngineerinfoPoMapper engineerinfoPoMapper;

    @Autowired
    GatewaysPoMapper gatewaysPoMapper;

    @Autowired
    ApiServiceMQImpl apiServiceMQ;

    @Autowired
    UsersPoMapper usersPoMapper;

    @Override
    @Transactional
    public ReturnMsg<Object> addGateways(String userid, GatewaysDto gatewaysDto) {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");

        if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(gatewaysDto.getNumber()) ||
                StringUtils.isEmpty(gatewaysDto.getType()) || StringUtils.isEmpty(gatewaysDto.getName())) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            GatewaysPo ret = gatewaysPoMapper.selectByPrimaryKey(gatewaysDto.getNumber());
            if (ret == null) {
                GatewaysPo gatewaysPo_byName = gatewaysPoMapper.selectByName(gatewaysDto.getName());
                if (gatewaysPo_byName == null) {
                    GatewaysPo gatewaysPo = new GatewaysPo();
                    gatewaysPo.setNumber(gatewaysDto.getNumber());
                    gatewaysPo.setName(gatewaysDto.getName());
                    gatewaysPo.setProtocol(gatewaysDto.getProtocol());
                    gatewaysPo.setNetwork(gatewaysDto.getNetwork());
                    gatewaysPo.setType(gatewaysDto.getType());
                    //获取机构id
                    String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
                    gatewaysPo.setOrgid(orgid);
                    gatewaysPo.setOsdid(gatewaysDto.getOsdid());
                    gatewaysPoMapper.insertSelective(gatewaysPo);
                    returnMsg = new ReturnMsg<>(ReturnMsg.SUCCESS, "成功");

                    EngineerinfoPo engineerinfoPo = engineerinfoPoMapper.selectByOsdid(gatewaysDto.getOsdid());
                    if (engineerinfoPo != null && engineerinfoPo.getSchedule() == 1) {
                        MQAllSendMessage.sendJobMq(engineerinfoPo.getJobnum(), gatewaysDto.getOsdid(), MQCode.JOB_RUN_UPDATE, apiServiceMQ);
                    }
                } else {
                    returnMsg.setMsgbox("網關名稱已存在...");
                }
            } else {
                returnMsg.setMsgbox("網關唯壹編碼已存在...");
            }
        }

        return returnMsg;
    }

    @Override
    public ReturnMsg getGateWaysMsgList(HttpServletRequest request) {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        String userid = request.getParameter("userid");
        String pageSize = request.getParameter("pageSize");
        String currentPage = request.getParameter("currentPage");
        String name = request.getParameter("name");

        if (StringUtils.isEmpty(userid)) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //获取机构id
            String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("orgid", orgid);
            if (!StringUtils.isEmpty(name)) {
                hashMap.put("name", name);
            }
            //根据带有符合条件的总数，进行分页查询的操作
            List<GatewaysPo> gatewaysPos = gatewaysPoMapper.selectNumsByPage(hashMap);
            if (StringUtils.isEmpty(pageSize)) {
                pageSize = "10";
            }
            if (StringUtils.isEmpty(currentPage)) {
                currentPage = "1";
            }
            if (gatewaysPos.size() < 1) {
                returnMsg.setMsgbox("找不到符合條件數據...");
            } else {
                PageUtil pageUtil = new PageUtil(gatewaysPos.size(), Integer.valueOf(pageSize), Integer.valueOf(currentPage), 5);
                int startrow = pageUtil.getStartrow();
                int pagesize = pageUtil.getPagesize();
                hashMap.put("startrow", startrow);
                hashMap.put("pagesize", pagesize);
                List<GatewaysPo> gatewaysPosList = gatewaysPoMapper.selectNumsByPage(hashMap);
                pageUtil.setPageData(gatewaysPosList);
                returnMsg = new ReturnMsg<>(ReturnMsg.SUCCESS, "成功", pageUtil);
            }
        }
        return returnMsg;
    }

    @Override
    @Transactional
    public ReturnMsg deleteGateWaysMsg(String userid, GatewaysDto gatewaysDto) {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");

        if (StringUtils.isEmpty(gatewaysDto.getNumber()) || StringUtils.isEmpty(userid)) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            gatewaysPoMapper.deleteByPrimaryKey(gatewaysDto.getNumber());
            returnMsg = new ReturnMsg<>(ReturnMsg.SUCCESS, "成功");

            EngineerinfoPo engineerinfoPo = engineerinfoPoMapper.selectByOsdid(gatewaysDto.getOsdid());
            if (engineerinfoPo != null && engineerinfoPo.getSchedule() == 1) {
                MQAllSendMessage.sendJobMq(engineerinfoPo.getJobnum(), gatewaysDto.getOsdid(), MQCode.JOB_RUN_UPDATE, apiServiceMQ);
            }
        }
        return returnMsg;
    }

    @Override
    @Transactional
    public ReturnMsg updateGateWaysMsg(String userid, GatewaysDto gatewaysDto) {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(gatewaysDto.getNumber()) ||
                StringUtils.isEmpty(gatewaysDto.getType()) || StringUtils.isEmpty(gatewaysDto.getName())) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            GatewaysPo gatewaysPo_byName = gatewaysPoMapper.selectByName(gatewaysDto.getName());
            if (gatewaysPo_byName == null || gatewaysPo_byName.getNumber().equals(gatewaysDto.getNumber())) {
                GatewaysPo gatewaysPo = new GatewaysPo();
                gatewaysPo.setNumber(gatewaysDto.getNumber());
                gatewaysPo.setName(gatewaysDto.getName());
                gatewaysPo.setProtocol(gatewaysDto.getProtocol());
                gatewaysPo.setNetwork(gatewaysDto.getNetwork());
                gatewaysPo.setType(gatewaysDto.getType());
                //获取机构id
                String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
                gatewaysPo.setOrgid(orgid);
                gatewaysPo.setOsdid(gatewaysDto.getOsdid());
                gatewaysPoMapper.updateByPrimaryKeySelective(gatewaysPo);
                returnMsg = new ReturnMsg<>(ReturnMsg.SUCCESS, "成功");

                EngineerinfoPo engineerinfoPo = engineerinfoPoMapper.selectByOsdid(gatewaysDto.getOsdid());
                if (engineerinfoPo != null && engineerinfoPo.getSchedule() == 1) {
                    MQAllSendMessage.sendJobMq(engineerinfoPo.getJobnum(), gatewaysDto.getOsdid(), MQCode.JOB_RUN_UPDATE, apiServiceMQ);
                }
            } else {
                returnMsg.setMsgbox("網關名稱已存在...");
            }
        }
        return returnMsg;
    }

    @Override
    @Transactional
    public ReturnMsg addGateWaysSetting(String userid, GatewaysDto gatewaysDto) {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(gatewaysDto.getNumber())) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            GatewaysPo gatewaysPo = new GatewaysPo();
            gatewaysPo.setNumber(gatewaysDto.getNumber());

            gatewaysPoMapper.updateByPrimaryKeySelective(gatewaysPo);
            gatewaysPo = gatewaysPoMapper.selectByPrimaryKey(gatewaysDto.getNumber());
            //推送消息 MQ
            MQAllSendMessage.sendGateWaysToMq(gatewaysPo, MQCode.GATEWAYS_SETING, apiServiceMQ);

            returnMsg.setCode(ReturnMsg.SUCCESS);
            returnMsg.setMsgbox("成功");
        }
        return returnMsg;
    }

    @Override
    public ReturnMsg<Object> getGateWaysStatus(String userid) {
        return null;
    }

    @Override
    public ReturnMsg<Object> reloadGateWay(String number) {
        return null;
    }
}
