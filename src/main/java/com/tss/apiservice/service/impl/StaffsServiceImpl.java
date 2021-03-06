package com.tss.apiservice.service.impl;

import com.tss.apiservice.common.PageUtil;
import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.common.utils.FilesUtils;
import com.tss.apiservice.common.utils.MQAllSendMessage;
import com.tss.apiservice.common.utils.MQCode;
import com.tss.apiservice.controller.StaffsController;
import com.tss.apiservice.dao.*;
import com.tss.apiservice.dto.StaffsDto;
import com.tss.apiservice.po.*;
import com.tss.apiservice.service.StaffsService;
import com.tss.apiservice.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.xml.crypto.Data;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class StaffsServiceImpl implements StaffsService {
    private static Logger logger = LoggerFactory.getLogger(StaffsServiceImpl.class);

    @Autowired
    StaffsPoMapper staffsPoMapper;

    @Autowired
    TagInfosPoMapper tagInfosPoMapper;

    @Autowired
    StaffscertPoMapper staffscertPoMapper;

    @Value("${filePath}")
    private String filePath;

    @Autowired
    PermitsconditionPoMapper permitsconditionPoMapper;

    @Autowired
    SafeobjsPoMapper safeobjsPoMapper;

    @Autowired
    EngineerinfoPoMapper engineerinfoPoMapper;

    @Autowired
    ObjsconditionPoMapper objsconditionPoMapper;

    @Autowired
    ApiServiceMQImpl apiServiceMQ;

    @Autowired
    UsersPoMapper usersPoMapper;

    @Autowired
    AbnormalPoMapper abnormalPoMapper;

    @Autowired
    ToolsPoMapper toolsPoMapper;

    @Autowired
    PermitsPoMapper permitsPoMapper;

    @Autowired
    private AiEngineerInfoMapper aiEngineerInfoMapper;

    @Override
    @Transactional
    public ReturnMsg addStaffsMsg(String userid, StaffsDto staffsDto, List<StaffsImagePO> staffsImageList) {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(staffsDto.getStaffid()) ||
                StringUtils.isEmpty(staffsDto.getEnname())) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            StaffsPo ret = staffsPoMapper.selectByPrimaryKey(staffsDto.getStaffid());
            //获取机构id
            String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
            Map map;
            if (ret == null) {
                //添加标签信息表
                List<Map<String, String>> tag = staffsDto.getTag();
                List<TagInfosPo> tagInfosPoList = new ArrayList<>();
                boolean tagExist = true;
                for (Map<String, String> stringStringMap : tag) {
                    TagInfosPo tagInfosPo = new TagInfosPo();
                    map = stringStringMap;
                    tagInfosPo.setTagid(map.get("tagCode").toString());
                    TagInfosPo tagInfo = tagInfosPoMapper.selectByPrimaryKey(tagInfosPo.getTagid());
                    if (tagInfo == null) {
                        tagInfosPo.setType(1);
                        tagInfosPo.setObjnum(staffsDto.getStaffid());
                        tagInfosPoList.add(tagInfosPo);
                    } else {
                        tagExist = false;
                        Integer type = tagInfo.getType();
                        String number = tagInfo.getObjnum();
                        if (type == 0) {
                            PermitsPo permitsPo = permitsPoMapper.selectByPrimaryKey(number);
                            if (orgid.equals(permitsPo.getOrgid())) {
                                returnMsg.setMsgbox("許可證編號:" + permitsPo.getPermitid() + "正在使用標簽");
                            } else {
                                returnMsg.setMsgbox("標簽已存在其他機構...");
                            }
                        } else if (type == 1) {
                            StaffsPo staffsPo = staffsPoMapper.selectByPrimaryKey(number);
                            if (orgid.equals(staffsPo.getOrgid())) {
                                returnMsg.setMsgbox("員工編號:" + staffsPo.getStaffid() + "正在使用標簽");
                            } else {
                                returnMsg.setMsgbox("標簽已存在其他機構...");
                            }
                        } else if (type == 2) {
                            ToolsPo toolsPo = toolsPoMapper.selectByPrimaryKey(number);
                            if (orgid.equals(toolsPo.getOrgid())) {
                                returnMsg.setMsgbox("工具編號:" + toolsPo.getToolid() + "正在使用標簽");
                            } else {
                                returnMsg.setMsgbox("標簽已存在其他機構...");
                            }
                        }
                        break;
                    }
                }

                List<Map<String, String>> carDataArr = staffsDto.getCarDataArr();
                List<StaffscertPo> staffscertPoList = new ArrayList<>();
                if (carDataArr != null && carDataArr.size() > 0) {
                    for (Map<String, String> stringStringMap : carDataArr) {
                        StaffscertPo staffscertPo = new StaffscertPo();
                        map = stringStringMap;
                        staffscertPo.setStaffid(staffsDto.getStaffid());
                        String certid = map.get("certid").toString();
                        StaffscertPo staffscertPoTmp = staffscertPoMapper.selectByPrimaryKey(certid);
                        if (staffscertPoTmp != null) {
                            returnMsg.setMsgbox(certid + ": 證件編號已存在...");
                            tagExist = false;
                            break;
                        }
                        String typeid = map.get("typeid").toString();
                        String validity = map.get("validity").toString();
                        if (StringUtils.isEmpty(certid) || StringUtils.isEmpty(typeid) || StringUtils.isEmpty(validity)) {
                            returnMsg.setMsgbox("證件信息存在空值...");
                            tagExist = false;
                            break;
                        } else {
                            staffscertPo.setCertid(certid);
                            staffscertPo.setTypeid(Integer.valueOf(typeid));
                            staffscertPo.setValidity(validity);
                            staffscertPoList.add(staffscertPo);
                        }
                    }
                }

                if (tagExist) {
                    for (TagInfosPo tagInfosPo : tagInfosPoList) {
                        tagInfosPoMapper.insertSelective(tagInfosPo);
                    }
                    //添加员工信息表
                    StaffsPo staffsPo = new StaffsPo();
                    staffsPo.setStaffid(staffsDto.getStaffid());
                    staffsPo.setChname(staffsDto.getChname());
                    staffsPo.setEnname(staffsDto.getEnname());
                    staffsPo.setOrgid(orgid);
                    staffsPo.setTreatment(staffsDto.getTreatment());
                    staffsPo.setEffdate(staffsDto.getEffdate());
                    staffsPo.setAltersalary(staffsDto.getAltersalary());
                    staffsPoMapper.insertSelective(staffsPo);
                    //添加员工证件表
                    for (StaffscertPo staffscertPo : staffscertPoList) {
                        staffscertPoMapper.insertSelective(staffscertPo);
                    }
                    for (StaffsImagePO staffsImage : staffsImageList) {
                        staffsPoMapper.insertStaffsImage(staffsImage);
                    }
                    returnMsg = new ReturnMsg<>(ReturnMsg.SUCCESS, "成功");
                }
            } else {
                returnMsg.setMsgbox("員工已存在...");
            }
        }
        return returnMsg;
    }

    @Override
    public ReturnMsg getStaffsMsgList(HttpServletRequest request) throws Exception {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        String userid = request.getParameter("userid");
        String pageSize = request.getParameter("pageSize");
        String currentPage = request.getParameter("currentPage");
        String numberBegin = request.getParameter("numberBegin");
        String numberEnd = request.getParameter("numberEnd");
        String chname = request.getParameter("chname");
        String enname = request.getParameter("enname");
        String expire = request.getParameter("expire");
        String expireNumber = request.getParameter("expireNumber");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        if (StringUtils.isEmpty(userid)) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //获取机构id
            String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
            //查看员工列表，根据员工查找标签，查找员工证件
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("orgid", orgid);
            if (!StringUtils.isEmpty(numberBegin)) {
                hashMap.put("numberBegin", numberBegin);
            }
            if (!StringUtils.isEmpty(numberEnd)) {
                hashMap.put("numberEnd", numberEnd);
            }
            if (!StringUtils.isEmpty(chname)) {
                hashMap.put("chname", chname);
            }
            if (!StringUtils.isEmpty(enname)) {
                hashMap.put("enname", enname);
            }
            //根据带有符合条件的总数，进行分页查询的操作
            if (!StringUtils.isEmpty(expire)) {
                hashMap.put("expire", expire);
            } else if (!StringUtils.isEmpty(expireNumber)) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.MONTH, Integer.parseInt(expireNumber));
                String format = formatter.format(cal.getTime());
                hashMap.put("expireNumber", format);
            }
            List<StaffsPo> staffsPos = staffsPoMapper.selectListByMap(hashMap);
            if (StringUtils.isEmpty(pageSize)) {
                pageSize = "10";
            }
            if (StringUtils.isEmpty(currentPage)) {
                currentPage = "1";
            }
            if (staffsPos.size() < 1) {
                returnMsg.setMsgbox("找不到符合條件數據...");
            } else {
                PageUtil pageUtil = new PageUtil(staffsPos.size(), Integer.parseInt(pageSize), Integer.parseInt(currentPage), 5);
                int startrow = pageUtil.getStartrow();
                int pagesize = pageUtil.getPagesize();
                hashMap.put("startrow", startrow);
                hashMap.put("pagesize", pagesize);
                List<StaffsPo> staffsPoList = staffsPoMapper.selectListByMap(hashMap);
                //根据员工信息去查对应的 员工证件表和标签表
                ArrayList<Object> arrayList = new ArrayList<>();
                for (StaffsPo staffsPo : staffsPoList) {
                    HashMap<String, Object> retMap = new HashMap<>();
                    TagInfosPo tagInfosPo = new TagInfosPo();
                    tagInfosPo.setType(1);
                    tagInfosPo.setObjnum(staffsPo.getStaffid());
                    List<TagInfosPo> tagInfosPos = tagInfosPoMapper.selectByTagPo(tagInfosPo);
                    String effdate = staffsPo.getEffdate();
                    if (effdate != null && !"".equals(effdate)) {
                        Date d2 = formatter.parse(effdate);
                        if (!d2.after(new Date())) {
                            staffsPo.setTreatment(staffsPo.getAltersalary());
                        }
                    }
                    //再查找员工证件表
                    List<StaffscertPo> staffscertPos = staffscertPoMapper.selectByStaffid(staffsPo.getStaffid());

                    //循环员工证件表，得到证件是否存在过期
                    retMap.put("staffscertStatus", "證正常");
                    List<StaffscertPo> staffscertPoList = new ArrayList<>();
                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    for (StaffscertPo staffscertPo : staffscertPos) {
                        if (StringUtils.isEmpty(expire) && StringUtils.isEmpty(expireNumber)) {
                            LocalDate localDate= LocalDate.now();
                            LocalDate validity = LocalDate.parse(staffscertPo.getValidity().split(" ")[0], fmt);
                            if (validity.isBefore(localDate)) {
                                staffscertPo.setStaffscertStatus("0");
                                retMap.put("staffscertStatus", "證過期");
                            } else {
                                for (int i = 1; true; i++) {
                                    if (i > 3) {
                                        staffscertPo.setStaffscertStatus("4");
                                        break;
                                    } else {
                                        LocalDate expireDate = LocalDate.now().plusMonths(i);
                                        if (!validity.isAfter(expireDate)) {
                                            if (i == 2) {
                                                staffscertPo.setStaffscertStatus("3");
                                            } else {
                                                staffscertPo.setStaffscertStatus(i + "");
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                            staffscertPoList.add(staffscertPo);
                        } else {
                            if (!StringUtils.isEmpty(expireNumber)) {
                                LocalDate expireDate = LocalDate.now().plusMonths(Long.parseLong(expireNumber));
                                LocalDate localDate= LocalDate.now();
                                LocalDate validity = LocalDate.parse(staffscertPo.getValidity().split(" ")[0], fmt);
                                if (!validity.isBefore(localDate) && !validity.isAfter(expireDate)) {
                                    staffscertPo.setStaffscertStatus(expireNumber);
                                    staffscertPoList.add(staffscertPo);
                                }
                            } else {
                                LocalDate localDate= LocalDate.now();
                                LocalDate validity = LocalDate.parse(staffscertPo.getValidity().split(" ")[0], fmt);
                                if (validity.isBefore(localDate)) {
                                    staffscertPo.setStaffscertStatus("0");
                                    retMap.put("staffscertStatus", "證過期");
                                    staffscertPoList.add(staffscertPo);
                                }
                            }
                        }
                    }
                    List<StaffsImagePO> staffsImageList = staffsPoMapper.selectStaffsImageByStaffId(staffsPo.getStaffid());
                    staffsPo.setStaffsImageList(staffsImageList);
                    retMap.put("staffsPo", staffsPo);
                    retMap.put("tagInfosPos", tagInfosPos);
                    retMap.put("staffscertPos", staffscertPoList);
                    arrayList.add(retMap);
                }
                pageUtil.setPageData(arrayList);
                returnMsg = new ReturnMsg<>(ReturnMsg.SUCCESS, "成功", pageUtil);
            }
        }
        return returnMsg;
    }

    @Override
    @Transactional
    public ReturnMsg deleteStaffsMsg(String userid, StaffsDto staffsDto) throws Exception {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(staffsDto.getStaffid())) {
            returnMsg.setMsgbox("參數異常...");
        } else {
//            Map<String, Object> param = new HashMap<>(2);
//            param.put("number", staffsDto.getStaffid());
//            param.put("type", 1);
//            Integer count = abnormalPoMapper.selectByNumberAndType(param);
//            if (count != null && count > 0) {
//                returnMsg.setMsgbox("已存在相關記錄，暫不支持刪除操作");
//                return returnMsg;
//            }
            //删除员工，删除标签，删除员工证件，删除头像
            StaffsPo staffsPo = staffsPoMapper.selectByPrimaryKey(staffsDto.getStaffid());
            if (staffsPo == null) {
                returnMsg.setMsgbox("員工不存在...");
            } else {
                //删除员工，删除员工表，删除标签表，删除员工证件表，删除图片文件
                List<StaffscertPo> staffscertPos = staffscertPoMapper.selectByStaffid(staffsDto.getStaffid());
                staffsPoMapper.deleteByPrimaryKey(staffsPo.getStaffid());
                for (StaffscertPo staffscertPo : staffscertPos) {
                    staffscertPoMapper.deleteByPrimaryKey(staffscertPo.getCertid());
                }
                TagInfosPo tagInfosPo = new TagInfosPo();
                tagInfosPo.setObjnum(staffsDto.getStaffid());
                tagInfosPo.setType(1);
                tagInfosPoMapper.deleteByTagPo(tagInfosPo);
                HashMap<String, Object> map = new HashMap<>(1);
                map.put("staffId", staffsDto.getStaffid());
                staffsPoMapper.deleteStaffsImageByStaffId(map);

                Map<String, Object> hashMap = new HashMap<>(2);
                hashMap.put("number", staffsDto.getStaffid());
                hashMap.put("type", 1);
                abnormalPoMapper.deleteByNumberAndType(hashMap);

                returnMsg.setCode(ReturnMsg.SUCCESS);
                returnMsg.setMsgbox("成功");

                //删除的话，需要通知MQ的同时，也需要删除安全对象信息表，删除员工证件条件表
                SafeobjsPo safeobjsPo = safeobjsPoMapper.selectByPrimaryKey(staffsDto.getStaffid(), 1);
                boolean isFind = false;
                if (safeobjsPo != null) {
                    isFind = true;
                    //如果安全对象表中有数据，删除安全对象表，删除员工证件条件表
                    safeobjsPoMapper.deleteByPrimaryKey(safeobjsPo.getObjnum());
                    objsconditionPoMapper.deleteByStaffid(safeobjsPo.getObjnum());

                    EngineerinfoPo engineerinfoPo = engineerinfoPoMapper.selectByOsdid(safeobjsPo.getOsdid());
                    if (engineerinfoPo.getSchedule() == 1) {
                        logger.info("sendJobMq jobNum:{},osdId:{},code:{}", safeobjsPo.getJobnum(), safeobjsPo.getOsdid(), MQCode.JOB_RUN_UPDATE);
                        MQAllSendMessage.sendJobMq(safeobjsPo.getJobnum(), safeobjsPo.getOsdid(), MQCode.JOB_RUN_UPDATE, apiServiceMQ);
                    }
                }
                //1.4CP推送
                WSWPInfoPO wswpInfo = aiEngineerInfoMapper.selectByWswpNum(staffsDto.getStaffid());
                if (wswpInfo != null) {
                    String jobNum = wswpInfo.getJobNum();
                    AiEngineerInfoPO aiEngineerInfo = aiEngineerInfoMapper.selectByAiNum(jobNum);
                    if (aiEngineerInfo != null) {
                        isFind = true;
                        if (aiEngineerInfo.getSchedule() == 1) {
                            throw new Exception("該員工正在使用中！");
                        } else {
                            List<StaffsImagePO> staffsImageList = staffsPoMapper.selectStaffsImageByStaffId(staffsDto.getStaffid());
                            for (StaffsImagePO staffsImage : staffsImageList) {
                                FilesUtils.deleteFile(staffsImage.getImageName(), filePath + staffsImage.getImageDir());
                            }
                        }
                    }
                }
                if (!isFind) {
                    MQAllSendMessage.sendIsNotBind(staffsPo.getOrgid(), staffsPo.getStaffid(), staffsPo.getType(), MQCode.IS_NOT_BIND, apiServiceMQ);
                }
            }
        }
        return returnMsg;
    }

    @Override
    @Transactional
    public ReturnMsg updateStaffsMsg(String userid, StaffsDto staffsDto, List<StaffsImagePO> staffsImageList, List<Integer> notDelIds) {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");

        if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(staffsDto.getStaffid())) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //头像，绿卡图片，标签表，员工表，员工证件表
            StaffsPo staffsPoOld = staffsPoMapper.selectByPrimaryKey(staffsDto.getStaffid());
            //获取机构id
            String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
            //1.上传头像和证件图片(前面已经上传了)
            //2.删除标签表和证件表
            //3.新加标签表和证件表
            //4.修改员工表
            //5.删除以前上传的文件
            if (staffsPoOld != null) {
                List<StaffsImagePO> staffsImageOldList = staffsPoMapper.selectStaffsImageByStaffId(staffsDto.getStaffid());
                //start 为了实时修改盒子员工加上
                SafeobjsPo safeobjsPo = safeobjsPoMapper.selectByPrimaryKey(staffsDto.getStaffid(), 1);
//                boolean isFind = false;
                if (safeobjsPo != null) {
//                    isFind = true;
                    //员工存在工程中，查看是否添加的员工证件条件是否和以前一样，不一样则不给修改，
                    List<ObjsconditionPo> objsconditionPos = objsconditionPoMapper.selectByStaffid(staffsDto.getStaffid());
                    //保存需要满足员工个人的证件
                    StringBuilder sb1 = new StringBuilder();
                    List<Map<String, String>> carDataArr = staffsDto.getCarDataArr();
                    for (Map<String, String> stringStringMap : carDataArr) {
                        sb1.append(stringStringMap.get("typeid")).append(";");
                    }
                    List<PermitsconditionPo> permitsconditionPos;
                    int i1;
                    for (ObjsconditionPo objsconditionPo : objsconditionPos) {
                        permitsconditionPos = permitsconditionPoMapper.selectByPermitTypeId(objsconditionPo.getPermittypeid());
                        for (PermitsconditionPo permitsconditionPo : permitsconditionPos) {
                            i1 = sb1.indexOf(permitsconditionPo.getCerttypeid().toString());
                            if (i1 < 0) {
                                returnMsg.setMsgbox("工程已綁定用戶，修改用戶證件需要滿足工程中要求...");
                                return returnMsg;
                            }
                        }
                    }
//                    EngineerinfoPo engineerinfoPo = engineerinfoPoMapper.selectByOsdid(safeobjsPo.getOsdid());
                    //满足条件了，则需要修改员工中的名称，和员工证件条件表中的数据
                    safeobjsPo.setObjname(staffsDto.getEnname());
                    safeobjsPoMapper.updateByPrimaryKeySelective(safeobjsPo);

//                    if (engineerinfoPo.getSchedule() == 1) {
//                        logger.info("sendJobMq jobNum:{},osdId:{},code:{}", safeobjsPo.getJobnum(), safeobjsPo.getOsdid(), MQCode.JOB_RUN_UPDATE);
//                        MQAllSendMessage.sendJobMq(safeobjsPo.getJobnum(), safeobjsPo.getOsdid(), MQCode.JOB_RUN_UPDATE, apiServiceMQ);
//                    }
                }
//                WSWPInfoPO wswpInfo = aiEngineerInfoMapper.selectByWswpNum(staffsDto.getStaffid());
//                if (wswpInfo != null) {
//                    String jobNum = wswpInfo.getJobNum();
//                    AiEngineerInfoPO aiEngineerInfo = aiEngineerInfoMapper.selectByAiNum(jobNum);
//                    if (aiEngineerInfo != null) {
//                        isFind = true;
//                        if (aiEngineerInfo.getSchedule() == 1) {
//                            logger.info("sendJobMq jobNum:{},osdId:{},code:{}", aiEngineerInfo.getJobNum(), aiEngineerInfo.getOrgId(), MQCode.ENGINEER_RUN_UPDATE);
//                            MQAllSendMessage.sendJobMq(aiEngineerInfo.getJobNum(), aiEngineerInfo.getOrgId(), MQCode.ENGINEER_RUN_UPDATE, apiServiceMQ);
//                        }
//                    }
//                }
//                if (!isFind) {
//                    MQAllSendMessage.sendIsNotBind(staffsPoOld.getOrgid(), staffsPoOld.getStaffid(), staffsPoOld.getType(), MQCode.IS_NOT_BIND, apiServiceMQ);
//                }
                //end 为了实时修改盒子员工加上

                //2.删除标签表和证件表
                List<StaffscertPo> staffscertPosOld = staffscertPoMapper.selectByStaffid(staffsDto.getStaffid());
                for (StaffscertPo value : staffscertPosOld) {
                    staffscertPoMapper.deleteByPrimaryKey(value.getCertid());
                }
                TagInfosPo tagInfosPo = new TagInfosPo();
                tagInfosPo.setType(1);
                tagInfosPo.setObjnum(staffsDto.getStaffid());
                List<TagInfosPo> tagInfosPosOld = tagInfosPoMapper.selectByTagPo(tagInfosPo);
                tagInfosPoMapper.deleteByTagPo(tagInfosPo);

                //3.新加标签表和证件表
                List<Map<String, String>> tag = staffsDto.getTag();
                List<TagInfosPo> tagInfosPoList = new ArrayList<>();
                boolean tagExist = true;
                Map map;
                for (Map<String, String> stringMap : tag) {
                    tagInfosPo = new TagInfosPo();
                    map = stringMap;
                    tagInfosPo.setTagid(map.get("tagCode").toString());
                    TagInfosPo tagInfo = tagInfosPoMapper.selectByPrimaryKey(tagInfosPo.getTagid());
                    if (tagInfo == null) {
                        tagInfosPo.setType(1);
                        tagInfosPo.setObjnum(staffsDto.getStaffid());
                        tagInfosPoList.add(tagInfosPo);
                    } else {
                        tagExist = false;
                        Integer type = tagInfo.getType();
                        String number = tagInfo.getObjnum();
                        if (type == 0) {
                            PermitsPo permitsPo = permitsPoMapper.selectByPrimaryKey(number);
                            if (orgid.equals(permitsPo.getOrgid())) {
                                returnMsg.setMsgbox("許可證編號:" + permitsPo.getPermitid() + "正在使用標簽");
                            } else {
                                returnMsg.setMsgbox("標簽已存在其他機構...");
                            }
                        } else if (type == 1) {
                            StaffsPo staffsPo = staffsPoMapper.selectByPrimaryKey(number);
                            if (orgid.equals(staffsPo.getOrgid())) {
                                returnMsg.setMsgbox("員工編號:" + staffsPo.getStaffid() + "正在使用標簽");
                            } else {
                                returnMsg.setMsgbox("標簽已存在其他機構...");
                            }
                        } else if (type == 2) {
                            ToolsPo toolsPo = toolsPoMapper.selectByPrimaryKey(number);
                            if (orgid.equals(toolsPo.getOrgid())) {
                                returnMsg.setMsgbox("工具編號:" + toolsPo.getToolid() + "正在使用標簽");
                            } else {
                                returnMsg.setMsgbox("標簽已存在其他機構...");
                            }
                        }
                        break;
                    }
                }
                List<Map<String, String>> carDataArr = staffsDto.getCarDataArr();
                List<StaffscertPo> staffscertPoList = new ArrayList<>();

                if (carDataArr != null && carDataArr.size() > 0) {
                    for (Map<String, String> stringStringMap : carDataArr) {
                        StaffscertPo staffscertPo = new StaffscertPo();
                        map = stringStringMap;
                        staffscertPo.setStaffid(staffsDto.getStaffid());
                        String certid = map.get("certid").toString();
                        StaffscertPo staffscertPoTmp = staffscertPoMapper.selectByPrimaryKey(certid);
                        if (staffscertPoTmp != null) {
                            returnMsg.setMsgbox(certid + ": 證件編號已存在...");
                            tagExist = false;
                            break;
                        }
                        String typeid = map.get("typeid").toString();
                        String validity = map.get("validity").toString();
                        if (StringUtils.isEmpty(certid) || StringUtils.isEmpty(typeid) || StringUtils.isEmpty(validity)) {
                            returnMsg.setMsgbox("證件信息存在空值...");
                            tagExist = false;
                            break;
                        } else {
                            staffscertPo.setCertid(certid);
                            staffscertPo.setTypeid(Integer.valueOf(typeid));
                            staffscertPo.setValidity(validity);
                            staffscertPoList.add(staffscertPo);
                        }
                    }
                }

                if (tagExist) {
                    for (TagInfosPo infosPo : tagInfosPoList) {
                        tagInfosPoMapper.insertSelective(infosPo);
                    }
                    //添加员工信息表
                    StaffsPo staffsPo = new StaffsPo();
                    staffsPo.setStaffid(staffsDto.getStaffid());
                    staffsPo.setChname(staffsDto.getChname());
                    staffsPo.setEnname(staffsDto.getEnname());
                    staffsPo.setOrgid(orgid);
                    staffsPo.setTreatment(staffsDto.getTreatment());
                    staffsPo.setAltersalary(staffsDto.getAltersalary());
                    staffsPo.setEffdate(staffsDto.getEffdate());
                    HashMap<String, Object> param = new HashMap<>(2);
                    param.put("staffId", staffsDto.getStaffid());
                    param.put("ids", notDelIds);
                    staffsPoMapper.deleteStaffsImageByStaffId(param);
                    staffsPoMapper.updateByPrimaryKeySelective(staffsPo);

                    //添加员工证件表
                    for (StaffscertPo po : staffscertPoList) {
                        staffscertPoMapper.insertSelective(po);
                    }
                    for (StaffsImagePO staffsImage : staffsImageList) {
                        if (staffsImage.getId() == null) {
                            staffsPoMapper.insertStaffsImage(staffsImage);
                        } else {
                            staffsPoMapper.updateStaffsImage(staffsImage);
                        }
                    }
                    returnMsg.setCode(ReturnMsg.SUCCESS);
                    returnMsg.setMsgbox("成功");
                }
                if (returnMsg.getCode() == ReturnMsg.SUCCESS) {
                    //操作成功
                    //1.删除以前的文件
                    //删除以前的图片
                    for (StaffsImagePO staffsImage : staffsImageOldList) {
                        if (!notDelIds.contains(staffsImage.getId())) {
                            FilesUtils.deleteFile(staffsImage.getImageName(), filePath + staffsImage.getImageDir());
                        }
                    }
                    for (StaffscertPo po : staffscertPosOld) {
                        FilesUtils.deleteFile(po.getImagename(), filePath + po.getImagepath());
                    }

                } else if (returnMsg.getCode() == ReturnMsg.FAIL) {
                    //操作失败
                    //1.还原删除的标签表，员工证件表，删除上传的文件
                    for (TagInfosPo infosPo : tagInfosPosOld) {
                        tagInfosPoMapper.insertSelective(infosPo);
                    }
                    for (StaffscertPo po : staffscertPosOld) {
                        staffscertPoMapper.insertSelective(po);
                    }
                    //照片删除，返回给controller操作
                }
            } else {
                returnMsg.setMsgbox("員工信息不存在...");
            }
        }

        return returnMsg;
    }

    @Override
    public ReturnMsg getStaffsPermitsType(HttpServletRequest request) {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        String userid = request.getParameter("userid");
        String typeNameStr = request.getParameter("typeNameStr");
        String type = request.getParameter("type");
        if (StringUtils.isEmpty(userid)) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //企业编码
            UsersPo usersPo = usersPoMapper.selectByPrimaryKey(Integer.valueOf(userid));

            List<StaffsPo> staffsPos = staffsPoMapper.selectListByOrgid(usersPo.getOrgid());
            HashMap<String, Object> map;
            ArrayList<Object> list = new ArrayList<>();
            List<StaffscertPo> staffscertPos;
            //查询满足证件的员工,更具许可证条件表
            String[] split = new String[0];
            if (typeNameStr != null && !StringUtils.isEmpty(typeNameStr)) {
                split = typeNameStr.split(";");
            }
            List<Integer> certnameArr = new ArrayList<>();
            List<PermitsconditionPo> permitsconditionPo;
            for (String s : split) {
                permitsconditionPo = permitsconditionPoMapper.selectByPermitTypeId(Integer.parseInt(s));
                for (PermitsconditionPo po : permitsconditionPo) {
                    if (!certnameArr.contains(po.getPermittypeid())) {
                        certnameArr.add(po.getPermittypeid());
                    }
                }
            }
            //获取到了员工证件类型
            boolean status;
            StringBuffer sb1;
            if (staffsPos != null && staffsPos.size() > 0) {
                //循环每一个员工
                for (StaffsPo staffsPo : staffsPos) {
                    if (type != null && !"".equals(type)) {
                        map = new HashMap<>(1);
                        map.put("staffsPo", staffsPo);
//                        map.put("staffscertPo", staffscertPos);
                        list.add(map);
                    } else {
                        status = true;
                        //获取员工编号的所有员工证件记录
                        staffscertPos = staffscertPoMapper.selectByStaffidValid(staffsPo.getStaffid());
                        if (staffscertPos.size() < certnameArr.size()) {
                            continue;
                        }

                        //比较
                        if (certnameArr.size() > 0) {
                            sb1 = new StringBuffer();
                            for (StaffscertPo staffscertPo : staffscertPos) {
                                sb1.append(staffscertPo.getTypeid()).append(";");
                            }

                            for (Integer integer : certnameArr) {
                                int i1 = sb1.toString().indexOf(integer.toString());
                                if (i1 < 0) {
                                    status = false;
                                    break;
                                }
                            }
                        }

                        if (status) {
                            map = new HashMap<>(2);
                            map.put("staffsPo", staffsPo);
                            map.put("staffscertPo", staffscertPos);
                            list.add(map);
                        }
                    }
                }
            }
            if (list.size() > 0) {
                returnMsg.setData(list);
                returnMsg.setCode(ReturnMsg.SUCCESS);
                returnMsg.setMsgbox("成功");
            } else {
                returnMsg.setMsgbox("沒有符合條件的員工...");
            }
        }
        return returnMsg;
    }

    @Override
    public ReturnMsg getStaffsMsg(HttpServletRequest request) throws Exception {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        String userid = request.getParameter("userid");
        String staffid = request.getParameter("staffid");
        if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(staffid)) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //获取员工信息
            StaffsPo staffsPo = staffsPoMapper.selectByPrimaryKey(staffid);
            //获取员工证件信息
            List<StaffscertPo> staffscertPos = staffscertPoMapper.selectByStaffid(staffid);
            //获取标签信息
            Map<String, Object> param = new HashMap<>();
            param.put("objnum", staffid);
            param.put("type", 1);
            TagInfosPo tagInfosPo = tagInfosPoMapper.selectByObjnum(param);
            List<StaffsImagePO> staffsImageList = staffsPoMapper.selectStaffsImageByStaffId(staffsPo.getStaffid());
            staffsPo.setStaffsImageList(staffsImageList);
            String effdate = staffsPo.getEffdate();
            if (effdate != null && !"".equals(effdate)) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date d2 = formatter.parse(effdate);
                if (!d2.after(new Date())) {
                    staffsPo.setTreatment(staffsPo.getAltersalary());
                }
            }
            HashMap<Object, Object> map = new HashMap<>();
            map.put("staffscertPos", staffscertPos);
            map.put("staffsPo", staffsPo);
            map.put("tagInfosPos", tagInfosPo);

            returnMsg.setCode(ReturnMsg.SUCCESS);
            returnMsg.setMsgbox("成功");
            returnMsg.setData(map);
        }
        return returnMsg;
    }

    @Override
    public ReturnMsg getExpireDataList(HttpServletRequest request) throws Exception {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        String userid = request.getParameter("userid");
        String expireNumber = request.getParameter("expireNumber");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        if (StringUtils.isEmpty(userid)) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //获取机构id
            String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
            //查看员工列表，根据员工查找标签，查找员工证件
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("orgid", orgid);
            //根据带有符合条件的总数，进行分页查询的操作
            if (!StringUtils.isEmpty(expireNumber)) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.MONTH, Integer.parseInt(expireNumber));
                String format = formatter.format(cal.getTime());
                hashMap.put("expireNumber", format);
            } else {
                hashMap.put("expire", 1);
            }
            List<StaffsPo> staffsPos = staffsPoMapper.selectListByMap(hashMap);
            if (staffsPos.size() < 1) {
                returnMsg.setMsgbox("找不到符合條件數據...");
                returnMsg.setCode(ReturnMsg.SUCCESS);
                returnMsg.setData(new ArrayList<>());
            } else {
                //根据员工信息去查对应的 员工证件表
                HashMap<String, Object> retMap = null;
                ArrayList<Object> arrayList = new ArrayList<>();
                for (StaffsPo staffsPo : staffsPos) {
                    //再查找员工证件表
                    List<StaffscertPo> staffscertPos = staffscertPoMapper.selectByStaffid(staffsPo.getStaffid());
                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    for (StaffscertPo staffscertPo : staffscertPos) {
                        retMap = new HashMap<>();
                        if (!StringUtils.isEmpty(expireNumber)) {
                            LocalDate expireDate = LocalDate.now().plusMonths(Long.parseLong(expireNumber));
                            LocalDate localDate= LocalDate.now();
                            LocalDate validity = LocalDate.parse(staffscertPo.getValidity().split(" ")[0], fmt);
                            if (validity.isBefore(localDate) || validity.isAfter(expireDate)) {
                                continue;
                            } else {
                                retMap.put("staffscertStatus", "證正常");
                            }
                        } else {
                            LocalDate localDate= LocalDate.now();
                            LocalDate validity = LocalDate.parse(staffscertPo.getValidity().split(" ")[0], fmt);
                            if (validity.isBefore(localDate)) {
                                retMap.put("staffscertStatus", "證過期");
                            } else {
                                continue;
                            }
                        }
                        retMap.put("staffsPo", staffsPo);
                        retMap.put("staffscertPo", staffscertPo);
                        arrayList.add(retMap);
                    }
                }
                returnMsg = new ReturnMsg<>(ReturnMsg.SUCCESS, "成功", arrayList);
            }
        }
        return returnMsg;
    }

    @Override
    public ReturnMsg getCertType(HttpServletRequest request) {
        List<CertTypePO> arrayList = staffsPoMapper.getCerType();
        return new ReturnMsg<>(ReturnMsg.SUCCESS, "成功", arrayList);
    }

    @Override
    public ReturnMsg getCpStaffs(HttpServletRequest request) {
        ReturnMsg returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        String userid = request.getParameter("userid");
        String positionTypeId = request.getParameter("positionTypeId");
        if (StringUtils.isEmpty(userid)) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //获取机构id
            String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
            List<String> ids = staffsPoMapper.selectCpListByPositionTypeId(positionTypeId);
            List<Integer> typeIds = new ArrayList<>();
            for (String id : ids) {
                String[] split = id.split(",");
                for (String s : split) {
                    typeIds.add(Integer.valueOf(s));
                }
            }
            List<StaffsPo> staffsPos = staffsPoMapper.selectListByOrgid(orgid);
            List<StaffsPo> staffsPoList = new ArrayList<>();
            for (StaffsPo staffsPo : staffsPos) {
                List<StaffscertPo> staffscertPos = staffscertPoMapper.selectByStaffid(staffsPo.getStaffid());
                for (StaffscertPo staffscertPo : staffscertPos) {
                    if (typeIds.contains(staffscertPo.getTypeid())) {
                        Integer openCount = aiEngineerInfoMapper.getCpCountByCpNum(staffsPo.getStaffid());
                        Integer closeCount = aiEngineerInfoMapper.getCpCountByCpNumClose(staffsPo.getStaffid());
                        if (openCount == null || openCount <= 3) {
                            if (closeCount == null || closeCount <= 10) {
                                staffsPoList.add(staffsPo);
                            }
                        }
                    }
                }
            }
            returnMsg.setData(staffsPoList);
            returnMsg.setCode(ReturnMsg.SUCCESS);
            returnMsg.setMsgbox("成功");
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
            List<String> allNum = staffsPoMapper.getAllNumByOrgId(orgid);
            returnMsg.setData(allNum);
            returnMsg.setCode(ReturnMsg.SUCCESS);
            returnMsg.setMsgbox("成功");
        }
        return returnMsg;
    }

    @Override
    public ReturnMsg getAllEnName(String userid) {
        ReturnMsg returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        if (StringUtils.isEmpty(userid)) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //获取机构id
            String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
            List<String> allName = staffsPoMapper.getAllEnNameByOrgId(orgid);
            returnMsg.setData(allName);
            returnMsg.setCode(ReturnMsg.SUCCESS);
            returnMsg.setMsgbox("成功");
        }
        return returnMsg;
    }

    @Override
    public ReturnMsg getAllChName(String userid) {
        ReturnMsg returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        if (StringUtils.isEmpty(userid)) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //获取机构id
            String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
            List<String> allName = staffsPoMapper.getAllChNameByOrgId(orgid);
            returnMsg.setData(allName);
            returnMsg.setCode(ReturnMsg.SUCCESS);
            returnMsg.setMsgbox("成功");
        }
        return returnMsg;
    }

    @Override
    public void sendUpdateMq(StaffsDto staffsDto) {
        StaffsPo staffsPoOld = staffsPoMapper.selectByPrimaryKey(staffsDto.getStaffid());
        //start 为了实时修改盒子员工加上
        SafeobjsPo safeobjsPo = safeobjsPoMapper.selectByPrimaryKey(staffsDto.getStaffid(), 1);
        boolean isFind = false;
        if (safeobjsPo != null) {
            isFind = true;
            EngineerinfoPo engineerinfoPo = engineerinfoPoMapper.selectByOsdid(safeobjsPo.getOsdid());
            if (engineerinfoPo.getSchedule() == 1) {
                logger.info("sendJobMq jobNum:{},osdId:{},code:{}", safeobjsPo.getJobnum(), safeobjsPo.getOsdid(), MQCode.JOB_RUN_UPDATE);
                MQAllSendMessage.sendJobMq(safeobjsPo.getJobnum(), safeobjsPo.getOsdid(), MQCode.JOB_RUN_UPDATE, apiServiceMQ);
            }
        }
        WSWPInfoPO wswpInfo = aiEngineerInfoMapper.selectByWswpNum(staffsDto.getStaffid());
        if (wswpInfo != null) {
            String jobNum = wswpInfo.getJobNum();
            AiEngineerInfoPO aiEngineerInfo = aiEngineerInfoMapper.selectByAiNum(jobNum);
            if (aiEngineerInfo != null) {
                isFind = true;
                if (aiEngineerInfo.getSchedule() == 1) {
                    logger.info("sendJobMq jobNum:{},osdId:{},code:{}", aiEngineerInfo.getJobNum(), aiEngineerInfo.getOrgId(), MQCode.ENGINEER_RUN_UPDATE);
                    MQAllSendMessage.sendJobMq(aiEngineerInfo.getJobNum(), aiEngineerInfo.getOrgId(), MQCode.ENGINEER_RUN_UPDATE, apiServiceMQ);
                }
            }
        }
        if (!isFind) {
            MQAllSendMessage.sendIsNotBind(staffsPoOld.getOrgid(), staffsPoOld.getStaffid(), staffsPoOld.getType(), MQCode.IS_NOT_BIND, apiServiceMQ);
        }
    }
}
