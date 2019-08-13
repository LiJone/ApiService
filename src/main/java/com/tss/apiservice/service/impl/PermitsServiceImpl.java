package com.tss.apiservice.service.impl;

import com.tss.apiservice.common.PageUtil;
import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.common.utils.FilesUtils;
import com.tss.apiservice.common.utils.MQAllSendMessage;
import com.tss.apiservice.common.utils.MQCode;
import com.tss.apiservice.dao.*;
import com.tss.apiservice.dto.PermitsDto;
import com.tss.apiservice.po.*;
import com.tss.apiservice.service.PermitsService;
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
public class PermitsServiceImpl implements PermitsService {

    @Autowired
    PermitsPoMapper permitsPoMapper;

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
    @Transactional
    public ReturnMsg<Object> addPermits(String userid, PermitsDto permitsDto, String filePathStr, String fileNameStr) throws ParseException {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");

        if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(permitsDto.getPermitid()) || StringUtils.isEmpty(permitsDto.getName()) ||
                StringUtils.isEmpty(permitsDto.getStartDate()) || StringUtils.isEmpty(permitsDto.getEndDate())) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //获取机构id
            String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
            PermitsPo ret = permitsPoMapper.selectByPrimaryKey(permitsDto.getPermitid());
            if (ret == null) {
                ret = permitsPoMapper.selectByName(permitsDto.getName());
                if (ret != null) {
                    returnMsg.setMsgbox("許可證名稱已存在...");
                } else {
                    //标签插入
                    TagInfosPo tagInfosPo = null;
                    List<Map<String, String>> tag = permitsDto.getTag();
                    List<TagInfosPo> tagInfosPoList = new ArrayList();
                    boolean tagExist = true;
                    Map map = null;
                    for (int i = 0; i < tag.size(); i++) {
                        tagInfosPo = new TagInfosPo();
                        map = tag.get(i);
                        tagInfosPo.setTagid(map.get("tagCode").toString());
                        tagInfosPo.setType(0);
                        tagInfosPo.setObjnum(permitsDto.getPermitid());
                        if (tagInfosPoMapper.selectByPrimaryKey(tagInfosPo.getTagid()) != null) {
                            tagExist = false;
                            returnMsg.setMsgbox("標簽已存在...");
                            break;
                        } else {
                            tagInfosPoList.add(tagInfosPo);
                        }
                    }
                    if (tagExist) {
                        //循环添加标签信息
                        for (int i = 0; i < tagInfosPoList.size(); i++) {
                            tagInfosPoMapper.insertSelective(tagInfosPoList.get(i));
                        }
                        //许可表插入
                        PermitsPo permitsPo = new PermitsPo();
                        permitsPo.setPermitid(permitsDto.getPermitid());
                        permitsPo.setName(permitsDto.getName());
                        permitsPo.setTypename(permitsDto.getTypeName());
                        permitsPo.setStartdate(permitsDto.getStartDate());
                        permitsPo.setEnddate(permitsDto.getEndDate());
                        permitsPo.setOrgid(orgid);
                        permitsPo.setFilepath(filePathStr);
                        permitsPo.setFilename(fileNameStr);
                        permitsPoMapper.insertSelective(permitsPo);
                        returnMsg = new ReturnMsg<>(ReturnMsg.SUCCESS, "成功");
                    }
                }
            } else {
                returnMsg.setMsgbox("許可證已存在...");
            }
        }

        return returnMsg;
    }

    @Override
    public ReturnMsg<Object> getPermitsMsgList(HttpServletRequest request) throws ParseException {
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
            //查看许可证列表，根据许可证查找标签
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
            List<PermitsPo> permitsPos = permitsPoMapper.selectListByMap(hashMap);

            if (StringUtils.isEmpty(pageSize)) {
                pageSize = "10";
            }
            if (StringUtils.isEmpty(currentPage)) {
                currentPage = "1";
            }
            if (permitsPos.size() < 1) {
                returnMsg.setMsgbox("找不到符合條件數據...");
            } else {
                PageUtil pageUtil = new PageUtil(permitsPos.size(), Integer.valueOf(pageSize), Integer.valueOf(currentPage), 5);
                int startrow = pageUtil.getStartrow();
                int pagesize = pageUtil.getPagesize();
                hashMap.put("startrow", startrow);
                hashMap.put("pagesize", pagesize);
                List<PermitsPo> permitsPoList = permitsPoMapper.selectListByMap(hashMap);
                //根据工具信息去查对应的 工具证件表和标签表
                TagInfosPo tagInfosPo = null;
                HashMap<String, Object> retMap = null;
                ArrayList<Object> arrayList = new ArrayList<>();
                for (int i = 0; i < permitsPoList.size(); i++) {
                    retMap = new HashMap<>();
                    tagInfosPo = new TagInfosPo();
                    tagInfosPo.setType(0);
                    tagInfosPo.setObjnum(permitsPoList.get(i).getPermitid());
                    List<TagInfosPo> tagInfosPos = tagInfosPoMapper.selectByTagPo(tagInfosPo);
                    retMap.put("permitsStatus", "證正常");
                    String validity = permitsPoList.get(i).getEnddate();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date d1 =formatter.parse(validity);
                    if (d1.compareTo(new Date()) == -1) {
                        retMap.put("permitsStatus", "證過期");
                    }
                    //再查找工具证件表
                    retMap.put("permitsPo", permitsPoList.get(i));
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
    @Transactional
    public ReturnMsg deletePermitsMsg(String userid, PermitsDto permitsDto) {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");

        if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(permitsDto.getPermitid())) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            Map<String, Object> param = new HashMap<>(2);
            param.put("number", permitsDto.getPermitid());
            param.put("type", 0);
            Integer count = abnormalPoMapper.selectByNumberAndType(param);
            if (count != null && count > 0) {
                returnMsg.setMsgbox("已存在相关记录，暂不支持删除操作");
                return returnMsg;
            }
            //删除还要对图片进行删除
            PermitsPo permitsPo = permitsPoMapper.selectByPrimaryKey(permitsDto.getPermitid());
            if (permitsPo == null) {
                returnMsg.setMsgbox("許可證不存在...");
            } else {
                //删除标签，删除许可证，在删除图片
                TagInfosPo tagInfosPo = new TagInfosPo();
                tagInfosPo.setObjnum(permitsPo.getPermitid());
                tagInfosPo.setType(0);
                List<TagInfosPo> tagInfosPos = tagInfosPoMapper.selectByTagPo(tagInfosPo);
                for (int i = 0; i < tagInfosPos.size(); i++) {
                    tagInfosPoMapper.deleteByPrimaryKey(tagInfosPos.get(i).getTagid());
                }
                permitsPoMapper.deleteByPrimaryKey(permitsDto.getPermitid());
                FilesUtils.deleteFile(permitsPo.getFilename(), filePath + permitsPo.getFilepath());
                returnMsg.setCode(ReturnMsg.SUCCESS);
                returnMsg.setMsgbox("成功");

                //在工程中的话去修改工程对象信息表，要是工程启动的话，就推送，加修改
                SafeobjsPo safeobjsPo = safeobjsPoMapper.selectByPrimaryKey(permitsDto.getPermitid(), 0);
                if (safeobjsPo != null) {
                    //这里是直接删除
                    safeobjsPoMapper.deleteByPrimaryKey(safeobjsPo.getObjnum());
                    EngineerinfoPo engineerinfoPo = engineerinfoPoMapper.selectByOsdid(safeobjsPo.getOsdid());
                    if (engineerinfoPo.getSchedule() == 1) {
                        MQAllSendMessage.sendJobMq(safeobjsPo.getJobnum(), safeobjsPo.getOsdid(), MQCode.JOB_RUN_UPDATE, apiServiceMQ);
                    }
                }
            }
        }
        return returnMsg;
    }

    @Override
    @Transactional
    public ReturnMsg updatePermitsMsg(String userid, PermitsDto permitsDto, String filePathStr, String fileNameStr) throws ParseException {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");

        PermitsPo permitsPoOld = null;
        List<TagInfosPo> tagInfosPosOld = null;

        if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(permitsDto.getPermitid())) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //获取机构id
            String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
            //查到许可证，通过许可证，修改标签和图片
            permitsPoOld = permitsPoMapper.selectByPrimaryKey(permitsDto.getPermitid());
            if (permitsPoOld != null) {
                //上传图片，删除标签，修改许可证
                List filesDataArr = null;
                //删除标签
                TagInfosPo tagInfosPo = null;
                tagInfosPo = new TagInfosPo();
                tagInfosPo.setObjnum(permitsDto.getPermitid());
                tagInfosPo.setType(0);
                tagInfosPosOld = tagInfosPoMapper.selectByTagPo(tagInfosPo);
                tagInfosPoMapper.deleteByTagPo(tagInfosPo);

                List<Map<String, String>> tag = permitsDto.getTag();
                List<TagInfosPo> tagInfosPoList = new ArrayList();
                boolean tagExist = true;
                Map map = null;
                for (int i = 0; i < tag.size(); i++) {
                    tagInfosPo = new TagInfosPo();
                    map = (HashMap) tag.get(i);
                    tagInfosPo.setTagid(map.get("tagCode").toString());
                    if (tagInfosPoMapper.selectByPrimaryKey(tagInfosPo.getTagid()) == null) {
                        tagInfosPo.setType(0);
                        tagInfosPo.setObjnum(permitsDto.getPermitid());
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
                    //修改许可证
                    PermitsPo permitsPo = new PermitsPo();
                    permitsPo.setPermitid(permitsDto.getPermitid());
                    permitsPo.setName(permitsDto.getName());
                    permitsPo.setTypename(permitsDto.getTypeName());
                    permitsPo.setStartdate(permitsDto.getStartDate());
                    permitsPo.setEnddate(permitsDto.getEndDate());
                    permitsPo.setOrgid(orgid);
                    permitsPo.setFilepath(filePathStr);
                    permitsPo.setFilename(fileNameStr);
                    permitsPoMapper.updateByPrimaryKeySelective(permitsPo);
                    //删除旧的图片
                    returnMsg.setCode(ReturnMsg.SUCCESS);
                    returnMsg.setMsgbox("成功");
                }

                if (returnMsg.getCode() == ReturnMsg.SUCCESS) {
                    //1.删除以前的文件
                    if (permitsPoOld != null) {
                        FilesUtils.deleteFile(permitsPoOld.getFilename(), filePath + permitsPoOld.getFilepath());
                    }

                    //在工程中的话去修改工程对象信息表，要是工程启动的话，就推送，加修改
                    SafeobjsPo safeobjsPo = safeobjsPoMapper.selectByPrimaryKey(permitsDto.getPermitid(), 0);
                    if (safeobjsPo != null) {
                        //修改安全对象信息表中的许可证名称
                        safeobjsPo.setObjname(permitsDto.getName());
                        safeobjsPoMapper.updateByPrimaryKeySelective(safeobjsPo);
                        EngineerinfoPo engineerinfoPo = engineerinfoPoMapper.selectByOsdid(safeobjsPo.getOsdid());
                        if (engineerinfoPo.getSchedule() == 1) {
                            MQAllSendMessage.sendJobMq(safeobjsPo.getJobnum(), safeobjsPo.getOsdid(), MQCode.JOB_RUN_UPDATE, apiServiceMQ);
                        }
                    }
                } else if (returnMsg.getCode() == ReturnMsg.FAIL) {
                    //1.还原删除的标签表，员工证件表，删除上传的文件
                    for (int i = 0; i < tagInfosPosOld.size(); i++) {
                        tagInfosPoMapper.insertSelective(tagInfosPosOld.get(i));
                    }
                }
            } else {
                returnMsg.setMsgbox("許可證不存在...");
            }
        }
        return returnMsg;
    }

    @Override
    public ReturnMsg getPermitsMsg(HttpServletRequest request) {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        String userid = request.getParameter("userid");
        String permitid = request.getParameter("permitid");
        if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(permitid)) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //获取员工信息
            PermitsPo permitsPo = permitsPoMapper.selectByPrimaryKey(permitid);
            //获取标签信息
            Map<String, Object> param = new HashMap<>();
            param.put("objnum", permitid);
            param.put("type", 0);
            TagInfosPo tagInfosPo = tagInfosPoMapper.selectByObjnum(param);

            HashMap<Object, Object> map = new HashMap<>();
            map.put("permitsPo", permitsPo);
            map.put("tagInfosPos", tagInfosPo);
            returnMsg.setCode(ReturnMsg.SUCCESS);
            returnMsg.setMsgbox("成功");
            returnMsg.setData(map);
        }
        return returnMsg;
    }
}
