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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 壮Jone
 */
@Service
public class PermitsServiceImpl implements PermitsService {

    @Autowired
    private PermitsPoMapper permitsPoMapper;

    @Autowired
    private TagInfosPoMapper tagInfosPoMapper;

    @Value("${filePath}")
    private String filePath;

    @Autowired
    private EngineerinfoPoMapper engineerinfoPoMapper;

    @Autowired
    private SafeobjsPoMapper safeobjsPoMapper;

    @Autowired
    private ApiServiceMQImpl apiServiceMQ;

    @Autowired
    private UsersPoMapper usersPoMapper;

    @Autowired
    private AbnormalPoMapper abnormalPoMapper;

    @Autowired
    private StaffsPoMapper staffsPoMapper;

    @Autowired
    private ToolsPoMapper toolsPoMapper;

    @Autowired
    private AiEngineerInfoMapper aiEngineerInfoMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ReturnMsg addPermits(String userid, PermitsDto permitsDto, List<PermitsImagePO> permitsImageList) {
        ReturnMsg returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
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
                    TagInfosPo tagInfosPo;
                    List<Map<String, String>> tag = permitsDto.getTag();
                    List<TagInfosPo> tagInfosPoList = new ArrayList<>();
                    boolean tagExist = true;
                    Map map;
                    for (Map<String, String> stringStringMap : tag) {
                        tagInfosPo = new TagInfosPo();
                        map = stringStringMap;
                        tagInfosPo.setTagid(map.get("tagCode").toString());
                        tagInfosPo.setType(0);
                        tagInfosPo.setObjnum(permitsDto.getPermitid());
                        TagInfosPo tagInfo = tagInfosPoMapper.selectByPrimaryKey(tagInfosPo.getTagid());
                        if (tagInfo != null) {
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
                        } else {
                            tagInfosPoList.add(tagInfosPo);
                        }
                    }
                    if (tagExist) {
                        //循环添加标签信息
                        for (TagInfosPo infosPo : tagInfosPoList) {
                            tagInfosPoMapper.insertSelective(infosPo);
                        }
                        //许可表插入
                        PermitsPo permitsPo = new PermitsPo();
                        permitsPo.setPermitid(permitsDto.getPermitid());
                        permitsPo.setName(permitsDto.getName());
                        permitsPo.setTypeid(permitsDto.getTypeid());
                        permitsPo.setStartdate(permitsDto.getStartDate());
                        permitsPo.setEnddate(permitsDto.getEndDate());
                        permitsPo.setOrgid(orgid);
                        permitsPo.setPositionid(permitsDto.getPositionid());
                        permitsPo.setRopeweight(permitsDto.getRopeweight());
                        permitsPoMapper.insertSelective(permitsPo);
                        for (PermitsImagePO permitsImage : permitsImageList) {
                            permitsPoMapper.insertPermitsImage(permitsImage);
                        }
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
    public ReturnMsg getPermitsMsgList(HttpServletRequest request) throws ParseException {
        ReturnMsg returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        String userid = request.getParameter("userid");
        String pageSize = request.getParameter("pageSize");
        String currentPage = request.getParameter("currentPage");
        String numberBegin = request.getParameter("numberBegin");
        String numberEnd = request.getParameter("numberEnd");
        String name = request.getParameter("name");
        String expire = request.getParameter("expire");
        String expireNumber = request.getParameter("expireNumber");
        String type = request.getParameter("type");
        String effective = request.getParameter("effective");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
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
            if (!StringUtils.isEmpty(type)) {
                List<Integer> typeIds = new ArrayList<>();
                String[] split = type.split(",");
                for (String s : split) {
                    typeIds.add(Integer.valueOf(s));
                }
                hashMap.put("type", typeIds);
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
                PageUtil pageUtil = new PageUtil(permitsPos.size(), Integer.parseInt(pageSize), Integer.parseInt(currentPage), 5);
                int startrow = pageUtil.getStartrow();
                int pagesize = pageUtil.getPagesize();
                hashMap.put("startrow", startrow);
                hashMap.put("pagesize", pagesize);
                List<PermitsPo> permitsPoList = permitsPoMapper.selectListByMap(hashMap);
                //根据工具信息去查对应的标签表
                TagInfosPo tagInfosPo;
                HashMap<String, Object> retMap;
                ArrayList<Object> arrayList = new ArrayList<>();
                for (PermitsPo permitsPo : permitsPoList) {
                    retMap = new HashMap<>();
                    tagInfosPo = new TagInfosPo();
                    tagInfosPo.setType(0);
                    tagInfosPo.setObjnum(permitsPo.getPermitid());
                    List<TagInfosPo> tagInfosPos = tagInfosPoMapper.selectByTagPo(tagInfosPo);
                    if (StringUtils.isEmpty(expire) && StringUtils.isEmpty(expireNumber)) {
                        retMap.put("permitsStatus", "證正常");
                        String validity = permitsPo.getEnddate();
                        Date d1 = formatter.parse(validity);
                        if (d1.compareTo(new Date()) < 0) {
                            retMap.put("permitsStatus", "證過期");
                        }
                    } else if (!StringUtils.isEmpty(effective)) {
                        retMap.put("permitsStatus", "證正常");
                        String validity = permitsPo.getEnddate();
                        Date d1 = formatter.parse(validity);
                        if (d1.compareTo(new Date()) < 0) {
                            continue;
                        }
                    } else {
                        if (!StringUtils.isEmpty(expireNumber)) {
                            retMap.put("permitsStatus", "證正常");
                        } else {
                            retMap.put("permitsStatus", "證過期");
                        }
                    }
                    List<PermitsImagePO> permitsImageList = permitsPoMapper.selectPermitsImageByPermitId(permitsPo.getPermitid());
                    permitsPo.setPermitsImageList(permitsImageList);
                    //再查找工具证件表
                    retMap.put("permitsPo", permitsPo);
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
    public ReturnMsg deletePermitsMsg(String userid, PermitsDto permitsDto) throws Exception {
        ReturnMsg returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");

        if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(permitsDto.getPermitid())) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            Map<String, Object> param = new HashMap<>(2);
            param.put("number", permitsDto.getPermitid());
            param.put("type", 0);
            Integer count = abnormalPoMapper.selectByNumberAndType(param);
            if (count != null && count > 0) {
                returnMsg.setMsgbox("已存在相關記錄，暫不支持刪除操作");
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
                for (TagInfosPo infosPo : tagInfosPos) {
                    tagInfosPoMapper.deleteByPrimaryKey(infosPo.getTagid());
                }
                permitsPoMapper.deleteByPrimaryKey(permitsDto.getPermitid());
                HashMap<String, Object> map = new HashMap<>(1);
                map.put("permitId", permitsDto.getPermitid());
                permitsPoMapper.deletePermitsImageByPermitId(map);
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
                //1.4许可证推送
                PermitBindInfoPO permitBindInfo = aiEngineerInfoMapper.selectByPermitNum(permitsDto.getPermitid());
                if (permitBindInfo != null) {
                    String jobNum = null;
                    if (permitBindInfo.getBindType() == 1) {
                        FuncBindInfoPO funcBindInfo = aiEngineerInfoMapper.selectByFuncNum(permitBindInfo.getBindNum());
                        if (funcBindInfo != null) {
                            jobNum = funcBindInfo.getJobNum();
                        }
                    } else {
                        jobNum = permitBindInfo.getBindNum();
                    }
                    AiEngineerInfoPO aiEngineerInfo = aiEngineerInfoMapper.selectByAiNum(jobNum);
                    if (aiEngineerInfo != null) {
                        if (aiEngineerInfo.getSchedule() == 1) {
                            throw new Exception("該許可證正在使用中！");
                        } else {
                            List<PermitsImagePO> permitsImageList = permitsPoMapper.selectPermitsImageByPermitId(permitsDto.getPermitid());
                            for (PermitsImagePO permitsImage : permitsImageList) {
                                FilesUtils.deleteFile(permitsImage.getImageName(), filePath + permitsImage.getImageDir());
                            }
                        }
                    }
                }
            }
        }
        return returnMsg;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ReturnMsg updatePermitsMsg(String userid, PermitsDto permitsDto, List<PermitsImagePO> permitsImageList, List<Integer> notDelIds) {
        ReturnMsg returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(permitsDto.getPermitid())) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //获取机构id
            String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
            //查到许可证，通过许可证，修改标签和图片
            PermitsPo permitsPoOld = permitsPoMapper.selectByPrimaryKey(permitsDto.getPermitid());
            if (permitsPoOld != null) {
                List<PermitsImagePO> permitsImageOldList = permitsPoMapper.selectPermitsImageByPermitId(permitsDto.getPermitid());
                //上传图片，删除标签，修改许可证
                //删除标签
                TagInfosPo tagInfosPo;
                tagInfosPo = new TagInfosPo();
                tagInfosPo.setObjnum(permitsDto.getPermitid());
                tagInfosPo.setType(0);
                List<TagInfosPo> tagInfosPosOld = tagInfosPoMapper.selectByTagPo(tagInfosPo);
                tagInfosPoMapper.deleteByTagPo(tagInfosPo);

                List<Map<String, String>> tag = permitsDto.getTag();
                List<TagInfosPo> tagInfosPoList = new ArrayList<>();
                boolean tagExist = true;
                Map map;
                for (Map<String, String> stringStringMap : tag) {
                    tagInfosPo = new TagInfosPo();
                    map = stringStringMap;
                    tagInfosPo.setTagid(map.get("tagCode").toString());
                    TagInfosPo tagInfo = tagInfosPoMapper.selectByPrimaryKey(tagInfosPo.getTagid());
                    if (tagInfo == null) {
                        tagInfosPo.setType(0);
                        tagInfosPo.setObjnum(permitsDto.getPermitid());
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
                    //修改许可证
                    PermitsPo permitsPo = new PermitsPo();
                    permitsPo.setPermitid(permitsDto.getPermitid());
                    permitsPo.setName(permitsDto.getName());
                    permitsPo.setTypeid(permitsDto.getTypeid());
                    permitsPo.setStartdate(permitsDto.getStartDate());
                    permitsPo.setEnddate(permitsDto.getEndDate());
                    permitsPo.setOrgid(orgid);
                    permitsPo.setPositionid(permitsDto.getPositionid());
                    permitsPo.setRopeweight(permitsDto.getRopeweight());
                    HashMap<String, Object> param = new HashMap<>(2);
                    param.put("permitId", permitsDto.getPermitid());
                    param.put("ids", notDelIds);
                    permitsPoMapper.deletePermitsImageByPermitId(param);
                    for (PermitsImagePO permitsImage : permitsImageList) {
                        if (permitsImage.getId() == null) {
                            permitsPoMapper.insertPermitsImage(permitsImage);
                        } else {
                            permitsPoMapper.updatePermitsImage(permitsImage);
                        }
                    }
                    permitsPoMapper.updateByPrimaryKeySelective(permitsPo);
                    returnMsg.setCode(ReturnMsg.SUCCESS);
                    returnMsg.setMsgbox("成功");
                }

                if (returnMsg.getCode() == ReturnMsg.SUCCESS) {
                    //删除旧的图片
                    for (PermitsImagePO permitsImage : permitsImageOldList) {
                        if (!notDelIds.contains(permitsImage.getId())) {
                            FilesUtils.deleteFile(permitsImage.getImageName(), filePath + permitsImage.getImageDir());
                        }
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
                    //1.4许可证推送
                    PermitBindInfoPO permitBindInfo = aiEngineerInfoMapper.selectByPermitNum(permitsDto.getPermitid());
                    if (permitBindInfo != null) {
                        String jobNum = null;
                        if (permitBindInfo.getBindType() == 1) {
                            FuncBindInfoPO funcBindInfo = aiEngineerInfoMapper.selectByFuncNum(permitBindInfo.getBindNum());
                            if (funcBindInfo != null) {
                                jobNum = funcBindInfo.getJobNum();
                            }
                        } else {
                            jobNum = permitBindInfo.getBindNum();
                        }
                        AiEngineerInfoPO aiEngineerInfo = aiEngineerInfoMapper.selectByAiNum(jobNum);
                        if (aiEngineerInfo != null) {
                            if (aiEngineerInfo.getSchedule() == 1) {
                                MQAllSendMessage.sendJobMq(aiEngineerInfo.getJobNum(), aiEngineerInfo.getOrgId(), MQCode.ENGINEER_RUN_UPDATE, apiServiceMQ);
                            }
                        }
                    }
                } else if (returnMsg.getCode() == ReturnMsg.FAIL) {
                    //1.还原删除的标签表，员工证件表，删除上传的文件
                    for (TagInfosPo infosPo : tagInfosPosOld) {
                        tagInfosPoMapper.insertSelective(infosPo);
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
        ReturnMsg returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
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
            List<PermitsImagePO> permitsImageList = permitsPoMapper.selectPermitsImageByPermitId(permitsPo.getPermitid());
            permitsPo.setPermitsImageList(permitsImageList);
            HashMap<Object, Object> map = new HashMap<>();
            map.put("permitsPo", permitsPo);
            map.put("tagInfosPos", tagInfosPo);
            returnMsg.setCode(ReturnMsg.SUCCESS);
            returnMsg.setMsgbox("成功");
            returnMsg.setData(map);
        }
        return returnMsg;
    }

    @Override
    public ReturnMsg getExpireDataList(HttpServletRequest request) {
        ReturnMsg returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        String userid = request.getParameter("userid");
        String expireNumber = request.getParameter("expireNumber");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        if (StringUtils.isEmpty(userid)) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //获取机构id
            String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
            //查看许可证列表，根据许可证查找标签
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
            List<PermitsPo> permitsPos = permitsPoMapper.selectListByMap(hashMap);
            if (permitsPos.size() < 1) {
                returnMsg.setMsgbox("找不到符合條件數據...");
                returnMsg.setCode(ReturnMsg.SUCCESS);
                returnMsg.setData(new ArrayList<>());
            } else {
                HashMap<String, Object> retMap = null;
                ArrayList<Object> arrayList = new ArrayList<>();
                for (PermitsPo permitsPo : permitsPos) {
                    retMap = new HashMap<>();
                    if (!StringUtils.isEmpty(expireNumber)) {
                        retMap.put("permitsStatus", "證正常");
                    } else {
                        retMap.put("permitsStatus", "證過期");
                    }
                    retMap.put("permitsPo", permitsPo);
                    arrayList.add(retMap);
                }
                returnMsg = new ReturnMsg<>(ReturnMsg.SUCCESS, "成功", arrayList);
            }
        }
        return returnMsg;
    }

    @Override
    public ReturnMsg getPermitType(HttpServletRequest request) {
        List<PermitTypePO> arrayList = permitsPoMapper.getPermitType();
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
            List<String> allNum = permitsPoMapper.getAllNumByOrgId(orgid);
            returnMsg.setData(allNum);
            returnMsg.setCode(ReturnMsg.SUCCESS);
            returnMsg.setMsgbox("成功");
        }
        return returnMsg;
    }
}
