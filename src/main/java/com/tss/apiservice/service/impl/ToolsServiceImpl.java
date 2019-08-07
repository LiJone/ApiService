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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public ReturnMsg getToolsMsgList(HttpServletRequest request) {
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
                PageUtil pageUtil = new PageUtil(toolsPos.size(), Integer.valueOf(pageSize), Integer.valueOf(currentPage), 5);
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
                TagInfosPo tagInfosPo = null;
                List<Map<String, String>> tag = toolsDto.getTag();
                List<TagInfosPo> tagInfosPoList = new ArrayList();
                boolean tagExist = true;
                for (int i = 0; i < tag.size(); i++) {
                    tagInfosPo = new TagInfosPo();
                    map = (HashMap) tag.get(i);
                    tagInfosPo.setTagid(map.get("tagCode").toString());
                    if (tagInfosPoMapper.selectByPrimaryKey(tagInfosPo.getTagid()) == null) {
                        tagInfosPo.setType(2);
                        tagInfosPo.setObjnum(toolsDto.getToolid());
                        tagInfosPoList.add(tagInfosPo);
                    } else {
                        tagExist = false;
                        returnMsg.setMsgbox("標簽已存在...");
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
                    //获取机构id
                    String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
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
                returnMsg.setMsgbox("已存在相关记录，暂不支持删除操作");
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
                    if (tagInfosPoMapper.selectByPrimaryKey(tagInfosPo.getTagid()) == null) {
                        tagInfosPo.setType(2);
                        tagInfosPo.setObjnum(toolsDto.getToolid());
                        tagInfosPoList.add(tagInfosPo);
                    } else {
                        tagExist = false;
                        returnMsg.setMsgbox("標簽已存在...");
                        break;
                    }
                }
                if (tagExist) {
                    ToolsPo toolsPo = new ToolsPo();
                    toolsPo.setToolid(toolsDto.getToolid());
                    toolsPo.setName(toolsDto.getName());
                    toolsPo.setImagepath(filePathStr);
                    toolsPo.setImagename(fileNameStr);
                    toolsPo.setValidity(toolsDto.getValiDity());
                    //获取机构id
                    String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
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
}
