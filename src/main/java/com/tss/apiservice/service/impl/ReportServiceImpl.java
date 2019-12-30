package com.tss.apiservice.service.impl;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.dao.AttendancePoMapper;
import com.tss.apiservice.dao.StaffsPoMapper;
import com.tss.apiservice.dao.UsersPoMapper;
import com.tss.apiservice.po.AttendOTRecordPo;
import com.tss.apiservice.po.AttendancePo;
import com.tss.apiservice.po.StaffsPo;
import com.tss.apiservice.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    AttendancePoMapper attendancePoMapper;

    @Autowired
    StaffsPoMapper staffsPoMapper;

    @Autowired
    private UsersPoMapper usersPoMapper;

    @Override
    public ReturnMsg getAttendanceCollect(HttpServletRequest request) throws Exception {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        String userid = request.getParameter("userid");
        String staffid = request.getParameter("staffid");
        String timeBegin = request.getParameter("timeBegin");
        String timeEnd = request.getParameter("timeEnd");
        String staffsName = request.getParameter("staffsName");
        String excel = (String) request.getAttribute("excel");
        if (StringUtils.isEmpty(userid)) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //获取所有的员工
            HashMap<Object, Object> map = new HashMap<>(3);
            //获取机构id
            String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
            map.put("orgid", orgid);
            if (!StringUtils.isEmpty(staffid)) {
                map.put("staffid", staffid);
            }
            if (!StringUtils.isEmpty(staffsName)) {
                map.put("staffsName", staffsName);
            }

            List<StaffsPo> staffsPos = staffsPoMapper.selectListByMap02(map);
            //循环所有的上班记录员工，各个班次上班只要有记录就是 半天
            map = new HashMap<>(4);
            if (!StringUtils.isEmpty(timeBegin)) {
                map.put("timeBegin", timeBegin);
            }
            if (!StringUtils.isEmpty(timeEnd)) {
                map.put("timeEnd", timeEnd);
            }
            map.put("orgid", orgid);

            ArrayList<Object> arrayList = new ArrayList<>();
            HashMap<Object, Object> retMap = null;
            for (StaffsPo staffsPo : staffsPos) {
                retMap = new HashMap<>();
                map.put("staffid", staffsPo.getStaffid());
                retMap.put("staffsPo", staffsPo);
                Double wordDay = (attendancePoMapper.selectCountAmontimeIsNotNull(map) + attendancePoMapper.selectCountPmontimeIsNotNull(map)) * 0.5;
                retMap.put("workDay", wordDay);
                //查询所有的考勤记录
                List<AttendancePo> attendancePos = attendancePoMapper.selectListByMap(map);
                Float workAddHour = 0F;
                double workAddSalary = 0.0;
                if (attendancePos != null && attendancePos.size() > 0) {
                    for (AttendancePo attendancePo : attendancePos) {
                        Double oneDay = 0.0;
                        if (attendancePo.getAmontime() != null) {
                            oneDay = oneDay + 0.5;
                        }
                        if (attendancePo.getPmontime() != null) {
                            oneDay = oneDay + 0.5;
                        }
                        if (oneDay != 0.0) {
                            workAddSalary = workAddSalary + oneDay * attendancePo.getTreatment();
                        }
                        Float addHour = attendancePoMapper.selectAddHour(attendancePo.getId());
                        if (addHour != null) {
                            workAddHour = workAddHour + addHour;
                            if (attendancePo.getTreatment() != null && attendancePo.getTreatment() != 0) {
                                workAddSalary = workAddSalary + (attendancePo.getTreatment() / 9 * 1.5) * addHour;
                            }
                        }
                    }
                }
                BigDecimal b = new BigDecimal(workAddSalary);
                if (excel != null) {
                    workAddSalary = b.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
                    retMap.put("workAddSalary", workAddSalary);
                } else {
                    retMap.put("workAddSalary", b.setScale(0, BigDecimal.ROUND_HALF_UP).intValue());
                }
                retMap.put("workAddHour", workAddHour);
                arrayList.add(retMap);
            }
            returnMsg.setCode(ReturnMsg.SUCCESS);
            returnMsg.setMsgbox("成功");
            returnMsg.setData(arrayList);
        }
        return returnMsg;
    }

    @Override
    public ReturnMsg getOneStaffAttendance(HttpServletRequest request) throws Exception {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        String userid = request.getParameter("userid");
        String staffid = request.getParameter("staffid");
        String timeBegin = request.getParameter("timeBegin");
        String timeEnd = request.getParameter("timeEnd");
        String excel = (String) request.getAttribute("excel");
        if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(staffid)) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //获取这个人的记录
            HashMap<Object, Object> map = new HashMap<>(4);
            //获取机构id
            String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
            map.put("orgid", orgid);
            map.put("staffid", staffid);
            if (!StringUtils.isEmpty(timeBegin)) {
                map.put("timeBegin", timeBegin);
            }
            if (!StringUtils.isEmpty(timeEnd)) {
                map.put("timeEnd", timeEnd);
            }

            List<AttendancePo> attendancePos = attendancePoMapper.selectListByMap(map);
            ArrayList<Object> arrayList = new ArrayList<>();
            for (AttendancePo po : attendancePos) {
                //循环每一天，计算出天数
                Double workDay = 0.0;
                Double workAddTimes = 0.0;
                AttendancePo attendancePo = po;
                //获取当前考勤该员工加班信息
                List<AttendOTRecordPo> attendOTRecordPos = attendancePoMapper.selectWorkAddInfo(attendancePo.getId());
                map = new HashMap<>(6);
                if (attendancePo.getAmontime() != null) {
                    workDay = workDay + 0.5;
                }
                if (attendancePo.getPmontime() != null) {
                    workDay = workDay + 0.5;
                }
                List<String> times = new ArrayList<>();
                for (AttendOTRecordPo attendOTRecordPo : attendOTRecordPos) {
                    workAddTimes = workAddTimes + attendOTRecordPo.getHour();
                    times.add(attendOTRecordPo.getOttime());
                }
                Integer salary = attendancePo.getTreatment();
                double realSalary;
                if (salary == null || salary == 0) {
                    realSalary = 0.0;
                } else {
                    realSalary = salary / 9.0 * workAddTimes * 1.5;
                    if (workDay != 0.0) {
                        realSalary = realSalary + workDay * salary;
                    }
                }
                BigDecimal b = new BigDecimal(realSalary);
                if (excel != null) {
                    realSalary = b.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
                    map.put("realSalary", realSalary);
                } else {
                    map.put("realSalary", b.setScale(0, BigDecimal.ROUND_HALF_UP).intValue());
                }
                map.put("workDay", workDay);
                map.put("workAddDay", workAddTimes);
                map.put("attendancePo", attendancePo);
                map.put("times", times);
                map.put("salary", salary);
                arrayList.add(map);
            }
            returnMsg.setCode(ReturnMsg.SUCCESS);
            returnMsg.setMsgbox("成功");
            returnMsg.setData(arrayList);
        }
        return returnMsg;
    }
}
