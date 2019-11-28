package com.tss.apiservice.service.impl;

import com.tss.apiservice.common.PageUtil;
import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.common.utils.MQAllSendMessage;
import com.tss.apiservice.common.utils.MQCode;
import com.tss.apiservice.dao.EngineerinfoPoMapper;
import com.tss.apiservice.dao.ObjsconditionPoMapper;
import com.tss.apiservice.dao.SafeobjsPoMapper;
import com.tss.apiservice.dao.UsersPoMapper;
import com.tss.apiservice.dto.JobDto;
import com.tss.apiservice.dto.SafeobjsDto;
import com.tss.apiservice.po.EngineerinfoPo;
import com.tss.apiservice.po.ObjsconditionPo;
import com.tss.apiservice.po.SafeobjsPo;
import com.tss.apiservice.po.UsersPo;
import com.tss.apiservice.service.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class JobServiceImpl implements JobService {
    private static Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);
    @Autowired
    EngineerinfoPoMapper engineerinfoPoMapper;

    @Autowired
    SafeobjsPoMapper safeobjsPoMapper;

    @Autowired
    ObjsconditionPoMapper objsconditionPoMapper;

    @Autowired
    ApiServiceMQImpl apiServiceMQ;

    @Autowired
    UsersPoMapper usersPoMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ReturnMsg addJobMsg(String userid, JobDto jobDto) throws ParseException {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");

        if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(jobDto.getOsdid())) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            EngineerinfoPo engineerinfoPo = engineerinfoPoMapper.selectByOsdid(jobDto.getOsdid());
            if (engineerinfoPo != null) {
                returnMsg.setMsgbox("盒子編號已綁定...");
            } else {
                //开始保存工程
                engineerinfoPo = new EngineerinfoPo();
                if (StringUtils.isEmpty(jobDto.getJobnum())) {
                    returnMsg.setMsgbox("工程編號為空!");
                    return returnMsg;
                } else {
                    engineerinfoPo.setJobnum(jobDto.getJobnum());
                }
                engineerinfoPo.setOsdid(jobDto.getOsdid());
                engineerinfoPo.setOsdname(jobDto.getOsdname());
                if (!StringUtils.isEmpty(jobDto.getStartTime())) {
                    engineerinfoPo.setStarttime(jobDto.getStartTime());
                }
                if (!StringUtils.isEmpty(jobDto.getEndTime())) {
                    engineerinfoPo.setEndtime(jobDto.getEndTime());
                }
                engineerinfoPo.setName(jobDto.getName());

                //企业编码
                UsersPo usersPo = usersPoMapper.selectByPrimaryKey(Integer.valueOf(userid));
//                if (usersPo.getLevel() == 1) {
//                    engineerinfoPo.setUserid(Integer.valueOf(userid));
//                } else {
//                    engineerinfoPo.setUserid(usersPo.getSuperiorid());
//                }
                //获取机构id
                //String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
                engineerinfoPo.setOrgid(usersPo.getOrgid());

                //保存安全对象信息,分别为员工，许可证，工具
                ArrayList<SafeobjsPo> list = new ArrayList<>();
                SafeobjsPo safeobjsPo;
                Map<String, List<Map<String, String>>> permits = jobDto.getPermits();
                List<Map<String, String>> permitsArr = permits.get("permitsArr");
                String objnum;
                String objname;
                String isleave;

                for (Map<String, String> stringMap : permitsArr) {
                    objnum = stringMap.get("objnum");
                    objname = stringMap.get("objname");
                    isleave = stringMap.get("isleave");
                    safeobjsPo = safeobjsPoMapper.selectByPrimaryKey(objnum, 0);
                    if (safeobjsPo != null) {
                        returnMsg.setMsgbox("許可證 {" + objname + "} 已在工程中使用...");
                        return returnMsg;
                    }
                    safeobjsPo = new SafeobjsPo();
                    safeobjsPo.setJobnum(jobDto.getJobnum());
                    safeobjsPo.setObjnum(objnum);
                    safeobjsPo.setIsleave(Integer.valueOf(isleave));
                    safeobjsPo.setOsdid(jobDto.getOsdid());
                    safeobjsPo.setType(0);
                    safeobjsPo.setObjname(objname);
                    list.add(safeobjsPo);
                }

                Map<String, List<Map<String, String>>> tools = jobDto.getTools();
                List<Map<String, String>> toolsArr = tools.get("toolsArr");
                for (Map<String, String> stringStringMap : toolsArr) {
                    objnum = stringStringMap.get("objnum");
                    objname = stringStringMap.get("objname");
                    isleave = stringStringMap.get("isleave");
                    safeobjsPo = safeobjsPoMapper.selectByPrimaryKey(objnum, 2);
                    if (safeobjsPo != null) {
                        returnMsg.setMsgbox("工具 {" + objname + "} 已在工程中使用...");
                        return returnMsg;
                    }
                    safeobjsPo = new SafeobjsPo();

                    safeobjsPo.setJobnum(jobDto.getJobnum());
                    safeobjsPo.setObjnum(objnum);
                    safeobjsPo.setIsleave(Integer.valueOf(isleave));
                    safeobjsPo.setOsdid(jobDto.getOsdid());
                    safeobjsPo.setType(2);
                    safeobjsPo.setObjname(objname);
                    list.add(safeobjsPo);
                }

                Map<String, List<Map<String, Object>>> staffs = jobDto.getStaffs();
                List<Map<String, Object>> staffsArr = staffs.get("staffsArr");

                List objsConditionArr;
                String permitname;
                String permittypeid;
                Map<String, String> map;

                ObjsconditionPo objsconditionPo;
                ArrayList<ObjsconditionPo> ObjsconditionPoList = new ArrayList<>();
                for (Map<String, Object> stringObjectMap : staffsArr) {
                    objnum = stringObjectMap.get("objnum").toString();
                    objname = stringObjectMap.get("objname").toString();
                    isleave = stringObjectMap.get("isleave").toString();
                    safeobjsPo = safeobjsPoMapper.selectByPrimaryKey(objnum, 1);
                    if (safeobjsPo != null) {
                        returnMsg.setMsgbox("員工 {" + objname + "} 已在工程中使用...");
                        return returnMsg;
                    }
                    safeobjsPo = new SafeobjsPo();

                    safeobjsPo.setJobnum(jobDto.getJobnum());
                    safeobjsPo.setObjnum(objnum);
                    safeobjsPo.setOsdid(jobDto.getOsdid());
                    safeobjsPo.setType(1);
                    safeobjsPo.setIsleave(Integer.valueOf(isleave));
                    safeobjsPo.setObjname(objname);
                    list.add(safeobjsPo);

                    objsConditionArr = (List) stringObjectMap.get("objsConditionArr");
                    for (Object o : objsConditionArr) {
                        objsconditionPo = new ObjsconditionPo();
                        map = (Map<String, String>) o;
                        permitname = map.get("permitname");
                        permittypeid = map.get("permittypeid");
                        objsconditionPo.setJobnum(jobDto.getJobnum());
                        objsconditionPo.setStaffid(objnum);
                        objsconditionPo.setOsdid(jobDto.getOsdid());
                        objsconditionPo.setPermitname(permitname);
                        objsconditionPo.setPermittypeid(Integer.parseInt(permittypeid));
                        ObjsconditionPoList.add(objsconditionPo);
                    }
                }

                //插入所有数据
                engineerinfoPoMapper.insertSelective(engineerinfoPo);
                for (SafeobjsPo value : list) {
                    safeobjsPoMapper.insertSelective(value);
                }

                for (ObjsconditionPo po : ObjsconditionPoList) {
                    objsconditionPoMapper.insertSelective(po);
                }

                returnMsg.setMsgbox("成功");
                returnMsg.setCode(ReturnMsg.SUCCESS);
            }

        }
        return returnMsg;
    }

    @Override
    public ReturnMsg getJobList(HttpServletRequest request) {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        String userid = request.getParameter("userid");
        String pageSize = request.getParameter("pageSize");
        String currentPage = request.getParameter("currentPage");
        String numberBegin = request.getParameter("numberBegin");
        String numberEnd = request.getParameter("numberEnd");
        String name = request.getParameter("name");

        if (StringUtils.isEmpty(userid)) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //企业编码
            UsersPo usersPo = usersPoMapper.selectByPrimaryKey(Integer.valueOf(userid));
//            if (usersPo.getLevel() == 1) {
//
//            } else {
//                userid = usersPo.getSuperiorid().toString();
//            }

            //查看许可证列表，根据许可证查找标签
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("orgid", usersPo.getOrgid());
            if (!StringUtils.isEmpty(numberBegin)) {
                hashMap.put("numberBegin", numberBegin);
            }
            if (!StringUtils.isEmpty(numberEnd)) {
                hashMap.put("numberEnd", numberEnd);
            }
            if (!StringUtils.isEmpty(name)) {
                hashMap.put("name", name);
            }
            //根据带有符合条件的总数，进行分页查询的操作
            List<EngineerinfoPo> engineerinfoPos = engineerinfoPoMapper.selectListByMap(hashMap);

            if (StringUtils.isEmpty(pageSize)) {
                pageSize = "10";
            }
            if (StringUtils.isEmpty(currentPage)) {
                currentPage = "1";
            }
            if (engineerinfoPos.size() < 1) {
                returnMsg.setMsgbox("找不到符合條件數據...");
            } else {
                PageUtil pageUtil = new PageUtil(engineerinfoPos.size(), Integer.parseInt(pageSize), Integer.parseInt(currentPage), 5);
                int startrow = pageUtil.getStartrow();
                int pagesize = pageUtil.getPagesize();
                hashMap.put("startrow", startrow);
                hashMap.put("pagesize", pagesize);
                List<EngineerinfoPo> engineerinfoPosList = engineerinfoPoMapper.selectListByMap(hashMap);
                HashMap<String, Object> retMap;
                ArrayList<Object> arrayList = new ArrayList<>();
                //每一个list还要获取对应的，安全对象和安全对象条件
                List<SafeobjsPo> safeobjsPoList;
                List<ObjsconditionPo> objsconditionPoList;
                for (EngineerinfoPo engineerinfoPo : engineerinfoPosList) {
                    retMap = new HashMap<>();
                    retMap.put("engineerinfoPo", engineerinfoPo);
                    safeobjsPoList = safeobjsPoMapper.selectByOsdid(engineerinfoPo.getOsdid());
                    retMap.put("safeobjsPoList", safeobjsPoList);
                    objsconditionPoList = objsconditionPoMapper.selectByOsdid(engineerinfoPo.getOsdid());
                    retMap.put("objsconditionPoList", objsconditionPoList);
                    arrayList.add(retMap);
                }
                pageUtil.setPageData(arrayList);
                returnMsg = new ReturnMsg<>(ReturnMsg.SUCCESS, "成功", pageUtil);
            }
        }
        return returnMsg;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ReturnMsg updateJobMsg(String userid, JobDto jobDto) throws ParseException {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");

        if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(jobDto.getOsdid())) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //EngineerinfoPo engineerinfoPoOld = engineerinfoPoMapper.selectByOsdid(jobDto.getOsdid());
            //engineerinfoPoMapper.deleteByOsdid(jobDto.getOsdid());
            //safeobjsPoMapper.deleteByOsdid(jobDto.getOsdid());
            //objsconditionPoMapper.deleteByOsdid(jobDto.getOsdid());
            EngineerinfoPo engineerinfoPoOld = engineerinfoPoMapper.selectByPrimaryKey(jobDto.getId());
            if (StringUtils.isEmpty(engineerinfoPoOld.getJobnum())) {
                returnMsg.setMsgbox("工程編號為空!");
                return returnMsg;
            }
            EngineerinfoPo engineerinfoPoNew = engineerinfoPoMapper.selectByOsdid(jobDto.getOsdid());
            if (engineerinfoPoNew != null && !engineerinfoPoNew.getId().equals(jobDto.getId())) {
                returnMsg.setMsgbox("盒子編號已綁定...");
            } else {
                //开始保存工程
                EngineerinfoPo engineerinfoPo = new EngineerinfoPo();
                engineerinfoPo.setSchedule(engineerinfoPoOld.getSchedule());
                engineerinfoPo.setJobnum(jobDto.getJobnum());
                engineerinfoPo.setOsdid(jobDto.getOsdid());
                engineerinfoPo.setOsdname(jobDto.getOsdname());
                if (!StringUtils.isEmpty(jobDto.getStartTime())) {
                    engineerinfoPo.setStarttime(jobDto.getStartTime());
                }
                if (!StringUtils.isEmpty(jobDto.getEndTime())) {
                    engineerinfoPo.setEndtime(jobDto.getEndTime());
                }
                engineerinfoPo.setName(jobDto.getName());

                //企业编码
                UsersPo usersPo = usersPoMapper.selectByPrimaryKey(Integer.valueOf(userid));
                engineerinfoPo.setOrgid(usersPo.getOrgid());
                //保存安全对象信息,分别为员工，许可证，工具
                ArrayList<SafeobjsPo> list = new ArrayList<>();
                SafeobjsPo safeobjsPo = null;
                Map<String, List<Map<String, String>>> permits = jobDto.getPermits();
                List<Map<String, String>> permitsArr = permits.get("permitsArr");
                String objnum;
                String objname;
                String isleave;

                for (Map<String, String> stringMap : permitsArr) {
                    objnum = stringMap.get("objnum");
                    objname = stringMap.get("objname");
                    isleave = stringMap.get("isleave");
                    safeobjsPo = safeobjsPoMapper.selectByPrimaryKey(objnum, 0);
                    if (safeobjsPo != null) {
                        if (StringUtils.isEmpty(safeobjsPo.getJobnum())) {
                            safeobjsPo.setIsleave(Integer.valueOf(isleave));
                            list.add(safeobjsPo);
                        } else {
                            if (StringUtils.isEmpty(engineerinfoPoOld.getJobnum())) {
                                safeobjsPo.setIsleave(Integer.valueOf(isleave));
                                list.add(safeobjsPo);
                            } else {
                                if (engineerinfoPoOld.getJobnum().equals(safeobjsPo.getJobnum())) {
                                    safeobjsPo.setIsleave(Integer.valueOf(isleave));
                                    list.add(safeobjsPo);
                                } else {
                                    returnMsg.setMsgbox("許可證 {" + objname + "} 已在工程中使用...");
                                    return returnMsg;
                                }
                            }
                        }
                    } else {
                        safeobjsPo = new SafeobjsPo();
                        safeobjsPo.setJobnum(jobDto.getJobnum());
                        safeobjsPo.setObjnum(objnum);
                        safeobjsPo.setOsdid(jobDto.getOsdid());
                        safeobjsPo.setType(0);
                        safeobjsPo.setIsleave(Integer.valueOf(isleave));
                        safeobjsPo.setObjname(objname);
                        list.add(safeobjsPo);
                    }
                }

                Map<String, List<Map<String, String>>> tools = jobDto.getTools();
                List<Map<String, String>> toolsArr = tools.get("toolsArr");
                for (Map<String, String> stringStringMap : toolsArr) {
                    objnum = stringStringMap.get("objnum");
                    objname = stringStringMap.get("objname");
                    isleave = stringStringMap.get("isleave");
                    safeobjsPo = safeobjsPoMapper.selectByPrimaryKey(objnum, 2);
                    if (safeobjsPo != null) {
                        if (StringUtils.isEmpty(safeobjsPo.getJobnum())) {
                            safeobjsPo.setIsleave(Integer.valueOf(isleave));
                            list.add(safeobjsPo);
                        } else {
                            if (StringUtils.isEmpty(engineerinfoPoOld.getJobnum())) {
                                safeobjsPo.setIsleave(Integer.valueOf(isleave));
                                list.add(safeobjsPo);
                            } else {
                                if (engineerinfoPoOld.getJobnum().equals(safeobjsPo.getJobnum())) {
                                    safeobjsPo.setIsleave(Integer.valueOf(isleave));
                                    list.add(safeobjsPo);
                                } else {
                                    returnMsg.setMsgbox("工具 {" + objname + "} 已在工程中使用...");
                                    return returnMsg;
                                }
                            }
                        }
                    } else {
                        safeobjsPo = new SafeobjsPo();
                        safeobjsPo.setJobnum(jobDto.getJobnum());
                        safeobjsPo.setObjnum(objnum);
                        safeobjsPo.setOsdid(jobDto.getOsdid());
                        safeobjsPo.setType(2);
                        safeobjsPo.setIsleave(Integer.valueOf(isleave));
                        safeobjsPo.setObjname(objname);
                        list.add(safeobjsPo);
                    }
                }
                Map<String, List<Map<String, Object>>> staffs = jobDto.getStaffs();
                List<Map<String, Object>> staffsArr = staffs.get("staffsArr");

                List objsConditionArr;
                String permitname;
                Map<String, String> map;

                ObjsconditionPo objsconditionPo;
                ArrayList<ObjsconditionPo> ObjsconditionPoList = new ArrayList<>();
                for (Map<String, Object> stringObjectMap : staffsArr) {
                    objnum = stringObjectMap.get("objnum").toString();
                    objname = stringObjectMap.get("objname").toString();
                    isleave = stringObjectMap.get("isleave").toString();
                    safeobjsPo = safeobjsPoMapper.selectByPrimaryKey(objnum, 1);
                    if (safeobjsPo != null) {
                        if (StringUtils.isEmpty(safeobjsPo.getJobnum())) {
                            safeobjsPo.setIsleave(Integer.valueOf(isleave));
                            list.add(safeobjsPo);
                        } else {
                            if (StringUtils.isEmpty(engineerinfoPoOld.getJobnum())) {
                                safeobjsPo.setIsleave(Integer.valueOf(isleave));
                                list.add(safeobjsPo);
                            } else {
                                if (engineerinfoPoOld.getJobnum().equals(safeobjsPo.getJobnum())) {
                                    safeobjsPo.setIsleave(Integer.valueOf(isleave));
                                    list.add(safeobjsPo);
                                } else {
                                    returnMsg.setMsgbox("員工 {" + objname + "} 已在工程中使用...");
                                    return returnMsg;
                                }
                            }
                        }
                    } else {
                        safeobjsPo = new SafeobjsPo();
                        safeobjsPo.setJobnum(jobDto.getJobnum());
                        safeobjsPo.setObjnum(objnum);
                        safeobjsPo.setOsdid(jobDto.getOsdid());
                        safeobjsPo.setType(1);
                        safeobjsPo.setIsleave(Integer.valueOf(isleave));
                        safeobjsPo.setObjname(objname);
                        list.add(safeobjsPo);
                    }
                    objsConditionArr = (List) stringObjectMap.get("objsConditionArr");
                    for (Object o : objsConditionArr) {
                        objsconditionPo = new ObjsconditionPo();
                        map = (Map<String, String>) o;

                        permitname = map.get("permitname");

                        objsconditionPo.setJobnum(jobDto.getJobnum());
                        objsconditionPo.setStaffid(objnum);
                        objsconditionPo.setOsdid(jobDto.getOsdid());
                        objsconditionPo.setPermitname(permitname);
                        ObjsconditionPoList.add(objsconditionPo);
                    }
                }

                engineerinfoPoMapper.deleteByOsdid(jobDto.getOsdid());
                safeobjsPoMapper.deleteByOsdid(jobDto.getOsdid());
                objsconditionPoMapper.deleteByOsdid(jobDto.getOsdid());
                //插入所有数据
                //engineerinfoPo.setOrgid(engineerinfoPoOld.getOrgid());
                engineerinfoPoMapper.insertSelective(engineerinfoPo);
                for (SafeobjsPo value : list) {
                    safeobjsPoMapper.insertSelective(value);
                }

                for (ObjsconditionPo po : ObjsconditionPoList) {
                    objsconditionPoMapper.insertSelective(po);
                }

                returnMsg.setMsgbox("成功");
                returnMsg.setCode(ReturnMsg.SUCCESS);
                if (engineerinfoPoOld.getSchedule() == 1) {
                    logger.info("sendJobMq jobNum:{},osdId:{},code:{}", engineerinfoPo.getJobnum(), jobDto.getOsdid(), MQCode.JOB_RUN_UPDATE);
                    MQAllSendMessage.sendJobMq(engineerinfoPo.getJobnum(), jobDto.getOsdid(), MQCode.JOB_RUN_UPDATE, apiServiceMQ);
                }
            }

        }
        return returnMsg;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ReturnMsg deleteJobMsg(String userid, JobDto jobDto) {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");

        if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(jobDto.getOsdid())) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            EngineerinfoPo engineerinfoPo = engineerinfoPoMapper.selectByOsdid(jobDto.getOsdid());
            engineerinfoPoMapper.deleteByOsdid(jobDto.getOsdid());
            safeobjsPoMapper.deleteByOsdid(jobDto.getOsdid());
            objsconditionPoMapper.deleteByOsdid(jobDto.getOsdid());

            if (engineerinfoPo.getSchedule() == 1) {
                logger.info("sendJobMq jobNum:{},osdId:{},code:{}", engineerinfoPo.getJobnum(), jobDto.getOsdid(), MQCode.JOB_RUN_DELETE);
                MQAllSendMessage.sendJobMq(engineerinfoPo.getJobnum(), jobDto.getOsdid(), MQCode.JOB_RUN_DELETE, apiServiceMQ);
            }

            returnMsg.setMsgbox("成功");
            returnMsg.setCode(ReturnMsg.SUCCESS);

        }
        return returnMsg;
    }

    @Override
    public ReturnMsg setJobRunStatus(String userid, JobDto jobDto) {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");

        if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(jobDto.getOsdid())) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            EngineerinfoPo engineerinfoPo = engineerinfoPoMapper.selectByOsdid(jobDto.getOsdid());
            if (jobDto.isRunStatus()) {
                engineerinfoPo.setSchedule(1);
                logger.info("sendJobMq jobNum:{},osdId:{},code:{}", engineerinfoPo.getJobnum(), jobDto.getOsdid(), MQCode.JOB_RUN_ADD);
                MQAllSendMessage.sendJobMq(engineerinfoPo.getJobnum(), jobDto.getOsdid(), MQCode.JOB_RUN_ADD, apiServiceMQ);
            } else {
                engineerinfoPo.setSchedule(0);
                logger.info("sendJobMq jobNum:{},osdId:{},code:{}", engineerinfoPo.getJobnum(), jobDto.getOsdid(), MQCode.JOB_RUN_DELETE);
                MQAllSendMessage.sendJobMq(engineerinfoPo.getJobnum(), jobDto.getOsdid(), MQCode.JOB_RUN_DELETE, apiServiceMQ);
            }
            engineerinfoPoMapper.updateByPrimaryKeySelective(engineerinfoPo);
            returnMsg.setMsgbox("成功");
            returnMsg.setCode(ReturnMsg.SUCCESS);
        }
        return returnMsg;
    }

    @Override
    public ReturnMsg setJobObjLeavtime(String userid, SafeobjsDto safeobjsDto) throws ParseException {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");

        if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(safeobjsDto.getObjnum()) || StringUtils.isEmpty(safeobjsDto.getLeavetime())) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            SafeobjsPo safeobjsPo = safeobjsPoMapper.selectByPrimaryKey(safeobjsDto.getObjnum(),null);
            if (safeobjsPo == null || safeobjsPo.getIsleave() == 1) {
                // 0 不允许离开，才可以进行授权，1是可以离开，不用授权
                returnMsg.setMsgbox("安全對象可離開，不用授權。...");
            } else {
                Date leavetime;
                leavetime = new Date(System.currentTimeMillis() + Integer.parseInt(safeobjsDto.getLeavetime()) * 60000);

                safeobjsPo.setLeavetime(leavetime);
                String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(leavetime);
                safeobjsPoMapper.updateByPrimaryKeySelective(safeobjsPo);
                //推送消息到MQ
                logger.info("sendJobObjLeavtime userid:{},format:{},safeobjsPo:{},code:{}", userid, format, safeobjsPo, MQCode.JOB_OBJ_UPDATE);
                MQAllSendMessage.sendJobObjLeavtime(userid, format, safeobjsPo, MQCode.JOB_OBJ_UPDATE, apiServiceMQ);
                returnMsg.setMsgbox("成功");
                returnMsg.setCode(ReturnMsg.SUCCESS);
            }
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
            List<String> allNum = engineerinfoPoMapper.getAllNumByOrgId(orgid);
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
            List<String> allName = engineerinfoPoMapper.getAllNameByOrgId(orgid);
            returnMsg.setData(allName);
            returnMsg.setCode(ReturnMsg.SUCCESS);
            returnMsg.setMsgbox("成功");
        }
        return returnMsg;
    }
}
