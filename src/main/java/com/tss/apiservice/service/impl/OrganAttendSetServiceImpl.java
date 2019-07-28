package com.tss.apiservice.service.impl;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.dao.OrganAttendSetPoMapper;
import com.tss.apiservice.dao.UsersPoMapper;
import com.tss.apiservice.dto.AttendOTSetDto;
import com.tss.apiservice.dto.AttendSetDto;
import com.tss.apiservice.po.AttendOTSetPo;
import com.tss.apiservice.po.AttendSetPo;
import com.tss.apiservice.service.OrganAttendSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class OrganAttendSetServiceImpl implements OrganAttendSetService {

    @Autowired
    private UsersPoMapper usersPoMapper;

    @Autowired
    private OrganAttendSetPoMapper organAttendSetPoMapper;

    @Override
    public ReturnMsg getOrganSetting(HttpServletRequest request) {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        String userid = request.getParameter("userid");

        if (StringUtils.isEmpty(userid)) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //获取机构id
            String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
            //获取该机构考勤设置
            AttendSetPo attendSetPo = organAttendSetPoMapper.selectAttendSet(orgid);
            if (attendSetPo != null) {
                //获取该机构考勤加班设置
                List<AttendOTSetPo> attendOTSetPos =  organAttendSetPoMapper.selectAttendOTSet(orgid);
                attendSetPo.setAttendOTSetPos(attendOTSetPos);
            }
            returnMsg.setMsgbox("成功");
            returnMsg.setCode(ReturnMsg.SUCCESS);
            returnMsg.setData(attendSetPo);
        }
        return returnMsg;
    }

    @Override
    public ReturnMsg setOrganSetting(String userid, AttendSetDto attendSetDto) throws Exception{
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        if (StringUtils.isEmpty(userid)) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //获取机构id
            String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
            if (orgid == null) {
                returnMsg.setMsgbox("機構不存在...");
                returnMsg.setCode(ReturnMsg.FAIL);
            } else {
                Integer count = organAttendSetPoMapper.checkIsExist(orgid);
                AttendSetPo attendSetPo = new AttendSetPo();
                attendSetPo.setOrgid(orgid);
                if (!StringUtils.isEmpty(attendSetDto.getAmofftime())) {
                    attendSetPo.setAmofftime(attendSetDto.getAmofftime());
                }
                if (!StringUtils.isEmpty(attendSetDto.getAmontime())) {
                    attendSetPo.setAmontime(attendSetDto.getAmontime());
                }
                if (!StringUtils.isEmpty(attendSetDto.getPmofftime())) {
                    attendSetPo.setPmofftime(attendSetDto.getPmofftime());
                }
                if (!StringUtils.isEmpty(attendSetDto.getPmontime())) {
                    attendSetPo.setPmontime(attendSetDto.getPmontime());
                }
                if (attendSetDto.getAttendOTSetDtos() != null) {
                    //删除旧的加班设置
                    organAttendSetPoMapper.deleteOldAttendOTSet(orgid);
                    for (AttendOTSetDto attendOTSetDto : attendSetDto.getAttendOTSetDtos()) {
                        AttendOTSetPo attendOTSetPo = new AttendOTSetPo();
                        attendOTSetPo.setOrgid(orgid);
                        if (attendOTSetDto.getHour() == null) {
                            attendOTSetPo.setHour(0.0);
                        } else {
                            attendOTSetPo.setHour(attendOTSetDto.getHour());
                        }
                        if (!StringUtils.isEmpty(attendOTSetDto.getOtofftime())) {
                            attendOTSetPo.setOtofftime(attendOTSetDto.getOtofftime());
                        }
                        if (!StringUtils.isEmpty(attendOTSetDto.getOtontime())) {
                            attendOTSetPo.setOtontime(attendOTSetDto.getOtontime());
                        }
                        //添加新的加班设置
                        organAttendSetPoMapper.insertNewAttendOTSet(attendOTSetPo);
                    }
                }
                //如果数据库没有就插入新的，有则修改
                if (count == null || count < 1) {
                    organAttendSetPoMapper.insertSelective(attendSetPo);
                } else {
                    organAttendSetPoMapper.updateByPrimaryKeySelective(attendSetPo);
                }
                returnMsg.setMsgbox("成功");
                returnMsg.setCode(ReturnMsg.SUCCESS);
            }
        }
        return returnMsg;
    }
}
