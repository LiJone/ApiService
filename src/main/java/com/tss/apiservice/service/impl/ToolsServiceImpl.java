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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ToolsServiceImpl implements ToolsService {
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
                cal.add(Calendar.MONTH, 1);
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
                TagInfosPo tagInfosPo = null;
                HashMap<String, Object> retMap = null;
                ArrayList<Object> arrayList = new ArrayList<>();
                for (int i = 0; i < toolsPoList.size(); i++) {
                    retMap = new HashMap<>();
                    tagInfosPo = new TagInfosPo();
                    tagInfosPo.setType(2);
                    tagInfosPo.setObjnum(toolsPoList.get(i).getToolid());
                    List<TagInfosPo> tagInfosPos = tagInfosPoMapper.selectByTagPo(tagInfosPo);
                    if (StringUtils.isEmpty(expire) && StringUtils.isEmpty(expireNumber)) {
                        retMap.put("toolStatus", "正常");
                        String validity = toolsPoList.get(i).getValidity();
                        Date d1 =formatter.parse(validity);
                        if (d1.compareTo(new Date()) == -1) {
                            retMap.put("toolStatus", "過期");
                        }
                    } else {
                        if (!StringUtils.isEmpty(expireNumber)) {
                            retMap.put("toolStatus", "正常");
                        } else {
                            retMap.put("toolStatus", "過期");
                        }
                    }
                    retMap.put("toolsPo", toolsPoList.get(i));
                    retMap.put("tagInfosPos", tagInfosPos);
                    arrayList.add(retMap);
                }
                pageUtil.setPageData(arrayList);
                returnMsg = new ReturnMsg<>(ReturnMsg.SUCCESS, "成功", pageUtil);
            }
        }
        return returnMsg;
    }

    @Transactional
    @Override
    public ReturnMsg addToolsMsg(String userid, ToolsDto toolsDto, String filePathStr, String fileNameStr) throws
            ParseException {

        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");

        if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(toolsDto.getName()) || StringUtils.isEmpty(toolsDto.getToolid())) {
            returnMsg.setMsgbox("參數異常...");

        } else {
            ToolsPo ret = toolsPoMapper.selectByPrimaryKey(toolsDto.getToolid());
            Map map = null;
            if (ret == null) {
                //获取机构id
                String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
                TagInfosPo tagInfosPo = null;
                List<Map<String, String>> tag = toolsDto.getTag();
                List<TagInfosPo> tagInfosPoList = new ArrayList();
                boolean tagExist = true;
                for (int i = 0; i < tag.size(); i++) {
                    tagInfosPo = new TagInfosPo();
                    map = (HashMap) tag.get(i);
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
                    for (int i = 0; i < tagInfosPoList.size(); i++) {
                        tagInfosPoMapper.insertSelective(tagInfosPoList.get(i));
                    }
                    ToolsPo toolsPo = new ToolsPo();
                    toolsPo.setToolid(toolsDto.getToolid());
                    toolsPo.setName(toolsDto.getName());
                    toolsPo.setImagepath(filePathStr);
                    toolsPo.setImagename(fileNameStr);
                    toolsPo.setValidity(toolsDto.getValiDity());

                    toolsPo.setOrgid(orgid);
                    toolsPoMapper.insertSelective(toolsPo);
                    returnMsg = new ReturnMsg<>(ReturnMsg.SUCCESS, "成功");
                }
            } else {
                returnMsg.setMsgbox("工具已存在...");
            }
        }
        return returnMsg;
    }

    @Override
    @Transactional
    public ReturnMsg deleteToolsMsg(String userid, ToolsDto toolsDto) {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");

        ToolsPo toolsPoOld = null;
        if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(toolsDto.getToolid())) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            Map<String, Object> param = new HashMap<>(2);
            param.put("number", toolsDto.getToolid());
            param.put("type", 2);
            Integer count = abnormalPoMapper.selectByNumberAndType(param);
            if (count != null && count > 0) {
                returnMsg.setMsgbox("已存在相關記錄，暫不支持刪除操作");
                return returnMsg;
            }
            toolsPoOld = toolsPoMapper.selectByPrimaryKey(toolsDto.getToolid());
            if (toolsPoOld == null) {
                returnMsg.setMsgbox("工具不存在...");
            } else {
                //删除标签，删除工具
                TagInfosPo tagInfosPo = new TagInfosPo();
                tagInfosPo.setObjnum(toolsDto.getToolid());
                tagInfosPo.setType(2);
                //删除标签
                List<TagInfosPo> tagInfosPos = tagInfosPoMapper.selectByTagPo(tagInfosPo);
                for (int i = 0; i < tagInfosPos.size(); i++) {
                    tagInfosPoMapper.deleteByPrimaryKey(tagInfosPos.get(i).getTagid());
                }
                ToolsPo toolsPo = toolsPoMapper.selectByPrimaryKey(toolsDto.getToolid());
                toolsPoMapper.deleteByPrimaryKey(toolsDto.getToolid());
                FilesUtils.deleteFile(toolsPo.getImagename(), filePath + toolsPo.getImagepath());

                SafeobjsPo safeobjsPo = safeobjsPoMapper.selectByPrimaryKey(toolsDto.getToolid(), 2);
                if(safeobjsPo != null){
                    safeobjsPoMapper.deleteByPrimaryKey(safeobjsPo.getObjnum());
                    EngineerinfoPo engineerinfoPo = engineerinfoPoMapper.selectByOsdid(safeobjsPo.getOsdid());
                    if(engineerinfoPo.getSchedule() == 1){
                        MQAllSendMessage.sendJobMq(safeobjsPo.getJobnum(),safeobjsPo.getOsdid(), MQCode.JOB_RUN_UPDATE,apiServiceMQ);
                    }
                }
                returnMsg.setMsgbox("成功");
                returnMsg.setCode(ReturnMsg.SUCCESS);
            }
        }
        return returnMsg;
    }

    @Override
    @Transactional
    public ReturnMsg updateToolsMsg(String userid, ToolsDto toolsDto, String filePathStr, String fileNameStr) throws ParseException {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");

        ToolscertPo toolscertPoOld = null;
        ToolsPo toolsPoOld = null;
        List<TagInfosPo> tagInfosPosOld = null;

        if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(toolsDto.getToolid())) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            toolsPoOld = toolsPoMapper.selectByPrimaryKey(toolsDto.getToolid());
            if (toolsPoOld != null) {
                //获取机构id
                String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
                //删除标签，更改工具，删除以前图片
                TagInfosPo tagInfosPo = null;
                tagInfosPo = new TagInfosPo();
                tagInfosPo.setType(2);
                tagInfosPo.setObjnum(toolsDto.getToolid());
                tagInfosPosOld = tagInfosPoMapper.selectByTagPo(tagInfosPo);
                tagInfosPoMapper.deleteByTagPo(tagInfosPo);

                toolscertPoOld = toolscertPoMapper.selectByToolid(toolsDto.getToolid());
                toolscertPoMapper.deleteByToolid(toolsDto.getToolid());

                List<Map<String, String>> tag = toolsDto.getTag();
                List<TagInfosPo> tagInfosPoList = new ArrayList();
                Map map = null;
                boolean tagExist = true;
                for (int i = 0; i < tag.size(); i++) {
                    tagInfosPo = new TagInfosPo();
                    map = (HashMap) tag.get(i);
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
                    toolsPo.setImagepath(filePathStr);
                    toolsPo.setImagename(fileNameStr);
                    toolsPo.setValidity(toolsDto.getValiDity());

                    toolsPo.setOrgid(orgid);

                    for (int i = 0; i < tagInfosPoList.size(); i++) {
                        tagInfosPoMapper.insertSelective(tagInfosPoList.get(i));
                    }
                    toolsPoMapper.updateByPrimaryKeySelective(toolsPo);

                    returnMsg.setCode(ReturnMsg.SUCCESS);
                    returnMsg.setMsgbox("成功");
                }

                if (returnMsg.getCode() == ReturnMsg.SUCCESS) {
                    //操作成功
                    //1.删除以前的文件
                    if (toolscertPoOld != null) {
                        FilesUtils.deleteFile(toolscertPoOld.getImagename(), filePath + toolscertPoOld.getImagepath());
                    }
                    SafeobjsPo safeobjsPo = safeobjsPoMapper.selectByPrimaryKey(toolsDto.getToolid(), 2);
                    if(safeobjsPo != null){
                        safeobjsPo.setObjname(toolsDto.getName());
                        safeobjsPoMapper.updateByPrimaryKeySelective(safeobjsPo);

                        EngineerinfoPo engineerinfoPo = engineerinfoPoMapper.selectByOsdid(safeobjsPo.getOsdid());
                        if(engineerinfoPo.getSchedule() == 1){
                            MQAllSendMessage.sendJobMq(safeobjsPo.getJobnum(),safeobjsPo.getOsdid(), MQCode.JOB_RUN_UPDATE,apiServiceMQ);
                        }
                    }


                } else if (returnMsg.getCode() == ReturnMsg.FAIL) {
                    //操作失败
                    //1.还原删除的标签表，员工证件表，删除上传的文件
                    for (int i = 0; i < tagInfosPosOld.size(); i++) {
                        tagInfosPoMapper.insertSelective(tagInfosPosOld.get(i));
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
    public ReturnMsg getExpireDataList(HttpServletRequest request) throws ParseException {
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
                cal.add(Calendar.MONTH, 1);
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
                HashMap<String, Object> retMap = null;
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
}
