package com.tss.apiservice.service.impl;

import com.tss.apiservice.common.PageUtil;
import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.common.utils.FilesUtils;
import com.tss.apiservice.common.utils.MQAllSendMessage;
import com.tss.apiservice.common.utils.MQCode;
import com.tss.apiservice.dao.*;
import com.tss.apiservice.dto.StaffsDto;
import com.tss.apiservice.po.*;
import com.tss.apiservice.service.StaffsService;
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
public class StaffsServiceImpl implements StaffsService {

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

    @Override
    @Transactional
    public ReturnMsg addStaffsMsg(String userid, StaffsDto staffsDto, HashMap<String, String> hashMap,
                                  String headImageName, String headPath) throws ParseException {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(staffsDto.getStaffid()) ||
                StringUtils.isEmpty(staffsDto.getChname()) ||
                StringUtils.isEmpty(staffsDto.getEnname())) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            StaffsPo ret = staffsPoMapper.selectByPrimaryKey(staffsDto.getStaffid());
            Map map = null;
            if (ret == null) {
                //添加标签信息表
                List<Map<String, String>> tag = staffsDto.getTag();
                List<TagInfosPo> tagInfosPoList = new ArrayList<>();
                boolean tagExist = true;
                for (int i = 0; i < tag.size(); i++) {
                    TagInfosPo tagInfosPo = new TagInfosPo();
                    map = tag.get(i);
                    tagInfosPo.setTagid(map.get("tagCode").toString());
                    if (tagInfosPoMapper.selectByPrimaryKey(tagInfosPo.getTagid()) == null) {
                        tagInfosPo.setType(1);
                        tagInfosPo.setObjnum(staffsDto.getStaffid());
                        tagInfosPoList.add(tagInfosPo);
                    } else {
                        tagExist = false;
                        returnMsg.setMsgbox("標簽已存在...");
                        break;
                    }
                }

                StaffscertPo staffscertPo = null;
                List<Map<String, String>> carDataArr = staffsDto.getCarDataArr();
                List<StaffscertPo> staffscertPoList = new ArrayList<>();
                String certid = null;
                String typename = null;
                String validity = null;
                String filePathTmp = null;
                StaffscertPo staffscertPoTmp = null;
                if (carDataArr != null && carDataArr.size() > 0) {
                    for (int i = 0; i < carDataArr.size(); i++) {
                        staffscertPo = new StaffscertPo();
                        map = carDataArr.get(i);
                        staffscertPo.setStaffid(staffsDto.getStaffid());
                        certid = map.get("certid").toString();
                        staffscertPoTmp = staffscertPoMapper.selectByPrimaryKey(certid);
                        if (staffscertPoTmp != null) {
                            returnMsg.setMsgbox(certid + ": 證件編號已存在...");
                            tagExist = false;
                            break;
                        }
                        typename = map.get("typename").toString();
                        validity = map.get("validity").toString();
                        if (StringUtils.isEmpty(certid) || StringUtils.isEmpty(typename) || StringUtils.isEmpty(validity)) {
                            returnMsg.setMsgbox("證件信息存在空值...");
                            tagExist = false;
                            break;
                        } else {
                            staffscertPo.setCertid(certid);
                            staffscertPo.setTypename(typename);
                            staffscertPo.setValidity(validity);
                            staffscertPoList.add(staffscertPo);
                            filePathTmp = hashMap.get(certid);
                            if (filePathTmp != null) {
                                staffscertPo.setImagename(filePathTmp.substring(filePathTmp.lastIndexOf("/") + 1, filePathTmp.length()));
                                staffscertPo.setImagepath(filePathTmp.substring(0, filePathTmp.lastIndexOf("/") + 1));
                            }

                        }
                    }
                }

                if (tagExist) {
                    for (int i = 0; i < tagInfosPoList.size(); i++) {
                        tagInfosPoMapper.insertSelective(tagInfosPoList.get(i));
                    }
                    //添加员工信息表
                    StaffsPo staffsPo = new StaffsPo();
                    staffsPo.setStaffid(staffsDto.getStaffid());
                    staffsPo.setChname(staffsDto.getChname());
                    staffsPo.setEnname(staffsDto.getEnname());
                    //获取机构id
                    String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
                    staffsPo.setOrgid(orgid);
                    if (!StringUtils.isEmpty(headImageName)) {
                        staffsPo.setImagepath(headPath);
                        staffsPo.setImagename(headImageName);
                    }
                    staffsPo.setTreatment(staffsDto.getTreatment());
                    staffsPo.setEffdate(staffsDto.getEffdate());
                    staffsPo.setAltersalary(staffsDto.getAltersalary());
                    staffsPoMapper.insertSelective(staffsPo);
                    //添加员工证件表
                    for (int i = 0; i < staffscertPoList.size(); i++) {
                        staffscertPoMapper.insertSelective(staffscertPoList.get(i));
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
    public ReturnMsg getStaffsMsgList(HttpServletRequest request) throws Exception{
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        String userid = request.getParameter("userid");
        String pageSize = request.getParameter("pageSize");
        String currentPage = request.getParameter("currentPage");
        String numberBegin = request.getParameter("numberBegin");
        String numberEnd = request.getParameter("numberEnd");
        String chname = request.getParameter("chname");
        String enname = request.getParameter("enname");

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
                PageUtil pageUtil = new PageUtil(staffsPos.size(), Integer.valueOf(pageSize), Integer.valueOf(currentPage), 5);
                int startrow = pageUtil.getStartrow();
                int pagesize = pageUtil.getPagesize();
                hashMap.put("startrow", startrow);
                hashMap.put("pagesize", pagesize);
                List<StaffsPo> staffsPoList = staffsPoMapper.selectListByMap(hashMap);
                //根据员工信息去查对应的 员工证件表和标签表
                TagInfosPo tagInfosPo = null;
                HashMap<String, Object> retMap = null;
                ArrayList<Object> arrayList = new ArrayList<>();
                for (int i = 0; i < staffsPoList.size(); i++) {
                    retMap = new HashMap<>();
                    tagInfosPo = new TagInfosPo();
                    tagInfosPo.setType(1);
                    tagInfosPo.setObjnum(staffsPoList.get(i).getStaffid());
                    List<TagInfosPo> tagInfosPos = tagInfosPoMapper.selectByTagPo(tagInfosPo);
                    String effdate = staffsPoList.get(i).getEffdate();
                    SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
                    if (effdate != null && !"".equals(effdate)) {
                        Date d2 =formatter1.parse(effdate);
                        if (!d2.after(new Date())) {
                            //staffsPoList.get(i).setAltersalary(staffsPoList.get(i).getTreatment());
                            staffsPoList.get(i).setTreatment(staffsPoList.get(i).getAltersalary());
                        }
                    }
                    //再查找员工证件表
                    List<StaffscertPo> staffscertPos = staffscertPoMapper.selectByStaffid(staffsPoList.get(i).getStaffid());

                    //循环员工证件表，得到证件是否存在过期
                    retMap.put("staffscertStatus", "證正常");
                    for (int y = 0; y < staffscertPos.size(); y++) {
                        String validity = staffscertPos.get(y).getValidity();
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        Date d1 =formatter.parse(validity);
                        if (d1.compareTo(new Date()) == -1) {
                            retMap.put("staffscertStatus", "證過期");
                            break;
                        }
                    }
                    retMap.put("staffsPo", staffsPoList.get(i));
                    retMap.put("tagInfosPos", tagInfosPos);
                    retMap.put("staffscertPos", staffscertPos);
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
    public ReturnMsg deleteStaffsMsg(String userid, StaffsDto staffsDto) {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        List<StaffscertPo> staffscertPos = null;
        StaffsPo staffsPo = null;
        if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(staffsDto.getStaffid())) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //删除员工，删除标签，删除员工证件，删除头像
            staffsPo = staffsPoMapper.selectByPrimaryKey(staffsDto.getStaffid());
            if (staffsPo == null) {
                returnMsg.setMsgbox("員工不存在...");
            } else {
                //删除员工，删除员工表，删除标签表，删除员工证件表，删除图片文件
                staffscertPos = staffscertPoMapper.selectByStaffid(staffsDto.getStaffid());
                staffsPoMapper.deleteByPrimaryKey(staffsPo.getStaffid());
                for (int i = 0; i < staffscertPos.size(); i++) {
                    staffscertPoMapper.deleteByPrimaryKey(staffscertPos.get(i).getCertid());
                }
                TagInfosPo tagInfosPo = new TagInfosPo();
                tagInfosPo.setObjnum(staffsDto.getStaffid());
                tagInfosPo.setType(1);
                tagInfosPoMapper.deleteByTagPo(tagInfosPo);
                returnMsg.setCode(ReturnMsg.SUCCESS);
                returnMsg.setMsgbox("成功");
            }
        }
        if (returnMsg.getCode() == ReturnMsg.SUCCESS) {
            if (!StringUtils.isEmpty(staffsPo.getImagepath())) {
                FilesUtils.deleteFile(staffsPo.getImagename(), filePath + staffsPo.getImagepath());
            }
            //成功进行删除图片
            for (int i = 0; i < staffscertPos.size(); i++) {
                FilesUtils.deleteFile(staffscertPos.get(i).getImagename(), filePath + staffscertPos.get(i).getImagepath());
            }

            //判断员工是否存在工程中，如果存在，则发送mq
//            trueOrFalseToMQ(staffsDto.getStaffid());
            //删除的话，需要通知MQ的同时，也需要删除安全对象信息表，删除员工证件条件表
            SafeobjsPo safeobjsPo = safeobjsPoMapper.selectByPrimaryKey(staffsDto.getStaffid());
            if (safeobjsPo != null) {
                //如果安全对象表中有数据，删除安全对象表，删除员工证件条件表
                safeobjsPoMapper.deleteByPrimaryKey(safeobjsPo.getObjnum());
                objsconditionPoMapper.deleteByStaffid(safeobjsPo.getObjnum());

                EngineerinfoPo engineerinfoPo = engineerinfoPoMapper.selectByOsdid(safeobjsPo.getOsdid());
                if (engineerinfoPo.getSchedule() == 1) {
                    MQAllSendMessage.sendJobMq(safeobjsPo.getJobnum(), safeobjsPo.getOsdid(), MQCode.JOB_RUN_UPDATE, apiServiceMQ);
                }
            }


        }
        return returnMsg;
    }

    @Override
    @Transactional
    public ReturnMsg updateStaffsMsg(String userid, StaffsDto staffsDto, HashMap<String, String> hashMap, String headImageName, String headPath) throws ParseException {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");

        StaffsPo staffsPoOld = null;
        List<StaffscertPo> staffscertPosOld = null;
        List<TagInfosPo> tagInfosPosOld = null;
        if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(staffsDto.getStaffid())) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //头像，绿卡图片，标签表，员工表，员工证件表
            staffsPoOld = staffsPoMapper.selectByPrimaryKey(staffsDto.getStaffid());
            //1.上传头像和证件图片(前面已经上传了)
            //2.删除标签表和证件表
            //3.新加标签表和证件表
            //4.修改员工表
            //5.删除以前上传的文件
            if (staffsPoOld != null) {
                //start 为了实时修改盒子员工加上
                //判断员工是否存在工程中，如果存在，则发送mq
//                    trueOrFalseToMQ(staffsDto.getStaffid());
                SafeobjsPo safeobjsPo = safeobjsPoMapper.selectByPrimaryKey(staffsDto.getStaffid());
                if (safeobjsPo != null) {
                    //员工存在工程中，查看是否添加的员工证件条件是否和以前一样，不一样则不给修改，
                    List<ObjsconditionPo> objsconditionPos = objsconditionPoMapper.selectByStaffid(staffsDto.getStaffid());
                    //保存需要满足员工个人的证件
                    StringBuffer sb1 = new StringBuffer();
                    List<Map<String, String>> carDataArr = staffsDto.getCarDataArr();
                    for (int i = 0; i < carDataArr.size(); i++) {
                        sb1.append(carDataArr.get(i).get("typename")).append(";");
                    }
                    List<PermitsconditionPo> permitsconditionPos = null;
                    int i1 = 0;
                    for (int i = 0; i < objsconditionPos.size(); i++) {
                        permitsconditionPos = permitsconditionPoMapper.selectByPermitname(objsconditionPos.get(i).getPermitname());
                        for (int j = 0; j < permitsconditionPos.size(); j++) {
//                            i1 = permitsconditionPos.get(j).getCertname().indexOf(sb1.toString());
                            i1 = sb1.indexOf(permitsconditionPos.get(j).getCertname());
                            if (i1 < 0) {
                                returnMsg.setMsgbox("工程已綁定用戶，修改用戶證件需要滿足工程中要求...");
                                return returnMsg;
                            }
                        }
                    }
                    EngineerinfoPo engineerinfoPo = engineerinfoPoMapper.selectByOsdid(safeobjsPo.getOsdid());
                    //满足条件了，则需要修改员工中的名称，和员工证件条件表中的数据
                    safeobjsPo.setObjname(staffsDto.getEnname());
                    safeobjsPoMapper.updateByPrimaryKeySelective(safeobjsPo);

                    if (engineerinfoPo.getSchedule() == 1) {
                        MQAllSendMessage.sendJobMq(safeobjsPo.getJobnum(), safeobjsPo.getOsdid(), MQCode.JOB_RUN_UPDATE, apiServiceMQ);
                    }
                }
                //end 为了实时修改盒子员工加上

                //2.删除标签表和证件表
                staffscertPosOld = staffscertPoMapper.selectByStaffid(staffsDto.getStaffid());
                for (int i = 0; i < staffscertPosOld.size(); i++) {
                    staffscertPoMapper.deleteByPrimaryKey(staffscertPosOld.get(i).getCertid());
                }
                TagInfosPo tagInfosPo = new TagInfosPo();
                tagInfosPo.setType(1);
                tagInfosPo.setObjnum(staffsDto.getStaffid());
                tagInfosPosOld = tagInfosPoMapper.selectByTagPo(tagInfosPo);
                tagInfosPoMapper.deleteByTagPo(tagInfosPo);

                //3.新加标签表和证件表
                List<Map<String, String>> tag = staffsDto.getTag();
                List<TagInfosPo> tagInfosPoList = new ArrayList();
                boolean tagExist = true;
                Map map = null;
                for (int i = 0; i < tag.size(); i++) {
                    tagInfosPo = new TagInfosPo();
                    map = (HashMap) tag.get(i);
                    tagInfosPo.setTagid(map.get("tagCode").toString());
                    if (tagInfosPoMapper.selectByPrimaryKey(tagInfosPo.getTagid()) == null) {
                        tagInfosPo.setType(1);
                        tagInfosPo.setObjnum(staffsDto.getStaffid());
                        tagInfosPoList.add(tagInfosPo);
                    } else {
                        tagExist = false;
                        returnMsg.setMsgbox("標簽已存在...");
                        break;
                    }
                }

                StaffscertPo staffscertPo = null;
                List<Map<String, String>> carDataArr = staffsDto.getCarDataArr();
                List<StaffscertPo> staffscertPoList = new ArrayList<>();
                String certid = null;
                String typename = null;
                String validity = null;
                String filePathTmp = null;
                StaffscertPo staffscertPoTmp = null;
                if (carDataArr != null && carDataArr.size() > 0) {
                    for (int i = 0; i < carDataArr.size(); i++) {
                        staffscertPo = new StaffscertPo();
                        map = (HashMap) carDataArr.get(i);
                        staffscertPo.setStaffid(staffsDto.getStaffid());
                        certid = map.get("certid").toString();
                        staffscertPoTmp = staffscertPoMapper.selectByPrimaryKey(certid);
                        if (staffscertPoTmp != null) {
                            returnMsg.setMsgbox(certid + ": 證件編號已存在...");
                            tagExist = false;
                            break;
                        }
                        typename = map.get("typename").toString();
                        validity = map.get("validity").toString();
                        if (StringUtils.isEmpty(certid) || StringUtils.isEmpty(typename) || StringUtils.isEmpty(validity)) {
                            returnMsg.setMsgbox("證件信息存在空值...");
                            tagExist = false;
                            break;
                        } else {
                            staffscertPo.setCertid(certid);
                            staffscertPo.setTypename(typename);
                            staffscertPo.setValidity(validity);
                            staffscertPoList.add(staffscertPo);
                            filePathTmp = hashMap.get(certid);
                            if (filePathTmp != null) {
                                staffscertPo.setImagename(filePathTmp.substring(filePathTmp.lastIndexOf("/") + 1, filePathTmp.length()));
                                staffscertPo.setImagepath(filePathTmp.substring(0, filePathTmp.lastIndexOf("/") + 1));
                            }

                        }
                    }
                }

                if (tagExist) {
                    for (int i = 0; i < tagInfosPoList.size(); i++) {
                        tagInfosPoMapper.insertSelective(tagInfosPoList.get(i));
                    }
                    //添加员工信息表
                    StaffsPo staffsPo = new StaffsPo();
                    staffsPo.setStaffid(staffsDto.getStaffid());
                    staffsPo.setChname(staffsDto.getChname());
                    staffsPo.setEnname(staffsDto.getEnname());

                    //获取机构id
                    String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
                    staffsPo.setOrgid(orgid);
                    if (!StringUtils.isEmpty(headImageName)) {
                        staffsPo.setImagepath(headPath);
                        staffsPo.setImagename(headImageName);
                    }
                    staffsPo.setTreatment(staffsDto.getTreatment());
                    staffsPo.setAltersalary(staffsDto.getAltersalary());
                    staffsPo.setEffdate(staffsDto.getEffdate());
                    staffsPoMapper.updateByPrimaryKeySelective(staffsPo);
                    //添加员工证件表
                    for (int i = 0; i < staffscertPoList.size(); i++) {
                        staffscertPoMapper.insertSelective(staffscertPoList.get(i));
                    }

                    returnMsg.setCode(ReturnMsg.SUCCESS);
                    returnMsg.setMsgbox("成功");
                }
                if (returnMsg.getCode() == ReturnMsg.SUCCESS) {
                    //操作成功
                    //1.删除以前的文件
                    //删除以前的图片
                    FilesUtils.deleteFile(staffsPoOld.getImagename(), filePath + staffsPoOld.getImagepath());
                    for (int i = 0; i < staffscertPosOld.size(); i++) {
                        FilesUtils.deleteFile(staffscertPosOld.get(i).getImagename(), filePath + staffscertPosOld.get(i).getImagepath());
                    }

                } else if (returnMsg.getCode() == ReturnMsg.FAIL) {
                    //操作失败
                    //1.还原删除的标签表，员工证件表，删除上传的文件
                    for (int i = 0; i < tagInfosPosOld.size(); i++) {
                        tagInfosPoMapper.insertSelective(tagInfosPosOld.get(i));
                    }
                    for (int i = 0; i < staffscertPosOld.size(); i++) {
                        staffscertPoMapper.insertSelective(staffscertPosOld.get(i));
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

            List<StaffsPo> staffsPos = staffsPoMapper.selectListByOrgid(usersPo.getOrgid());
            HashMap<String, Object> map = null;
            ArrayList<Object> list = new ArrayList<>();
            List<StaffscertPo> staffscertPos = null;
            //查询满足证件的员工,更具许可证条件表
            String[] split = new String[1];
            if (typeNameStr == null || StringUtils.isEmpty(typeNameStr)) {
                split[0] = null;
            } else {
                split = typeNameStr.split(";");
            }

            List certnameArr = new ArrayList<>();
            List<PermitsconditionPo> permitsconditionPo = null;
            for (int i = 0; i < split.length; i++) {
                permitsconditionPo = permitsconditionPoMapper.selectByPermitname(split[i]);
                for (int j = 0; j < permitsconditionPo.size(); j++) {
                    if (!certnameArr.contains(permitsconditionPo.get(j).getCertname())) {
                        certnameArr.add(permitsconditionPo.get(j).getCertname());
                    }
                }
            }
            //获取到了员工证件类型
            boolean status = true;
            StringBuffer sb1 = null;
            if (staffsPos != null && staffsPos.size() > 0) {
                //循环每一个员工
                for (int i = 0; i < staffsPos.size(); i++) {
                    status = true;
                    map = new HashMap<>();
                    //获取员工编号的所有员工证件记录
                    staffscertPos = staffscertPoMapper.selectByStaffidValid(staffsPos.get(i).getStaffid());

                    if (staffscertPos.size() < certnameArr.size()) {
                        continue;
                    }

                    //比较
                    if (certnameArr.size() > 0) {
                        sb1 = new StringBuffer();
                        for (int j = 0; j < staffscertPos.size(); j++) {
                            sb1.append(staffscertPos.get(j).getTypename()).append(";");
                        }

                        for (int y = 0; y < certnameArr.size(); y++) {
                            int i1 = sb1.toString().indexOf(certnameArr.get(y).toString());
                            if (i1 < 0) {
                                status = false;
                                break;
                            }
                        }
                    }

                    if (status) {
                        map = new HashMap<>();
                        map.put("staffsPo", staffsPos.get(i));
                        map.put("staffscertPo", staffscertPos);
                        list.add(map);
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
    public ReturnMsg getStaffsMsg(HttpServletRequest request) {
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
            TagInfosPo tagInfosPo = tagInfosPoMapper.selectByObjnum(staffid);

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
}
