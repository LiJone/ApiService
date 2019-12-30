package com.tss.apiservice.service.impl;

import com.tss.apiservice.common.PageUtil;
import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.common.utils.FilesUtils;
import com.tss.apiservice.common.utils.MQAllSendMessage;
import com.tss.apiservice.common.utils.MQCode;
import com.tss.apiservice.dao.*;
import com.tss.apiservice.dto.ToolsDto;
import com.tss.apiservice.po.*;
import com.tss.apiservice.service.ToolsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ToolsServiceImpl implements ToolsService {
    private static Logger logger = LoggerFactory.getLogger(ToolsServiceImpl.class);
    @Autowired
    ToolsPoMapper toolsPoMapper;

    @Autowired
    ToolscertPoMapper toolscertPoMapper;

    @Autowired
    TagInfosPoMapper tagInfosPoMapper;

    @Value("${filePath}")
    private String filePath;

    @Autowired
    EngineerinfoPoMapper engineerinfoPoMapper;

    @Autowired
    SafeobjsPoMapper safeobjsPoMapper;

    @Autowired
    ApiServiceMQImpl apiServiceMQ;

    @Autowired
    UsersPoMapper usersPoMapper;

    @Autowired
    AbnormalPoMapper abnormalPoMapper;

    @Autowired
    PermitsPoMapper permitsPoMapper;

    @Autowired
    StaffsPoMapper staffsPoMapper;

    @Override
    public ReturnMsg getToolsMsgList(HttpServletRequest request) throws ParseException {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        String userid = request.getParameter("userid");
        String pageSize = request.getParameter("pageSize");
        String currentPage = request.getParameter("currentPage");
        String numberBegin = request.getParameter("numberBegin");
        String numberEnd = request.getParameter("numberEnd");
        String name = request.getParameter("name");
        String expire =  request.getParameter("expire");
        String expireNumber =  request.getParameter("expireNumber");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        if (StringUtils.isEmpty(userid)) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //获取机构id
            String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
            //查看工具列表，根据工具查找标签，查找工具证件
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("orgid", orgid);
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
            if (!StringUtils.isEmpty(expire)) {
                hashMap.put("expire", expire);
            } else if (!StringUtils.isEmpty(expireNumber)) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.MONTH, Integer.parseInt(expireNumber));
                String format = formatter.format(cal.getTime());
                hashMap.put("expireNumber", format);
            }
            List<ToolsPo> toolsPos = toolsPoMapper.selectListByMap(hashMap);

            if (StringUtils.isEmpty(pageSize)) {
                pageSize = "10";
            }
            if (StringUtils.isEmpty(currentPage)) {
                currentPage = "1";
            }
            if (toolsPos.size() < 1) {
                returnMsg.setMsgbox("找不到符合條件數據...");
            } else {
                PageUtil pageUtil = new PageUtil(toolsPos.size(), Integer.parseInt(pageSize), Integer.parseInt(currentPage), 5);
                int startrow = pageUtil.getStartrow();
                int pagesize = pageUtil.getPagesize();
                hashMap.put("startrow", startrow);
                hashMap.put("pagesize", pagesize);
                List<ToolsPo> toolsPoList = toolsPoMapper.selectListByMap(hashMap);
                //根据工具信息去查对应的标签表
                TagInfosPo tagInfosPo;
                HashMap<String, Object> retMap;
                ArrayList<Object> arrayList = new ArrayList<>();
                for (ToolsPo toolsPo : toolsPoList) {
                    retMap = new HashMap<>();
                    tagInfosPo = new TagInfosPo();
                    tagInfosPo.setType(2);
                    tagInfosPo.setObjnum(toolsPo.getToolid());
                    List<TagInfosPo> tagInfosPos = tagInfosPoMapper.selectByTagPo(tagInfosPo);
                    if (StringUtils.isEmpty(expire) && StringUtils.isEmpty(expireNumber)) {
                        retMap.put("toolStatus", "正常");
                        String validity = toolsPo.getValidity();
                        Date d1 = formatter.parse(validity);
                        Date now = new Date();
                        Calendar cal1 = Calendar.getInstance();
                        cal1.setTime(now);
                        cal1.set(Calendar.HOUR_OF_DAY, 0);
                        cal1.set(Calendar.MINUTE, 0);
                        cal1.set(Calendar.SECOND, 0);
                        cal1.set(Calendar.MILLISECOND, 0);
                        if (d1.compareTo(cal1.getTime()) < 0) {
                            retMap.put("toolStatus", "過期");
                        }
                    } else {
                        if (!StringUtils.isEmpty(expireNumber)) {
                            retMap.put("toolStatus", "正常");
                        } else {
                            retMap.put("toolStatus", "過期");
                        }
                    }
                    List<ToolsImagePO> toolsImageList = toolsPoMapper.selectToolsImageByToolId(toolsPo.getToolid());
                    toolsPo.setToolsImageList(toolsImageList);
                    retMap.put("toolsPo", toolsPo);
                    retMap.put("tagInfosPos", tagInfosPos);
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
    public ReturnMsg addToolsMsg(String userid, ToolsDto toolsDto, List<ToolsImagePO> toolsImageList) {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(toolsDto.getName()) || StringUtils.isEmpty(toolsDto.getToolid())) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            ToolsPo ret = toolsPoMapper.selectByPrimaryKey(toolsDto.getToolid());
            Map map;
            if (ret == null) {
                //获取机构id
                String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
                TagInfosPo tagInfosPo;
                List<Map<String, String>> tag = toolsDto.getTag();
                List<TagInfosPo> tagInfosPoList = new ArrayList<>();
                boolean tagExist = true;
                for (Map<String, String> stringStringMap : tag) {
                    tagInfosPo = new TagInfosPo();
                    map = stringStringMap;
                    tagInfosPo.setTagid(map.get("tagCode").toString());
                    TagInfosPo tagInfo = tagInfosPoMapper.selectByPrimaryKey(tagInfosPo.getTagid());
                    if (tagInfo == null) {
                        tagInfosPo.setType(2);
                        tagInfosPo.setObjnum(toolsDto.getToolid());
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

                if (tagExist) {
                    for (TagInfosPo infosPo : tagInfosPoList) {
                        tagInfosPoMapper.insertSelective(infosPo);
                    }
                    ToolsPo toolsPo = new ToolsPo();
                    toolsPo.setToolid(toolsDto.getToolid());
                    toolsPo.setName(toolsDto.getName());
                    toolsPo.setValidity(toolsDto.getValiDity());
                    toolsPo.setTypeid(toolsDto.getTypeid());
                    toolsPo.setOrgid(orgid);
                    toolsPoMapper.insertSelective(toolsPo);
                    for (ToolsImagePO toolsImage : toolsImageList) {
                        toolsPoMapper.insertToolsImage(toolsImage);
                    }
                    returnMsg = new ReturnMsg<>(ReturnMsg.SUCCESS, "成功");
                }
            } else {
                returnMsg.setMsgbox("工具已存在...");
            }
        }
        return returnMsg;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ReturnMsg deleteToolsMsg(String userid, ToolsDto toolsDto) {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");

        if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(toolsDto.getToolid())) {
            returnMsg.setMsgbox("參數異常...");
        } else {
//            Map<String, Object> param = new HashMap<>(2);
//            param.put("number", toolsDto.getToolid());
//            param.put("type", 2);
//            Integer count = abnormalPoMapper.selectByNumberAndType(param);
//            if (count != null && count > 0) {
//                returnMsg.setMsgbox("已存在相關記錄，暫不支持刪除操作");
//                return returnMsg;
//            }
            ToolsPo toolsPoOld = toolsPoMapper.selectByPrimaryKey(toolsDto.getToolid());
            if (toolsPoOld == null) {
                returnMsg.setMsgbox("工具不存在...");
            } else {
                //删除标签，删除工具
                TagInfosPo tagInfosPo = new TagInfosPo();
                tagInfosPo.setObjnum(toolsDto.getToolid());
                tagInfosPo.setType(2);
                //删除标签
                List<TagInfosPo> tagInfosPos = tagInfosPoMapper.selectByTagPo(tagInfosPo);
                for (TagInfosPo infosPo : tagInfosPos) {
                    tagInfosPoMapper.deleteByPrimaryKey(infosPo.getTagid());
                }
                toolsPoMapper.deleteByPrimaryKey(toolsDto.getToolid());
                List<ToolsImagePO> toolsImageList = toolsPoMapper.selectToolsImageByToolId(toolsDto.getToolid());
                for (ToolsImagePO toolsImage : toolsImageList) {
                    FilesUtils.deleteFile(toolsImage.getImageName(), filePath + toolsImage.getImageDir());
                }
                HashMap<String, Object> map = new HashMap<>(1);
                map.put("toolId", toolsDto.getToolid());
                toolsPoMapper.deleteToolsImageByToolId(map);

                Map<String, Object> hashMap = new HashMap<>(2);
                hashMap.put("number", toolsDto.getToolid());
                hashMap.put("type", 2);
                abnormalPoMapper.deleteByNumberAndType(hashMap);

                SafeobjsPo safeobjsPo = safeobjsPoMapper.selectByPrimaryKey(toolsDto.getToolid(), 2);
                boolean isFind = false;
                if(safeobjsPo != null){
                    isFind = true;
                    safeobjsPoMapper.deleteByPrimaryKey(safeobjsPo.getObjnum());
                    EngineerinfoPo engineerinfoPo = engineerinfoPoMapper.selectByOsdid(safeobjsPo.getOsdid());
                    if(engineerinfoPo.getSchedule() == 1){
                        logger.info("sendJobMq jobNum:{},osdId:{},code:{}", safeobjsPo.getJobnum(), safeobjsPo.getOsdid(), MQCode.JOB_RUN_UPDATE);
                        MQAllSendMessage.sendJobMq(safeobjsPo.getJobnum(),safeobjsPo.getOsdid(), MQCode.JOB_RUN_UPDATE,apiServiceMQ);
                    }
                }
                if (!isFind) {
                    MQAllSendMessage.sendIsNotBind(toolsPoOld.getOrgid(), toolsPoOld.getToolid(), toolsPoOld.getType(), MQCode.IS_NOT_BIND, apiServiceMQ);
                }
                returnMsg.setMsgbox("成功");
                returnMsg.setCode(ReturnMsg.SUCCESS);
            }
        }
        return returnMsg;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ReturnMsg updateToolsMsg(String userid, ToolsDto toolsDto, List<ToolsImagePO> toolsImageList, List<Integer> notDelIds) {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        List<TagInfosPo> tagInfosPosOld;
        if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(toolsDto.getToolid())) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            ToolsPo toolsPoOld = toolsPoMapper.selectByPrimaryKey(toolsDto.getToolid());
            if (toolsPoOld != null) {
                List<ToolsImagePO> toolsImageOldList = toolsPoMapper.selectToolsImageByToolId(toolsDto.getToolid());
                //获取机构id
                String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
                //删除标签，更改工具，删除以前图片
                TagInfosPo tagInfosPo;
                tagInfosPo = new TagInfosPo();
                tagInfosPo.setType(2);
                tagInfosPo.setObjnum(toolsDto.getToolid());
                tagInfosPosOld = tagInfosPoMapper.selectByTagPo(tagInfosPo);
                tagInfosPoMapper.deleteByTagPo(tagInfosPo);
                toolscertPoMapper.deleteByToolid(toolsDto.getToolid());

                List<Map<String, String>> tag = toolsDto.getTag();
                List<TagInfosPo> tagInfosPoList = new ArrayList<>();
                Map map;
                boolean tagExist = true;
                for (Map<String, String> stringStringMap : tag) {
                    tagInfosPo = new TagInfosPo();
                    map = stringStringMap;
                    tagInfosPo.setTagid(map.get("tagCode").toString());
                    TagInfosPo tagInfo = tagInfosPoMapper.selectByPrimaryKey(tagInfosPo.getTagid());
                    if (tagInfo == null) {
                        tagInfosPo.setType(2);
                        tagInfosPo.setObjnum(toolsDto.getToolid());
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
                if (tagExist) {
                    ToolsPo toolsPo = new ToolsPo();
                    toolsPo.setToolid(toolsDto.getToolid());
                    toolsPo.setName(toolsDto.getName());
                    toolsPo.setTypeid(toolsDto.getTypeid());
                    toolsPo.setValidity(toolsDto.getValiDity());
                    toolsPo.setOrgid(orgid);
                    for (TagInfosPo infosPo : tagInfosPoList) {
                        tagInfosPoMapper.insertSelective(infosPo);
                    }
                    HashMap<String, Object> param = new HashMap<>();
                    param.put("toolId", toolsDto.getToolid());
                    param.put("ids", notDelIds);
                    toolsPoMapper.deleteToolsImageByToolId(param);
                    for (ToolsImagePO toolsImage : toolsImageList) {
                        if (toolsImage.getId() == null) {
                            toolsPoMapper.insertToolsImage(toolsImage);
                        } else {
                            toolsPoMapper.updateToolsImage(toolsImage);
                        }
                    }
                    toolsPoMapper.updateByPrimaryKeySelective(toolsPo);

                    returnMsg.setCode(ReturnMsg.SUCCESS);
                    returnMsg.setMsgbox("成功");
                }

                if (returnMsg.getCode() == ReturnMsg.SUCCESS) {
                    //操作成功
                    //1.删除以前的文件
                    for (ToolsImagePO toolsImage : toolsImageOldList) {
                        if (!notDelIds.contains(toolsImage.getId())) {
                            FilesUtils.deleteFile(toolsImage.getImageName(), filePath + toolsImage.getImageDir());
                        }
                    }
                    SafeobjsPo safeobjsPo = safeobjsPoMapper.selectByPrimaryKey(toolsDto.getToolid(), 2);
//                    boolean isFind = false;
                    if(safeobjsPo != null){
//                        isFind = true;
                        safeobjsPo.setObjname(toolsDto.getName());
                        safeobjsPoMapper.updateByPrimaryKeySelective(safeobjsPo);

//                        EngineerinfoPo engineerinfoPo = engineerinfoPoMapper.selectByOsdid(safeobjsPo.getOsdid());
//                        if(engineerinfoPo.getSchedule() == 1){
//                            logger.info("sendJobMq jobNum:{},osdId:{},code:{}", safeobjsPo.getJobnum(), safeobjsPo.getOsdid(), MQCode.JOB_RUN_UPDATE);
//                            MQAllSendMessage.sendJobMq(safeobjsPo.getJobnum(),safeobjsPo.getOsdid(), MQCode.JOB_RUN_UPDATE,apiServiceMQ);
//                        }
                    }
//                    if (!isFind) {
//                        MQAllSendMessage.sendIsNotBind(toolsPoOld.getOrgid(), toolsPoOld.getToolid(), toolsPoOld.getType(), MQCode.IS_NOT_BIND, apiServiceMQ);
//                    }

                } else if (returnMsg.getCode() == ReturnMsg.FAIL) {
                    //操作失败
                    //1.还原删除的标签表，员工证件表，删除上传的文件
                    for (TagInfosPo infosPo : tagInfosPosOld) {
                        tagInfosPoMapper.insertSelective(infosPo);
                    }
                }
            } else {
                returnMsg.setMsgbox("工具不存在....");
            }
        }
        return returnMsg;
    }

    @Override
    public ReturnMsg getToolsMsg(HttpServletRequest request) {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        String userid = request.getParameter("userid");
        String toolid = request.getParameter("toolid");
        if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(toolid)) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //获取员工信息
            ToolsPo toolsPo = toolsPoMapper.selectByPrimaryKey(toolid);
            //获取标签信息
            Map<String, Object> param = new HashMap<>();
            param.put("objnum", toolid);
            param.put("type", 2);
            TagInfosPo tagInfosPo = tagInfosPoMapper.selectByObjnum(param);
            List<ToolsImagePO> toolsImageList = toolsPoMapper.selectToolsImageByToolId(toolsPo.getToolid());
            toolsPo.setToolsImageList(toolsImageList);
            HashMap<Object, Object> map = new HashMap<>();
            map.put("toolsPo",toolsPo);
            map.put("tagInfosPos",tagInfosPo);

            returnMsg.setCode(ReturnMsg.SUCCESS);
            returnMsg.setMsgbox("成功");
            returnMsg.setData(map);
        }
        return returnMsg;
    }

    @Override
    public ReturnMsg getExpireDataList(HttpServletRequest request) {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        String userid = request.getParameter("userid");
        String expireNumber =  request.getParameter("expireNumber");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        if (StringUtils.isEmpty(userid)) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //获取机构id
            String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
            //查看工具列表，根据工具查找标签，查找工具证件
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
            List<ToolsPo> toolsPos = toolsPoMapper.selectListByMap(hashMap);
            if (toolsPos.size() < 1) {
                returnMsg.setMsgbox("找不到符合條件數據...");
                returnMsg.setCode(ReturnMsg.SUCCESS);
                returnMsg.setData(new ArrayList<>());
            } else {
                HashMap<String, Object> retMap;
                ArrayList<Object> arrayList = new ArrayList<>();
                for (ToolsPo toolsPo : toolsPos) {
                    retMap = new HashMap<>();
                    if (!StringUtils.isEmpty(expireNumber)) {
                        retMap.put("toolStatus", "正常");
                    } else {
                        retMap.put("toolStatus", "過期");
                    }
                    retMap.put("toolsPo", toolsPo);
                    arrayList.add(retMap);
                }
                returnMsg = new ReturnMsg<>(ReturnMsg.SUCCESS, "成功", arrayList);
            }
        }
        return returnMsg;
    }

    @Override
    public ReturnMsg getToolType(HttpServletRequest request) {
        List<ToolTypePO> arrayList = toolsPoMapper.getToolType();
        return new ReturnMsg<>(ReturnMsg.SUCCESS, "成功", arrayList);
    }

    @Override
    public ReturnMsg getAllNum(String userid) {
        ReturnMsg returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        if (StringUtils.isEmpty(userid)) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //获取机构id
            String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
            List<String> allNum = toolsPoMapper.getAllNumByOrgId(orgid);
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
            List<String> allName = toolsPoMapper.getAllNameByOrgId(orgid);
            returnMsg.setData(allName);
            returnMsg.setCode(ReturnMsg.SUCCESS);
            returnMsg.setMsgbox("成功");
        }
        return returnMsg;
    }

    @Override
    public void sendUpdateMq(ToolsDto toolsDto) {
        ToolsPo toolsPoOld = toolsPoMapper.selectByPrimaryKey(toolsDto.getToolid());
        SafeobjsPo safeobjsPo = safeobjsPoMapper.selectByPrimaryKey(toolsDto.getToolid(), 2);
        boolean isFind = false;
        if(safeobjsPo != null){
            isFind = true;

            EngineerinfoPo engineerinfoPo = engineerinfoPoMapper.selectByOsdid(safeobjsPo.getOsdid());
            if(engineerinfoPo.getSchedule() == 1){
                logger.info("sendJobMq jobNum:{},osdId:{},code:{}", safeobjsPo.getJobnum(), safeobjsPo.getOsdid(), MQCode.JOB_RUN_UPDATE);
                MQAllSendMessage.sendJobMq(safeobjsPo.getJobnum(),safeobjsPo.getOsdid(), MQCode.JOB_RUN_UPDATE,apiServiceMQ);
            }
        }
        if (!isFind) {
            MQAllSendMessage.sendIsNotBind(toolsPoOld.getOrgid(), toolsPoOld.getToolid(), toolsPoOld.getType(), MQCode.IS_NOT_BIND, apiServiceMQ);
        }
    }
}
