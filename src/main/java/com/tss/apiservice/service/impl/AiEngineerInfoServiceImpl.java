package com.tss.apiservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tss.apiservice.common.PageUtil;
import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.common.utils.MQAllSendMessage;
import com.tss.apiservice.common.utils.MQCode;
import com.tss.apiservice.dao.*;
import com.tss.apiservice.dto.NoiseDTO;
import com.tss.apiservice.dto.SurroundingDto;
import com.tss.apiservice.form.*;
import com.tss.apiservice.po.*;
import com.tss.apiservice.service.AiEngineerInfoService;

import com.tss.apiservice.utils.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 壮Jone
 */
@Component
public class AiEngineerInfoServiceImpl implements AiEngineerInfoService {
    private static Logger logger = LoggerFactory.getLogger(AiEngineerInfoServiceImpl.class);

    @Autowired
    private AiEngineerInfoMapper aiEngineerInfoMapper;

    @Autowired
    private UsersPoMapper usersPoMapper;

    @Autowired
    private ApiServiceMQImpl apiServiceMq;

    @Value("${getSurroundingsUrl}")
    private String getSurroundingsUrl;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ReturnMsg addEngineerMsg(String userid, AiEngineerInfoForm aiEngineerInfoForm) throws Exception {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(aiEngineerInfoForm.getJobNum())) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //获取机构id
            String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
            //判斷工程編號
            AiEngineerInfoPO aiEngineerInfo = aiEngineerInfoMapper.selectByAiNum(aiEngineerInfoForm.getJobNum());
            if (aiEngineerInfo != null) {
                throw new Exception("工程編號" + aiEngineerInfoForm.getJobNum() + "已存在...");
            } else {
                //判斷工程綁定盒子
                List<OsdBindInfoForm> osdBindInfoFormList = aiEngineerInfoForm.getOsdBindInfoFormList();
                for (OsdBindInfoForm osdBindInfoForm : osdBindInfoFormList) {
                    OsdBindInfoPO osdInfo = aiEngineerInfoMapper.selectByOsdNum(osdBindInfoForm.getOsdNum());
                    if (osdInfo != null) {
                        throw new Exception("工程編號" + aiEngineerInfoForm.getJobNum() + "的盒子編號" + osdInfo.getOsdNum() + "已綁定...");
                    } else {
                        OsdBindInfoPO osdBindInfo = new OsdBindInfoPO();
                        osdBindInfo.setOsdNum(osdBindInfoForm.getOsdNum());
                        osdBindInfo.setOsdName(osdBindInfoForm.getOsdName());
                        osdBindInfo.setBindType(0);
                        osdBindInfo.setBindNum(aiEngineerInfoForm.getJobNum());
                        aiEngineerInfoMapper.insertOsdInfo(osdBindInfo);
                    }
                }
                //判斷工程綁定許可證
                List<PermitBindInfoForm> permitBindInfoFormList = aiEngineerInfoForm.getPermitBindInfoFormList();
                for (PermitBindInfoForm permitBindInfoForm : permitBindInfoFormList) {
                    PermitBindInfoPO permitInfo = aiEngineerInfoMapper.selectByPermitNum(permitBindInfoForm.getPermitNum());
                    if (permitInfo != null) {
                        throw new Exception("工程編號" + aiEngineerInfoForm.getJobNum() + "的許可證編號" + permitInfo.getPermitNum() + "已綁定...");
                    } else {
                        PermitBindInfoPO permitBindInfo = new PermitBindInfoPO();
                        permitBindInfo.setPermitNum(permitBindInfoForm.getPermitNum());
                        permitBindInfo.setBindType(0);
                        permitBindInfo.setBindNum(aiEngineerInfoForm.getJobNum());
                        aiEngineerInfoMapper.insertPermitInfo(permitBindInfo);
                    }
                }
                //判斷工程綁定功能
                List<FuncBindInfoForm> funcBindInfoFormList = aiEngineerInfoForm.getFuncBindInfoFormList();
                for (FuncBindInfoForm funcBindInfoForm : funcBindInfoFormList) {
                    FuncBindInfoPO funcInfo = aiEngineerInfoMapper.selectByFuncNum(funcBindInfoForm.getFuncNum());
                    if (funcInfo != null) {
                        throw new Exception("工程編號" + aiEngineerInfoForm.getJobNum() + "的功能編號" + funcInfo.getFuncNum() + "已綁定...");
                    } else {
                        if (funcBindInfoForm.getFuncType() == 1) {
                            //判斷功能綁定盒子
                            List<OsdBindInfoForm> osdBindInfoFormListForFunc = funcBindInfoForm.getOsdBindInfoFormList();
                            for (OsdBindInfoForm osdBindInfoForm : osdBindInfoFormListForFunc) {
                                OsdBindInfoPO osdInfo = aiEngineerInfoMapper.selectByOsdNum(osdBindInfoForm.getOsdNum());
                                if (osdInfo != null) {
                                    throw new Exception("功能編號" + funcBindInfoForm.getFuncNum() + "的盒子編號" + osdInfo.getOsdNum() + "已綁定...");
                                } else {
                                    OsdBindInfoPO osdBindInfo = new OsdBindInfoPO();
                                    osdBindInfo.setOsdNum(osdBindInfoForm.getOsdNum());
                                    osdBindInfo.setOsdName(osdBindInfoForm.getOsdName());
                                    osdBindInfo.setBindType(1);
                                    osdBindInfo.setBindNum(funcBindInfoForm.getFuncNum());
                                    aiEngineerInfoMapper.insertOsdInfo(osdBindInfo);
                                }
                            }
                        }
                        //判斷功能綁定許可證
                        List<PermitBindInfoForm> permitBindInfoFormListForFunc = funcBindInfoForm.getPermitBindInfoFormList();
                        for (PermitBindInfoForm permitBindInfoForm : permitBindInfoFormListForFunc) {
                            PermitBindInfoPO permitInfo = aiEngineerInfoMapper.selectByPermitNum(permitBindInfoForm.getPermitNum());
                            if (permitInfo != null) {
                                throw new Exception("功能編號" + funcBindInfoForm.getFuncNum() + "的許可證編號" + permitInfo.getPermitNum() + "已綁定...");
                            } else {
                                PermitBindInfoPO permitBindInfo = new PermitBindInfoPO();
                                permitBindInfo.setPermitNum(permitBindInfoForm.getPermitNum());
                                permitBindInfo.setBindType(1);
                                permitBindInfo.setBindNum(funcBindInfoForm.getFuncNum());
                                aiEngineerInfoMapper.insertPermitInfo(permitBindInfo);
                            }
                        }
                        List<ToolBindInfoForm> toolBindInfoFormList = funcBindInfoForm.getToolBindInfoFormList();
                        for (ToolBindInfoForm toolBindInfoForm : toolBindInfoFormList) {
                            ToolBindInfoPO toolInfo = new ToolBindInfoPO();
                            toolInfo.setTypeId(toolBindInfoForm.getTypeId());
                            toolInfo.setTypeName(toolBindInfoForm.getTypeName());
                            toolInfo.setCount(toolBindInfoForm.getCount());
                            toolInfo.setValid(toolBindInfoForm.getValid());
                            toolInfo.setFuncNum(funcBindInfoForm.getFuncNum());
                            aiEngineerInfoMapper.insertToolInfo(toolInfo);
                        }
                        FuncBindInfoPO funcBindInfo = new FuncBindInfoPO();
                        funcBindInfo.setFuncNum(funcBindInfoForm.getFuncNum());
                        funcBindInfo.setFuncName(funcBindInfoForm.getFuncName());
                        funcBindInfo.setFuncStatus(funcBindInfoForm.getFuncStatus());
                        funcBindInfo.setFuncType(funcBindInfoForm.getFuncType());
                        funcBindInfo.setStartTime(funcBindInfoForm.getStartTime());
                        funcBindInfo.setEndTime(funcBindInfoForm.getEndTime());
                        funcBindInfo.setJobNum(aiEngineerInfoForm.getJobNum());
                        aiEngineerInfoMapper.insertFuncInfo(funcBindInfo);
                    }
                }
                List<WSWPInfoForm> wswpInfoFormList = aiEngineerInfoForm.getWswpInfoFormList();
                for (WSWPInfoForm wswpInfoForm : wswpInfoFormList) {
                    Integer openCount = aiEngineerInfoMapper.getCpCountByCpNum(wswpInfoForm.getCpNum());
                    Integer closeCount = aiEngineerInfoMapper.getCpCountByCpNumClose(wswpInfoForm.getCpNum());
                    if (openCount != null && openCount > 3) {
                        throw new Exception("工程編號" + aiEngineerInfoForm.getJobNum() + "的CP編號" + wswpInfoForm.getCpNum() + "已綁定其他運行中工程3個以上");
                    } else if (closeCount != null && closeCount > 10) {
                        throw new Exception("工程編號" + aiEngineerInfoForm.getJobNum() + "的CP編號" + wswpInfoForm.getCpNum() + "已綁定其他關閉中工程10個以上");
                    } else {
                        WSWPInfoPO wswpBindInfo = new WSWPInfoPO();
                        wswpBindInfo.setCpNum(wswpInfoForm.getCpNum());
                        wswpBindInfo.setCaptainCount(wswpInfoForm.getCaptainCount());
                        wswpBindInfo.setJobNum(aiEngineerInfoForm.getJobNum());
                        aiEngineerInfoMapper.insertWswpInfo(wswpBindInfo);
                    }
                }
                AiEngineerInfoPO aiEngineer = new AiEngineerInfoPO();
                aiEngineer.setJobNum(aiEngineerInfoForm.getJobNum());
                aiEngineer.setJobName(aiEngineerInfoForm.getJobName());
                aiEngineer.setStartTime(aiEngineerInfoForm.getStartTime());
                aiEngineer.setEndTime(aiEngineerInfoForm.getEndTime());
                aiEngineer.setOrgId(orgid);
                aiEngineerInfoMapper.insertAiEngineerInfo(aiEngineer);
                returnMsg.setMsgbox("添加成功");
                returnMsg.setCode(ReturnMsg.SUCCESS);
            }
        }
        return returnMsg;
    }

    @Override
    public ReturnMsg getEngineerList(HttpServletRequest request) {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        String userid = request.getParameter("userid");
        String pageSize = request.getParameter("pageSize");
        String currentPage = request.getParameter("currentPage");
        String jobNum = request.getParameter("jobNum");
        String jobName = request.getParameter("jobName");
        if (StringUtils.isEmpty(userid)) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //获取机构id
            String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("orgId", orgid);
            if (!StringUtils.isEmpty(jobNum)) {
                hashMap.put("jobNum", jobNum);
            }
            if (!StringUtils.isEmpty(jobName)) {
                hashMap.put("jobName", jobName);
            }
            //根据带有符合条件的总数，进行分页查询的操作
            List<AiEngineerInfoForm> aiEngineerInfo = aiEngineerInfoMapper.selectListByMap(hashMap);
            if (StringUtils.isEmpty(pageSize)) {
                pageSize = "10";
            }
            if (StringUtils.isEmpty(currentPage)) {
                currentPage = "1";
            }
            if (aiEngineerInfo.size() < 1) {
                returnMsg.setMsgbox("找不到符合條件數據...");
            } else {
                PageUtil pageUtil = new PageUtil(aiEngineerInfo.size(), Integer.parseInt(pageSize), Integer.parseInt(currentPage), 5);
                int startrow = pageUtil.getStartrow();
                int pagesize = pageUtil.getPagesize();
                hashMap.put("startrow", startrow);
                hashMap.put("pagesize", pagesize);
                List<AiEngineerInfoForm> aiEngineerInfoList = aiEngineerInfoMapper.selectListByMap(hashMap);
                for (AiEngineerInfoForm aiEngineerInfoForm : aiEngineerInfoList) {
                    List<PermitBindInfoForm> permitBindInfoForms = aiEngineerInfoMapper.selectPermitFormByAiNum(aiEngineerInfoForm.getJobNum());
                    aiEngineerInfoForm.setPermitBindInfoFormList(permitBindInfoForms);
                    List<OsdBindInfoForm> osdBindInfoForms = aiEngineerInfoMapper.selectOsdFormByAiNum(aiEngineerInfoForm.getJobNum());
                    aiEngineerInfoForm.setOsdBindInfoFormList(osdBindInfoForms);
                    List<WSWPInfoForm> wswpInfoForms = aiEngineerInfoMapper.selectWswpFormByAiNum(aiEngineerInfoForm.getJobNum());
                    aiEngineerInfoForm.setWswpInfoFormList(wswpInfoForms);
                    List<FuncBindInfoForm> funcBindInfoForms = aiEngineerInfoMapper.selectFuncFormByAiNum(aiEngineerInfoForm.getJobNum());
                    for (FuncBindInfoForm funcBindInfoForm : funcBindInfoForms) {
                        if (funcBindInfoForm.getFuncType() == 1) {
                            List<OsdBindInfoForm> osdBindInfoForms1 = aiEngineerInfoMapper.selectOsdFormByFuncNum(funcBindInfoForm.getFuncNum());
                            funcBindInfoForm.setOsdBindInfoFormList(osdBindInfoForms1);
                        }
                        List<PermitBindInfoForm> permitBindInfoForms1 = aiEngineerInfoMapper.selectPermitFormByFuncNum(funcBindInfoForm.getFuncNum());
                        funcBindInfoForm.setPermitBindInfoFormList(permitBindInfoForms1);
                        List<ToolBindInfoForm> toolBindInfoForms = aiEngineerInfoMapper.selectToolFormByFuncNum(funcBindInfoForm.getFuncNum());
                        funcBindInfoForm.setToolBindInfoFormList(toolBindInfoForms);
                    }
                    aiEngineerInfoForm.setFuncBindInfoFormList(funcBindInfoForms);
                }
                pageUtil.setPageData(aiEngineerInfoList);
                returnMsg = new ReturnMsg<>(ReturnMsg.SUCCESS, "成功", pageUtil);
            }
        }
        return returnMsg;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ReturnMsg updateEngineerMsg(AiEngineerInfoForm aiEngineerInfoForm) throws Exception {
        ReturnMsg returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        if (StringUtils.isEmpty(aiEngineerInfoForm.getId()) || StringUtils.isEmpty(aiEngineerInfoForm.getJobNum())) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //删除原先工程绑定盒子
            aiEngineerInfoMapper.deleteOsdByAiNum(aiEngineerInfoForm.getJobNum());
            //删除原先工程绑定许可证
            aiEngineerInfoMapper.deletePermitByAiNum(aiEngineerInfoForm.getJobNum());
            //删除原先工程绑定wswp
            aiEngineerInfoMapper.deleteWswpByAiNum(aiEngineerInfoForm.getJobNum());
            //删除原先工程绑定功能
            List<FuncBindInfoPO> funcBindInfoList = aiEngineerInfoMapper.selectFuncByAiNum(aiEngineerInfoForm.getJobNum());
            for (FuncBindInfoPO bindInfo : funcBindInfoList) {
                if (bindInfo.getFuncType() == 1) {
                    //删除原先工程功能绑定盒子
                    aiEngineerInfoMapper.deleteOsdByFuncNum(bindInfo.getFuncNum());
                }
                //删除原先工程功能绑定许可证
                aiEngineerInfoMapper.deletePermitByFuncNum(bindInfo.getFuncNum());
                //删除原先工程功能绑定工具
                aiEngineerInfoMapper.deleteToolByFuncNum(bindInfo.getFuncNum());
            }
            //删除原先工程绑定功能
            aiEngineerInfoMapper.deleteFuncByAiNum(aiEngineerInfoForm.getJobNum());
            //判斷工程綁定盒子
            List<OsdBindInfoForm> osdBindInfoFormList = aiEngineerInfoForm.getOsdBindInfoFormList();
            for (OsdBindInfoForm osdBindInfoForm : osdBindInfoFormList) {
                OsdBindInfoPO osdInfo = aiEngineerInfoMapper.selectByOsdNum(osdBindInfoForm.getOsdNum());
                if (osdInfo != null) {
                    throw new Exception("工程編號" + aiEngineerInfoForm.getJobNum() + "的盒子編號" + osdInfo.getOsdNum() + "已綁定...");
                } else {
                    OsdBindInfoPO osdBindInfo = new OsdBindInfoPO();
                    osdBindInfo.setOsdNum(osdBindInfoForm.getOsdNum());
                    osdBindInfo.setOsdName(osdBindInfoForm.getOsdName());
                    osdBindInfo.setBindType(0);
                    osdBindInfo.setBindNum(aiEngineerInfoForm.getJobNum());
                    aiEngineerInfoMapper.insertOsdInfo(osdBindInfo);
                }
            }
            //判斷工程綁定許可證
            List<PermitBindInfoForm> permitBindInfoFormList = aiEngineerInfoForm.getPermitBindInfoFormList();
            for (PermitBindInfoForm permitBindInfoForm : permitBindInfoFormList) {
                PermitBindInfoPO permitInfo = aiEngineerInfoMapper.selectByPermitNum(permitBindInfoForm.getPermitNum());
                if (permitInfo != null) {
                    throw new Exception("工程編號" + aiEngineerInfoForm.getJobNum() + "的許可證編號" + permitInfo.getPermitNum() + "已綁定...");
                } else {
                    PermitBindInfoPO permitBindInfo = new PermitBindInfoPO();
                    permitBindInfo.setPermitNum(permitBindInfoForm.getPermitNum());
                    permitBindInfo.setBindType(0);
                    permitBindInfo.setBindNum(aiEngineerInfoForm.getJobNum());
                    aiEngineerInfoMapper.insertPermitInfo(permitBindInfo);
                }
            }
            //判斷工程綁定功能
            List<FuncBindInfoForm> funcBindInfoFormList = aiEngineerInfoForm.getFuncBindInfoFormList();
            for (FuncBindInfoForm funcBindInfoForm : funcBindInfoFormList) {
                FuncBindInfoPO funcInfo = aiEngineerInfoMapper.selectByFuncNum(funcBindInfoForm.getFuncNum());
                if (funcInfo != null) {
                    throw new Exception("工程編號" + aiEngineerInfoForm.getJobNum() + "的功能編號" + funcInfo.getFuncNum() + "已綁定...");
                } else {
                    if (funcBindInfoForm.getFuncType() == 1) {
                        //判斷功能綁定盒子
                        List<OsdBindInfoForm> osdBindInfoFormListForFunc = funcBindInfoForm.getOsdBindInfoFormList();
                        for (OsdBindInfoForm osdBindInfoForm : osdBindInfoFormListForFunc) {
                            OsdBindInfoPO osdInfo = aiEngineerInfoMapper.selectByOsdNum(osdBindInfoForm.getOsdNum());
                            if (osdInfo != null) {
                                throw new Exception("功能編號" + funcBindInfoForm.getFuncNum() + "的盒子編號" + osdInfo.getOsdNum() + "已綁定...");
                            } else {
                                OsdBindInfoPO osdBindInfo = new OsdBindInfoPO();
                                osdBindInfo.setOsdNum(osdBindInfoForm.getOsdNum());
                                osdBindInfo.setOsdName(osdBindInfoForm.getOsdName());
                                osdBindInfo.setBindType(1);
                                osdBindInfo.setBindNum(funcBindInfoForm.getFuncNum());
                                aiEngineerInfoMapper.insertOsdInfo(osdBindInfo);
                            }
                        }
                    }
                    //判斷功能綁定許可證
                    List<PermitBindInfoForm> permitBindInfoFormListForFunc = funcBindInfoForm.getPermitBindInfoFormList();
                    for (PermitBindInfoForm permitBindInfoForm : permitBindInfoFormListForFunc) {
                        PermitBindInfoPO permitInfo = aiEngineerInfoMapper.selectByPermitNum(permitBindInfoForm.getPermitNum());
                        if (permitInfo != null) {
                            throw new Exception("功能編號" + funcBindInfoForm.getFuncNum() + "的許可證編號" + permitInfo.getPermitNum() + "已綁定...");
                        } else {
                            PermitBindInfoPO permitBindInfo = new PermitBindInfoPO();
                            permitBindInfo.setPermitNum(permitBindInfoForm.getPermitNum());
                            permitBindInfo.setBindType(1);
                            permitBindInfo.setBindNum(funcBindInfoForm.getFuncNum());
                            aiEngineerInfoMapper.insertPermitInfo(permitBindInfo);
                        }
                    }
                    List<ToolBindInfoForm> toolBindInfoFormList = funcBindInfoForm.getToolBindInfoFormList();
                    for (ToolBindInfoForm toolBindInfoForm : toolBindInfoFormList) {
                        ToolBindInfoPO toolInfo = new ToolBindInfoPO();
                        toolInfo.setTypeId(toolBindInfoForm.getTypeId());
                        toolInfo.setTypeName(toolBindInfoForm.getTypeName());
                        toolInfo.setCount(toolBindInfoForm.getCount());
                        toolInfo.setValid(toolBindInfoForm.getValid());
                        toolInfo.setFuncNum(funcBindInfoForm.getFuncNum());
                        aiEngineerInfoMapper.insertToolInfo(toolInfo);
                    }
                    FuncBindInfoPO funcBindInfo = new FuncBindInfoPO();
                    funcBindInfo.setFuncNum(funcBindInfoForm.getFuncNum());
                    funcBindInfo.setFuncName(funcBindInfoForm.getFuncName());
                    funcBindInfo.setFuncStatus(funcBindInfoForm.getFuncStatus());
                    funcBindInfo.setFuncType(funcBindInfoForm.getFuncType());
                    funcBindInfo.setStartTime(funcBindInfoForm.getStartTime());
                    funcBindInfo.setEndTime(funcBindInfoForm.getEndTime());
                    funcBindInfo.setJobNum(aiEngineerInfoForm.getJobNum());
                    aiEngineerInfoMapper.insertFuncInfo(funcBindInfo);
                }
            }
            List<WSWPInfoForm> wswpInfoFormList = aiEngineerInfoForm.getWswpInfoFormList();
            for (WSWPInfoForm wswpInfoForm : wswpInfoFormList) {
                Integer openCount = aiEngineerInfoMapper.getCpCountByCpNum(wswpInfoForm.getCpNum());
                Integer closeCount = aiEngineerInfoMapper.getCpCountByCpNumClose(wswpInfoForm.getCpNum());
                if (openCount != null && openCount > 3) {
                    throw new Exception("工程編號" + aiEngineerInfoForm.getJobNum() + "的CP編號" + wswpInfoForm.getCpNum() + "已綁定其他運行中工程3個以上");
                } else if (closeCount != null && closeCount > 10) {
                    throw new Exception("工程編號" + aiEngineerInfoForm.getJobNum() + "的CP編號" + wswpInfoForm.getCpNum() + "已綁定其他關閉中工程10個以上");
                } else {
                    WSWPInfoPO wswpBindInfo = new WSWPInfoPO();
                    wswpBindInfo.setCpNum(wswpInfoForm.getCpNum());
                    wswpBindInfo.setCaptainCount(wswpInfoForm.getCaptainCount());
                    wswpBindInfo.setJobNum(aiEngineerInfoForm.getJobNum());
                    aiEngineerInfoMapper.insertWswpInfo(wswpBindInfo);
                }
            }
            AiEngineerInfoPO aiEngineerInfo = aiEngineerInfoMapper.selectByAiNum(aiEngineerInfoForm.getJobNum());
            aiEngineerInfo.setJobName(aiEngineerInfoForm.getJobName());
            aiEngineerInfo.setStartTime(aiEngineerInfoForm.getStartTime());
            aiEngineerInfo.setEndTime(aiEngineerInfoForm.getEndTime());
            aiEngineerInfoMapper.updateAiEngineerInfo(aiEngineerInfo);
//            if (aiEngineerInfo.getSchedule() == 1) {
//                logger.info("sendJobMq jobNum:{},orgId:{},code:{}", aiEngineerInfo.getJobNum(), aiEngineerInfo.getOrgId(), MQCode.ENGINEER_RUN_UPDATE);
//                MQAllSendMessage.sendJobMq(aiEngineerInfo.getJobNum(), aiEngineerInfo.getOrgId(), MQCode.ENGINEER_RUN_UPDATE, apiServiceMq);
//            }
            returnMsg.setMsgbox("修改成功");
            returnMsg.setCode(ReturnMsg.SUCCESS);
        }
        return returnMsg;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ReturnMsg deleteEngineerMsg(String jobNum) {
        ReturnMsg returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        if (StringUtils.isEmpty(jobNum)) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //删除原先工程绑定盒子
            aiEngineerInfoMapper.deleteOsdByAiNum(jobNum);
            //删除原先工程绑定许可证
            aiEngineerInfoMapper.deletePermitByAiNum(jobNum);
            //删除原先工程绑定wswp
            aiEngineerInfoMapper.deleteWswpByAiNum(jobNum);
            //删除原先工程绑定功能
            List<FuncBindInfoPO> funcBindInfoList = aiEngineerInfoMapper.selectFuncByAiNum(jobNum);
            for (FuncBindInfoPO bindInfo : funcBindInfoList) {
                if (bindInfo.getFuncType() == 1) {
                    //删除原先工程功能绑定盒子
                    aiEngineerInfoMapper.deleteOsdByFuncNum(bindInfo.getFuncNum());
                }
                //删除原先工程功能绑定许可证
                aiEngineerInfoMapper.deletePermitByFuncNum(bindInfo.getFuncNum());
                //删除原先工程功能绑定工具
                aiEngineerInfoMapper.deleteToolByFuncNum(bindInfo.getFuncNum());
            }
            //删除工程
            aiEngineerInfoMapper.deleteByAiNum(jobNum);
            returnMsg.setMsgbox("刪除成功");
            returnMsg.setCode(ReturnMsg.SUCCESS);
        }
        return returnMsg;
    }

    @Override
    public ReturnMsg setJobRunStatus(String jobNum) {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        if (StringUtils.isEmpty(jobNum)) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            AiEngineerInfoPO aiEngineerInfo = aiEngineerInfoMapper.selectByAiNum(jobNum);
            if (aiEngineerInfo.getSchedule() == 1) {
                aiEngineerInfo.setSchedule(0);
                aiEngineerInfoMapper.updateAiEngineerInfo(aiEngineerInfo);
                logger.info("sendJobMq jobNum:{},orgId:{},code:{}", aiEngineerInfo.getJobNum(), aiEngineerInfo.getOrgId(), MQCode.ENGINEER_RUN_DELETE);
                MQAllSendMessage.sendJobMq(aiEngineerInfo.getJobNum(), aiEngineerInfo.getOrgId(), MQCode.ENGINEER_RUN_DELETE, apiServiceMq);
            } else {
                logger.info("sendJobMq jobNum:{},orgId:{},code:{}", aiEngineerInfo.getJobNum(), aiEngineerInfo.getOrgId(), MQCode.ENGINEER_RUN_ADD);
                aiEngineerInfo.setSchedule(1);
                aiEngineerInfoMapper.updateAiEngineerInfo(aiEngineerInfo);
                MQAllSendMessage.sendJobMq(aiEngineerInfo.getJobNum(), aiEngineerInfo.getOrgId(), MQCode.ENGINEER_RUN_ADD, apiServiceMq);
            }
            returnMsg.setMsgbox("成功");
            returnMsg.setCode(ReturnMsg.SUCCESS);
        }
        return returnMsg;
    }

    @Override
    public ReturnMsg getAllNum(String userid) {
        ReturnMsg returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        if (StringUtils.isEmpty(userid)) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //获取机构id
            String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
            List<String> allNum = aiEngineerInfoMapper.getAllNumByOrgId(orgid);
            returnMsg.setData(allNum);
            returnMsg.setCode(ReturnMsg.SUCCESS);
            returnMsg.setMsgbox("成功");
        }
        return returnMsg;
    }

    @Override
    public ReturnMsg getAllName(String userid) {
        ReturnMsg returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        if (StringUtils.isEmpty(userid)) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //获取机构id
            String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
            List<String> allName = aiEngineerInfoMapper.getAllNameByOrgId(orgid);
            returnMsg.setData(allName);
            returnMsg.setCode(ReturnMsg.SUCCESS);
            returnMsg.setMsgbox("成功");
        }
        return returnMsg;
    }

    @Override
    public void sendUpdateMq(AiEngineerInfoForm aiEngineerInfoForm) {
        AiEngineerInfoPO aiEngineerInfo = aiEngineerInfoMapper.selectByAiNum(aiEngineerInfoForm.getJobNum());
        if (aiEngineerInfo.getSchedule() == 1) {
            logger.info("sendJobMq jobNum:{},orgId:{},code:{}", aiEngineerInfo.getJobNum(), aiEngineerInfo.getOrgId(), MQCode.ENGINEER_RUN_UPDATE);
            MQAllSendMessage.sendJobMq(aiEngineerInfo.getJobNum(), aiEngineerInfo.getOrgId(), MQCode.ENGINEER_RUN_UPDATE, apiServiceMq);
        }
    }

    @Override
    public ReturnMsg getSurroundings(String jobNum) throws IOException {
        ReturnMsg returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        List<String> dtusn = aiEngineerInfoMapper.selectSurroundingsOsdIdListByAiNum(jobNum);
        List<String> noisesn = aiEngineerInfoMapper.selectNoiseOsdIdListByAiNum(jobNum);
        if ((dtusn != null && dtusn.size() > 0) || (noisesn != null && noisesn.size() > 0)) {
            Map<String, Object> param = new HashMap<>(2);
            param.put("dtusn", dtusn);
            param.put("noisesn", noisesn);
            String s = HttpUtils.sendPost(JSON.toJSONString(param), getSurroundingsUrl);
            if (!StringUtils.isEmpty(s)) {
                JSONObject jsonObject = JSON.parseObject(s);
                String code = jsonObject.getString("code");
                if ("200".equals(code)) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    String dtuvalues = data.getString("dtuvalues");
                    String noisevalues = data.getString("noisevalues");
                    Map<String, Object> map = new HashMap<>(2);
                    if (!StringUtils.isEmpty(dtuvalues)) {
                        List<SurroundingDto> dtoList = JSON.parseArray(dtuvalues, SurroundingDto.class);
                        map.put("dtuvalues", dtoList);
                    }
                    if (!StringUtils.isEmpty(noisevalues)) {
                        List<NoiseDTO> dtoList = JSON.parseArray(noisevalues, NoiseDTO.class);
                        map.put("noisevalues", dtoList);
                    }
                    returnMsg.setData(map);
                    returnMsg.setCode(ReturnMsg.SUCCESS);
                    returnMsg.setMsgbox("成功");
                }
            }
        } else {
            returnMsg.setCode(ReturnMsg.FAIL);
            returnMsg.setMsgbox("無環境數據！");
        }
        return returnMsg;
    }
}
