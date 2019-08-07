package com.tss.apiservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import com.tss.apiservice.utils.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Value("${getStatusUrl}")
    private String getStatusUrl;

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
    public ReturnMsg getGateWaysMsgList(HttpServletRequest request) throws IOException {
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
                List<String> ids = new ArrayList<>(gatewaysPos.size());
                for (GatewaysPo gatewaysPo : gatewaysPosList) {
                    ids.add(gatewaysPo.getNumber());
                }
                Map<String, Object> param = new HashMap<>(1);
                param.put("devsn", ids);
                String s = HttpUtils.sendPost(JSON.toJSONString(param), getStatusUrl);
                if (!StringUtils.isEmpty(s)) {
                    JSONObject jsonObject = JSON.parseObject(s);
                    String code = jsonObject.getString("code");
                    if ("200".equals(code)) {
                        JSONArray data = jsonObject.getJSONArray("data");
                        if (data != null) {
                            for (int i = 0; i < data.size(); i++) {
                                JSONObject object = data.getJSONObject(i);
                                String devsn = object.getString("devsn");
                                for (GatewaysPo gatewaysPo : gatewaysPosList) {
                                    if (devsn.equals(gatewaysPo.getNumber())) {
                                        gatewaysPo.setStatus(object.getBoolean("online"));
                                    }
                                }

                            }
                        }
                    }
                }
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
    public ReturnMsg addGateWaysSetting(String userid, String number, String distance) {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(number) || StringUtils.isEmpty(distance)) {
            returnMsg.setMsgbox("參數異常...");
        } else {

            GatewaysPo gatewaysPo = gatewaysPoMapper.selectByPrimaryKey(number);
            //推送消息 MQ
            MQAllSendMessage.sendGateWaysToMq(gatewaysPo, MQCode.GATEWAYS_SETING, apiServiceMQ);

            returnMsg.setCode(ReturnMsg.SUCCESS);
            returnMsg.setMsgbox("成功");
        }
        return returnMsg;
    }

    @Override
    public ReturnMsg<Object> getGateWaysStatus(String userid) throws IOException {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        if (StringUtils.isEmpty(userid)) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //获取机构id
            String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
            Map<String, Object> map = new HashMap<>(1);
            map.put("orgid", orgid);
            List<GatewaysPo> gatewaysPos = gatewaysPoMapper.selectNumsByPage(map);
            List<String> ids = new ArrayList<>(gatewaysPos.size());
            for (GatewaysPo gatewaysPo : gatewaysPos) {
                ids.add(gatewaysPo.getNumber());
            }
            Map<String, Object> param = new HashMap<>(1);
            param.put("devsn", ids);
            String s = HttpUtils.sendPost(JSON.toJSONString(param), getStatusUrl);
            if (!StringUtils.isEmpty(s)) {
                JSONObject jsonObject = JSON.parseObject(s);
                String code = jsonObject.getString("code");
                if ("200".equals(code)) {
                    JSONArray data = jsonObject.getJSONArray("data");
                } else {

                }
            } else {
                returnMsg.setMsgbox("獲取狀態返回為空");
                return returnMsg;
            }
            returnMsg.setCode(ReturnMsg.SUCCESS);
            returnMsg.setMsgbox("成功");
        }
        return returnMsg;
    }

    @Override
    public ReturnMsg<Object> reloadGateWay(String number) {
        return null;
    }
}
