package com.tss.apiservice.controller;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.po.AttendancePo;
import com.tss.apiservice.po.StaffsPo;
import com.tss.apiservice.po.StaffscertPo;
import com.tss.apiservice.po.vo.AbnormalExceptionVo;
import com.tss.apiservice.service.*;
import com.tss.apiservice.utils.ExcelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author 壮Jone
 */
@Controller
@RequestMapping("/app/excel")
public class ExcelFileController {
    private static Logger logger = LoggerFactory.getLogger(ExcelFileController.class);

    @Autowired
    private ReportService reportService;

    @Autowired
    private ExceptionSerive exceptionSerive;

    @Autowired
    private StaffsService staffsService;

    @Autowired
    private UsersService usersService;

    @RequestMapping(value = "/attendanceExcel", method = RequestMethod.GET)
    public void attendanceExcel(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.setAttribute("excel", "1");
            ReturnMsg returnMsg = reportService.getAttendanceCollect(request);
            if (returnMsg.getCode() == 1) {
                String timeBegin = request.getParameter("timeBegin");
                String timeEnd = request.getParameter("timeEnd");
                String userid = request.getParameter("userid");
                String username = usersService.getUserNameById(Integer.valueOf(userid));
                ArrayList<Object> arrayList = (ArrayList<Object>) returnMsg.getData();
                String sheetName = "考勤汇总表格";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = sdf.format(new Date());
                String titleName = "考勤匯總報表 (" + timeBegin + "至" + timeEnd + ")";
                String titleName2 = "打印人：" + username + "                  打印時間：" + date;
                String fileName = "考勤汇总表格";
                int columnNumber = 7;
                int[] columnWidth = {8, 20, 18, 10, 15, 18, 13};
                String[] columnName = {"No.", "員工編號", "英文名", "中文名", "個人總天數", "個人加班總小時", "總薪酬"};
                Double workDays = 0.0;
                Double workAddSalarys = 0.0;
                Float workAddHours = 0F;
                String[][] dataList = new String[arrayList.size() + 1][7];
                for (int i = 0; i < arrayList.size(); i++) {
                    HashMap<Object, Object> retMap = (HashMap<Object, Object>) arrayList.get(i);
                    StaffsPo staffsPo = (StaffsPo) retMap.get("staffsPo");
                    Double workDay = (Double) retMap.get("workDay");
                    Float workAddHour = (Float) retMap.get("workAddHour");
                    Double workAddSalary = (Double) retMap.get("workAddSalary");
                    if (workDay != null) {
                        workDays = workDays + workDay;
                    }
                    if (workAddSalary != null) {
                        workAddSalarys = workAddSalarys + workAddSalary;
                    }
                    if (workAddHour != null) {
                        workAddHours = workAddHours + workAddHour;
                    }
                    dataList[i][0] = ((i + 1) + "");
                    dataList[i][1] = staffsPo.getStaffid();
                    dataList[i][2] = staffsPo.getEnname();
                    dataList[i][3] = staffsPo.getChname();
                    dataList[i][4] = (workDay + "天");
                    dataList[i][5] = (workAddHour + "小時");
                    dataList[i][6] = (workAddSalary + "元");
                }
                dataList[arrayList.size()][0] = ("合計");
                dataList[arrayList.size()][1] = ("");
                dataList[arrayList.size()][2] = ("");
                dataList[arrayList.size()][3] = ("");
                dataList[arrayList.size()][4] = (workDays + "天");
                dataList[arrayList.size()][5] = (workAddHours + "小時");
                dataList[arrayList.size()][6] = (workAddSalarys + "元");
                ExcelUtils.ExportWithResponse(sheetName, titleName, titleName2, fileName, columnNumber, columnWidth, columnName, dataList, response);
            } else {
                logger.error("获取考勤汇总数据失败");
            }
        } catch (Exception e) {
            logger.info("/app/excel/attendanceExcel 异常");
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/OneStaffAttendanceExcel", method = RequestMethod.GET)
    public void oneStaffAttendanceExcel(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.setAttribute("excel", "1");
            ReturnMsg returnMsg = reportService.getOneStaffAttendance(request);
            if (returnMsg.getCode() == 1) {
                String timeBegin = request.getParameter("timeBegin");
                String timeEnd = request.getParameter("timeEnd");
                String name = request.getParameter("name");
                String userid = request.getParameter("userid");
                String username = usersService.getUserNameById(Integer.valueOf(userid));
                ArrayList<Object> arrayList = (ArrayList<Object>) returnMsg.getData();
                String sheetName = name + "考勤詳情表格";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = sdf.format(new Date());
                String titleName = name + "考勤詳情報表 (" + timeBegin + "至" + timeEnd + ")";
                String titleName2 = "打印人：" + username + "                  打印時間：" + date;
                String fileName = name + "考勤詳情表格";
                int columnNumber = 12;
                int[] columnWidth = {8, 18, 20, 10, 15, 18, 13, 18, 18, 10, 15, 18};
                String[] columnName = {"No.", "日期", "員工編號", "上午上班時間", "上午下班時間", "下午上班時間",
                        "下午下班時間", "晚上加班時間", "上班天數", "加班小時", "每日薪酬", "實際薪酬"};
                Double workDays = 0.0;
                Double realSalarys = 0.0;
                Double workAddHours = 0.0;
                String[][] dataList = new String[arrayList.size() + 1][12];
                for (int i = 0; i < arrayList.size(); i++) {
                    HashMap<Object, Object> retMap = (HashMap<Object, Object>) arrayList.get(i);
                    AttendancePo attendancePo = (AttendancePo) retMap.get("attendancePo");
                    Double workDay = (Double) retMap.get("workDay");
                    Double workAddHour = (Double) retMap.get("workAddTimes");
                    Integer salary = (Integer) retMap.get("salary");
                    Double realSalary = (Double) retMap.get("realSalary");
                    List<String> times = (List<String>) retMap.get("times");
                    String time = "";
                    if (times != null && times.size() > 0) {
                        StringBuffer sb = new StringBuffer();
                        for (String s : times) {
                            sb.append(s).append(",");
                        }
                        time = sb.substring(0, sb.length() - 1);
                    }
                    if (workDay != null) {
                        workDays = workDays + workDay;
                    }
                    if (realSalary != null) {
                        realSalarys = realSalarys + realSalary;
                    }
                    if (workAddHour != null) {
                        workAddHours = workAddHours + workAddHour;
                    }
                    dataList[i][0] = ((i + 1) + "");
                    dataList[i][1] = attendancePo.getAttdate();
                    dataList[i][2] = attendancePo.getNumber();
                    dataList[i][3] = attendancePo.getAmontime();
                    dataList[i][4] = attendancePo.getAmofftime();
                    dataList[i][5] = attendancePo.getPmontime();
                    dataList[i][6] = attendancePo.getPmofftime();
                    dataList[i][7] = time;
                    dataList[i][8] = workDay == null ? "" : (workDay + "天");
                    dataList[i][9] = workAddHour == null ? "" : (workAddHour + "小時");
                    dataList[i][10] = salary == null ? "0" : (salary + "元");
                    dataList[i][11] = realSalary == null ? "0" : (realSalary + "元");
                }
                dataList[arrayList.size()][0] = ("合計");
                dataList[arrayList.size()][1] = ("");
                dataList[arrayList.size()][2] = ("");
                dataList[arrayList.size()][3] = ("");
                dataList[arrayList.size()][4] = ("");
                dataList[arrayList.size()][5] = ("");
                dataList[arrayList.size()][6] = ("");
                dataList[arrayList.size()][7] = ("");
                dataList[arrayList.size()][8] = (workDays + "天");
                dataList[arrayList.size()][9] = (workAddHours + "小時");
                dataList[arrayList.size()][10] = ("");
                dataList[arrayList.size()][11] = (realSalarys + "元");
                ExcelUtils.ExportWithResponse(sheetName, titleName, titleName2, fileName, columnNumber, columnWidth, columnName, dataList, response);
            } else {
                logger.error("获取個人考勤汇总数据失败");
            }
        } catch (Exception e) {
            logger.info("/app/excel/OneStaffAttendanceExcel 异常");
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/exceptionExcel", method = RequestMethod.GET)
    public void exceptionExcel(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.setAttribute("order", "asc");
            ReturnMsg returnMsg = exceptionSerive.getExceptionLog(request);
            if (returnMsg.getCode() == 1) {
                String timeBegin = request.getParameter("timeBegin");
                String timeEnd = request.getParameter("timeEnd");
                String userid = request.getParameter("userid");
                String username = usersService.getUserNameById(Integer.valueOf(userid));
                ArrayList<Object> arrayList = (ArrayList<Object>) returnMsg.getData();
                String sheetName = "異常信息表格";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = sdf.format(new Date());
                String titleName = "異常信息報表 (" + timeBegin + "至" + timeEnd + ")";
                String titleName2 = "打印人："+username+"                  打印時間：" + date;
                String fileName = "異常信息表格";
                int columnNumber = 8;
                int[] columnWidth = {8, 20, 18, 10, 15, 18, 13, 13};
                String[] columnName = {"No.", "日期", "工程編號", "工程名稱", "人員/工具/許可證", "名稱", "異常原因", "圖片"};
                String[][] dataList = new String[arrayList.size()][8];
                for (int i = 0; i < arrayList.size(); i++) {
                    HashMap<Object, Object> retMap = (HashMap<Object, Object>) arrayList.get(i);
                    AbnormalExceptionVo abnormalPo = (AbnormalExceptionVo) retMap.get("abnormalPo");
                    String objName = (String) retMap.get("objName");
                    dataList[i][0] = ((i + 1) + "");
                    dataList[i][1] = abnormalPo.getAbtime() + "";
                    dataList[i][2] = abnormalPo.getJobnum();
                    dataList[i][3] = abnormalPo.getEngineerName();
                    if (abnormalPo.getType() == 0) {
                        dataList[i][4] = "許可證";
                    } else if (abnormalPo.getType() == 1) {
                        dataList[i][4] = "員工";
                    } else if (abnormalPo.getType() == 2) {
                        dataList[i][4] = "工具";
                    }
                    dataList[i][5] = objName;
                    dataList[i][6] = abnormalPo.getReason();
                    dataList[i][7] = abnormalPo.getImageid();
                }
                ExcelUtils.ExportWithResponse(sheetName, titleName, titleName2, fileName, columnNumber, columnWidth, columnName, dataList, response);
            } else {
                logger.error("获取異常数据失败");
            }
        } catch (Exception e) {
            logger.info("/app/excel/exceptionExcel 异常");
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/expireExcel", method = RequestMethod.GET)
    public void expireExcel(HttpServletRequest request, HttpServletResponse response) {
        try {
            ReturnMsg returnMsg = staffsService.getExpireDataList(request);
            if (returnMsg.getCode() == 1) {
                String userid = request.getParameter("userid");
                String username = usersService.getUserNameById(Integer.valueOf(userid));
                ArrayList<Object> arrayList = (ArrayList<Object>) returnMsg.getData();
                String sheetName = "過期員工表格";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = sdf.format(new Date());
                String titleName = "過期員工報表";
                String titleName2 = "打印人：" + username + "                  打印時間：" + date;
                String fileName = "過期員工表格";
                int columnNumber = 7;
                int[] columnWidth = {8, 20, 18, 10, 15, 15, 18};
                String[] columnName = {"No.", "員工編號", "英文名稱", "中文名稱", "證件", "狀態", "過期時間"};
                String[][] dataList = new String[arrayList.size()][7];
                for (int i = 0; i < arrayList.size(); i++) {
                    HashMap<Object, Object> retMap = (HashMap<Object, Object>) arrayList.get(i);
                    StaffsPo staffsPo = (StaffsPo) retMap.get("staffsPo");
                    List<StaffscertPo> staffscertPos = (List<StaffscertPo>) retMap.get("staffscertPos");
                    String staffscertStatus = (String) retMap.get("staffscertStatus");
                    dataList[i][0] = ((i + 1) + "");
                    dataList[i][1] = staffsPo.getStaffid();
                    dataList[i][2] = staffsPo.getEnname();
                    dataList[i][3] = staffsPo.getChname();
                    dataList[i][4] = "綠卡";
                    dataList[i][5] = staffscertStatus;
                    dataList[i][6] = staffscertPos.get(0).getValidity();
                }
                ExcelUtils.ExportWithResponse(sheetName, titleName, titleName2, fileName, columnNumber, columnWidth, columnName, dataList, response);
            } else {
                logger.error("获取異常数据失败");
            }
        } catch (Exception e) {
            logger.info("/app/excel/expireExcel 异常");
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/warningExpireExcel", method = RequestMethod.GET)
    public void warningExpireExcel(HttpServletRequest request, HttpServletResponse response) {
        try {
            ReturnMsg returnMsg = staffsService.getExpireDataList(request);
            if (returnMsg.getCode() == 1) {
                String userid = request.getParameter("userid");
                String username = usersService.getUserNameById(Integer.valueOf(userid));
                ArrayList<Object> arrayList = (ArrayList<Object>) returnMsg.getData();
                String sheetName = "逾過期員工表格";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = sdf.format(new Date());
                String titleName = "逾過期員工報表";
                String titleName2 = "打印人：" + username + "                  打印時間：" + date;
                String fileName = "逾過期員工表格";
                int columnNumber = 7;
                int[] columnWidth = {8, 20, 18, 10, 15, 15, 18};
                String[] columnName = {"No.", "員工編號", "英文名稱", "中文名稱", "證件", "狀態", "過期時間"};
                String[][] dataList = new String[arrayList.size()][7];
                for (int i = 0; i < arrayList.size(); i++) {
                    HashMap<Object, Object> retMap = (HashMap<Object, Object>) arrayList.get(i);
                    StaffsPo staffsPo = (StaffsPo) retMap.get("staffsPo");
                    List<StaffscertPo> staffscertPos = (List<StaffscertPo>) retMap.get("staffscertPos");
                    String staffscertStatus = (String) retMap.get("staffscertStatus");
                    dataList[i][0] = ((i + 1) + "");
                    dataList[i][1] = staffsPo.getStaffid();
                    dataList[i][2] = staffsPo.getEnname();
                    dataList[i][3] = staffsPo.getChname();
                    dataList[i][4] = "綠卡";
                    dataList[i][5] = staffscertStatus;
                    dataList[i][6] = staffscertPos.get(0).getValidity();
                }
                ExcelUtils.ExportWithResponse(sheetName, titleName, titleName2, fileName, columnNumber, columnWidth, columnName, dataList, response);
            } else {
                logger.error("获取異常数据失败");
            }
        } catch (Exception e) {
            logger.info("/app/excel/warningExpireExcel 异常");
            e.printStackTrace();
        }
    }
}
