package com.tss.apiservice.service.impl;

import com.tss.apiservice.common.PageUtil;
import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.common.utils.MQAllSendMessage;
import com.tss.apiservice.common.utils.MQCode;
import com.tss.apiservice.dao.*;
import com.tss.apiservice.form.*;
import com.tss.apiservice.po.*;
import com.tss.apiservice.service.AiEngineerInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

/**
 * @author 壮Jone
 */
@Component
public class AiEngineerInfoServiceImpl implements AiEngineerInfoService {

    @Autowired
    private AiEngineerInfoMapper aiEngineerInfoMapper;

    @Autowired
    private UsersPoMapper usersPoMapper;

    @Override
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor= Exception.class)
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
                    Integer count = aiEngineerInfoMapper.getCpCountByCpNum(wswpInfoForm.getCpNum());
                    if (count != null && count > 3) {
                        throw new Exception("工程編號" + aiEngineerInfoForm.getJobNum() + "的CP編號" + wswpInfoForm.getCpNum() + "已綁定其他運行中工程3個以上");
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
                    for (FuncBindInfoForm funcBindInfoForm :funcBindInfoForms) {
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
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor= Exception.class)
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
                Integer count = aiEngineerInfoMapper.getCpCountByCpNum(wswpInfoForm.getCpNum());
                if (count != null && count > 3) {
                    throw new Exception("工程編號" + aiEngineerInfoForm.getJobNum() + "的CP編號" + wswpInfoForm.getCpNum() + "已綁定其他運行中工程3個以上");
                } else {
                    WSWPInfoPO wswpBindInfo = new WSWPInfoPO();
                    wswpBindInfo.setCpNum(wswpInfoForm.getCpNum());
                    wswpBindInfo.setCaptainCount(wswpInfoForm.getCaptainCount());
                    wswpBindInfo.setJobNum(aiEngineerInfoForm.getJobNum());
                    aiEngineerInfoMapper.insertWswpInfo(wswpBindInfo);
                }
            }
            AiEngineerInfoPO aiEngineer = new AiEngineerInfoPO();
            aiEngineer.setId(aiEngineerInfoForm.getId());
            aiEngineer.setJobName(aiEngineerInfoForm.getJobName());
            aiEngineer.setStartTime(aiEngineerInfoForm.getStartTime());
            aiEngineer.setEndTime(aiEngineerInfoForm.getEndTime());
            aiEngineerInfoMapper.updateAiEngineerInfo(aiEngineer);
            returnMsg.setMsgbox("修改成功");
            returnMsg.setCode(ReturnMsg.SUCCESS);
        }
        return returnMsg;
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor= Exception.class)
    public ReturnMsg deleteEngineerMsg(String jobNum) throws Exception{
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
            if ("1".equals(aiEngineerInfo.getSchedule())) {
                aiEngineerInfo.setSchedule("0");
            } else {
                aiEngineerInfo.setSchedule("1");
            }
            aiEngineerInfoMapper.updateAiEngineerInfo(aiEngineerInfo);
            returnMsg.setMsgbox("成功");
            returnMsg.setCode(ReturnMsg.SUCCESS);
        }
        return returnMsg;
    }
}
