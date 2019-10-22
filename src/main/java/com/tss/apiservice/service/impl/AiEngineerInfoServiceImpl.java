package com.tss.apiservice.service.impl;

import com.tss.apiservice.common.ReturnMsg;
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
import java.util.List;

/**
 * @author 壮Jone
 */
@Component
public class AiEngineerInfoServiceImpl implements AiEngineerInfoService {

    @Autowired
    private AiEngineerInfoMapper aiEngineerInfoMapper;

    @Autowired
    private PermitsPoMapper permitsPoMapper;

    @Autowired
    private ToolsPoMapper toolsPoMapper;

    @Autowired
    private OSDInfosPoMapper osdInfosPoMapper;

    @Autowired
    private UsersPoMapper usersPoMapper;

    @Override
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor= Exception.class)
    public ReturnMsg addEngineerMsg(String userid, AiEngineerInfoForm aiEngineerInfoForm) throws Exception {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        if (StringUtils.isEmpty(userid)) {
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
                List<FuncBindInfoForm> funcBindInfoFormList = aiEngineerInfoForm.getFuncBindInfoFormList();
                for (FuncBindInfoForm funcBindInfoForm : funcBindInfoFormList) {
                    //判斷工程綁定功能
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
                            toolInfo.setFuncNum(funcBindInfoForm.getFuncNum());
                            aiEngineerInfoMapper.insertToolInfo(toolInfo);
                        }
                    }

                }
                List<WSWPInfoForm> wswpInfoFormList = aiEngineerInfoForm.getWswpInfoFormList();
                for (WSWPInfoForm wswpInfoForm : wswpInfoFormList) {
                    WSWPInfoPO wswpInfo = new WSWPInfoPO();
                    wswpInfo.setCpNum(wswpInfoForm.getCpNum());
                    wswpInfo.setCaptainCount(wswpInfoForm.getCaptainCount());
                    wswpInfo.setJobNum(aiEngineerInfoForm.getJobNum());
                    aiEngineerInfoMapper.insertWSWPInfo(wswpInfo);
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
        return null;
    }

    @Override
    public ReturnMsg updateEngineerMsg(String userid, AiEngineerInfoForm aiEngineerInfoForm) {
        return null;
    }

    @Override
    public ReturnMsg deleteEngineerMsg(String userid, AiEngineerInfoForm aiEngineerInfoForm) {
        return null;
    }
}
