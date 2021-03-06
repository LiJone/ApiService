package com.tss.apiservice.controller;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.dto.DepositoryStatisticDTO;
import com.tss.apiservice.po.*;
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

    @Autowired
    private PermitsService permitsService;

    @Autowired
    private ToolsService toolsService;

    @Autowired
    private DepositoryService depositoryService;

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
                int columnNumber = 9;
                int[] columnWidth = {8, 20, 18, 13, 15, 18, 13, 15, 30};
                String[] columnName = {"No.", "員工編號", "英文名", "中文名", "個人總天數", "個人加班總小時", "總薪酬", "osd編號", "osd名稱"};
                Double workDays = 0.0;
                Double workAddSalarys = 0.0;
                Float workAddHours = 0F;
                String[][] dataList = new String[arrayList.size() + 1][9];
                for (int i = 0; i < arrayList.size(); i++) {
                    HashMap<Object, Object> retMap = (HashMap<Object, Object>) arrayList.get(i);
                    StaffsPo staffsPo = (StaffsPo) retMap.get("staffsPo");
                    Double workDay = (Double) retMap.get("workDay");
                    Float workAddHour = (Float) retMap.get("workAddHour");
                    Double workAddSalary = (Double) retMap.get("workAddSalary");
                    String osdnum = (String) retMap.get("osdnum");
                    String osdname = (String) retMap.get("osdname");
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
                    dataList[i][7] = (osdnum);
                    dataList[i][8] = (osdname);
                }
                dataList[arrayList.size()][0] = ("合計");
                dataList[arrayList.size()][1] = ("");
                dataList[arrayList.size()][2] = ("");
                dataList[arrayList.size()][3] = ("");
                dataList[arrayList.size()][4] = (workDays + "天");
                dataList[arrayList.size()][5] = (workAddHours + "小時");
                dataList[arrayList.size()][6] = (workAddSalarys + "元");
                dataList[arrayList.size()][7] = ("");
                dataList[arrayList.size()][8] = ("");
                ExcelUtils.exportWithResponse(sheetName, titleName, titleName2, fileName, columnNumber, columnWidth, columnName, dataList, response);
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
                int columnNumber = 14;
                int[] columnWidth = {8, 18, 20, 10, 15, 18, 13, 18, 18, 10, 15, 18, 15, 30};
                String[] columnName = {"No.", "日期", "員工編號", "上午上班時間", "上午下班時間", "下午上班時間",
                        "下午下班時間", "晚上加班時間", "上班天數", "加班小時", "每日薪酬", "實際薪酬", "osd編號", "osd名稱"};
                Double workDays = 0.0;
                Double realSalarys = 0.0;
                Double workAddHours = 0.0;
                String[][] dataList = new String[arrayList.size() + 1][14];
                for (int i = 0; i < arrayList.size(); i++) {
                    HashMap<Object, Object> retMap = (HashMap<Object, Object>) arrayList.get(i);
                    AttendancePo attendancePo = (AttendancePo) retMap.get("attendancePo");
                    Double workDay = (Double) retMap.get("workDay");
                    Double workAddHour = (Double) retMap.get("workAddDay");
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
                    dataList[i][12] = attendancePo.getOsdnum();
                    dataList[i][13] = attendancePo.getOsdname();
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
                dataList[arrayList.size()][12] = ("");
                dataList[arrayList.size()][13] = ("");
                ExcelUtils.exportWithResponse(sheetName, titleName, titleName2, fileName, columnNumber, columnWidth, columnName, dataList, response);
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
            request.setAttribute("excel", "1");
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
                    dataList[i][1] = sdf.format(abnormalPo.getAbtime());
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
                ExcelUtils.exportWithResponse(sheetName, titleName, titleName2, fileName, columnNumber, columnWidth, columnName, dataList, response);
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
                    StaffscertPo staffscertPo = (StaffscertPo) retMap.get("staffscertPo");
                    String staffscertStatus = (String) retMap.get("staffscertStatus");
                    dataList[i][0] = ((i + 1) + "");
                    dataList[i][1] = staffsPo.getStaffid();
                    dataList[i][2] = staffsPo.getEnname();
                    dataList[i][3] = staffsPo.getChname();
                    dataList[i][4] = staffscertPo.getTypename();
                    dataList[i][5] = staffscertStatus;
                    dataList[i][6] = staffscertPo.getValidity();
                }
                ExcelUtils.exportWithResponse(sheetName, titleName, titleName2, fileName, columnNumber, columnWidth, columnName, dataList, response);
            } else {
                logger.error("获取過期員工数据失败");
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
                String expireNumber = request.getParameter("expireNumber");
                String username = usersService.getUserNameById(Integer.valueOf(userid));
                ArrayList<Object> arrayList = (ArrayList<Object>) returnMsg.getData();
                String sheetName = expireNumber + "個月逾過期員工表格";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = sdf.format(new Date());
                String titleName = expireNumber + "個月逾過期員工報表";
                String titleName2 = "打印人：" + username + "                  打印時間：" + date;
                String fileName = expireNumber + "個月逾過期員工表格";
                int columnNumber = 7;
                int[] columnWidth = {8, 20, 18, 10, 15, 15, 18};
                String[] columnName = {"No.", "員工編號", "英文名稱", "中文名稱", "證件", "狀態", "過期時間"};
                String[][] dataList = new String[arrayList.size()][7];
                for (int i = 0; i < arrayList.size(); i++) {
                    HashMap<Object, Object> retMap = (HashMap<Object, Object>) arrayList.get(i);
                    StaffsPo staffsPo = (StaffsPo) retMap.get("staffsPo");
                    StaffscertPo staffscertPo = (StaffscertPo) retMap.get("staffscertPo");
                    String staffscertStatus = (String) retMap.get("staffscertStatus");
                    dataList[i][0] = ((i + 1) + "");
                    dataList[i][1] = staffsPo.getStaffid();
                    dataList[i][2] = staffsPo.getEnname();
                    dataList[i][3] = staffsPo.getChname();
                    dataList[i][4] = staffscertPo.getTypename();
                    dataList[i][5] = staffscertStatus;
                    dataList[i][6] = staffscertPo.getValidity();
                }
                ExcelUtils.exportWithResponse(sheetName, titleName, titleName2, fileName, columnNumber, columnWidth, columnName, dataList, response);
            } else {
                logger.error("获取逾過期員工数据失败");
            }
        } catch (Exception e) {
            logger.info("/app/excel/warningExpireExcel 异常");
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/expirePermitsExcel", method = RequestMethod.GET)
    public void expirePermitsExcel(HttpServletRequest request, HttpServletResponse response) {
        try {
            ReturnMsg returnMsg = permitsService.getExpireDataList(request);
            if (returnMsg.getCode() == 1) {
                String userid = request.getParameter("userid");
                String username = usersService.getUserNameById(Integer.valueOf(userid));
                ArrayList<Object> arrayList = (ArrayList<Object>) returnMsg.getData();
                String sheetName = "過期許可證表格";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = sdf.format(new Date());
                String titleName = "過期許可證報表";
                String titleName2 = "打印人：" + username + "                  打印時間：" + date;
                String fileName = "過期許可證表格";
                int columnNumber = 7;
                int[] columnWidth = {8, 20, 18, 10, 15, 15, 18};
                String[] columnName = {"No.", "許可證編號", "許可證名稱", "許可證種類名稱", "開始日期", "結束日期", "狀態"};
                String[][] dataList = new String[arrayList.size()][7];
                for (int i = 0; i < arrayList.size(); i++) {
                    HashMap<Object, Object> retMap = (HashMap<Object, Object>) arrayList.get(i);
                    PermitsPo permitsPo = (PermitsPo) retMap.get("permitsPo");
                    String permitsStatus = (String) retMap.get("permitsStatus");
                    dataList[i][0] = ((i + 1) + "");
                    dataList[i][1] = permitsPo.getPermitid();
                    dataList[i][2] = permitsPo.getName();
                    dataList[i][3] = permitsPo.getTypename();
                    dataList[i][4] = permitsPo.getStartdate();
                    dataList[i][5] = permitsPo.getEnddate();
                    dataList[i][6] = permitsStatus;
                }
                ExcelUtils.exportWithResponse(sheetName, titleName, titleName2, fileName, columnNumber, columnWidth, columnName, dataList, response);
            } else {
                logger.error("获取過期許可證数据失败");
            }
        } catch (Exception e) {
            logger.info("/app/excel/expirePermitsExcel 异常");
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/warningPermitsExpireExcel", method = RequestMethod.GET)
    public void warningPermitsExpireExcel(HttpServletRequest request, HttpServletResponse response) {
        try {
            ReturnMsg returnMsg = permitsService.getExpireDataList(request);
            if (returnMsg.getCode() == 1) {
                String userid = request.getParameter("userid");
                String username = usersService.getUserNameById(Integer.valueOf(userid));
                ArrayList<Object> arrayList = (ArrayList<Object>) returnMsg.getData();
                String sheetName = "逾過期許可證表格";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = sdf.format(new Date());
                String titleName = "逾過期許可證報表";
                String titleName2 = "打印人：" + username + "                  打印時間：" + date;
                String fileName = "逾過期許可證表格";
                int columnNumber = 7;
                int[] columnWidth = {8, 20, 18, 10, 15, 15, 18};
                String[] columnName = {"No.", "許可證編號", "許可證名稱", "許可證種類名稱", "開始日期", "結束日期", "狀態"};
                String[][] dataList = new String[arrayList.size()][7];
                for (int i = 0; i < arrayList.size(); i++) {
                    HashMap<Object, Object> retMap = (HashMap<Object, Object>) arrayList.get(i);
                    PermitsPo permitsPo = (PermitsPo) retMap.get("permitsPo");
                    String permitsStatus = (String) retMap.get("permitsStatus");
                    dataList[i][0] = ((i + 1) + "");
                    dataList[i][1] = permitsPo.getPermitid();
                    dataList[i][2] = permitsPo.getName();
                    dataList[i][3] = permitsPo.getTypename();
                    dataList[i][4] = permitsPo.getStartdate();
                    dataList[i][5] = permitsPo.getEnddate();
                    dataList[i][6] = permitsStatus;
                }
                ExcelUtils.exportWithResponse(sheetName, titleName, titleName2, fileName, columnNumber, columnWidth, columnName, dataList, response);
            } else {
                logger.error("获取逾過期許可證数据失败");
            }
        } catch (Exception e) {
            logger.info("/app/excel/warningPermitsExpireExcel 异常");
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/expireToolsExcel", method = RequestMethod.GET)
    public void expireToolsExcel(HttpServletRequest request, HttpServletResponse response) {
        try {
            ReturnMsg returnMsg = toolsService.getExpireDataList(request);
            if (returnMsg.getCode() == 1) {
                String userid = request.getParameter("userid");
                String username = usersService.getUserNameById(Integer.valueOf(userid));
                ArrayList<Object> arrayList = (ArrayList<Object>) returnMsg.getData();
                String sheetName = "過期工具表格";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = sdf.format(new Date());
                String titleName = "過期工具報表";
                String titleName2 = "打印人：" + username + "                  打印時間：" + date;
                String fileName = "過期工具表格";
                int columnNumber = 5;
                int[] columnWidth = {8, 20, 18, 10, 18};
                String[] columnName = {"No.", "工具編號", "工具名稱", "狀態", "過期時間"};
                String[][] dataList = new String[arrayList.size()][5];
                for (int i = 0; i < arrayList.size(); i++) {
                    HashMap<Object, Object> retMap = (HashMap<Object, Object>) arrayList.get(i);
                    ToolsPo toolsPo = (ToolsPo) retMap.get("toolsPo");
                    String toolStatus = (String) retMap.get("toolStatus");
                    dataList[i][0] = ((i + 1) + "");
                    dataList[i][1] = toolsPo.getToolid();
                    dataList[i][2] = toolsPo.getName();
                    dataList[i][3] = toolStatus;
                    dataList[i][4] = toolsPo.getValidity();
                }
                ExcelUtils.exportWithResponse(sheetName, titleName, titleName2, fileName, columnNumber, columnWidth, columnName, dataList, response);
            } else {
                logger.error("获取過期工具数据失败");
            }
        } catch (Exception e) {
            logger.info("/app/excel/expireToolsExcel 异常");
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/warningToolsExpireExcel", method = RequestMethod.GET)
    public void warningToolsExpireExcel(HttpServletRequest request, HttpServletResponse response) {
        try {
            ReturnMsg returnMsg = toolsService.getExpireDataList(request);
            if (returnMsg.getCode() == 1) {
                String userid = request.getParameter("userid");
                String username = usersService.getUserNameById(Integer.valueOf(userid));
                ArrayList<Object> arrayList = (ArrayList<Object>) returnMsg.getData();
                String sheetName = "逾過期工具表格";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = sdf.format(new Date());
                String titleName = "逾過期工具報表";
                String titleName2 = "打印人：" + username + "                  打印時間：" + date;
                String fileName = "逾過期工具表格";
                int columnNumber = 5;
                int[] columnWidth = {8, 20, 18, 10, 18};
                String[] columnName = {"No.", "工具編號", "工具名稱", "狀態", "過期時間"};
                String[][] dataList = new String[arrayList.size()][5];
                for (int i = 0; i < arrayList.size(); i++) {
                    HashMap<Object, Object> retMap = (HashMap<Object, Object>) arrayList.get(i);
                    ToolsPo toolsPo = (ToolsPo) retMap.get("toolsPo");
                    String toolStatus = (String) retMap.get("toolStatus");
                    dataList[i][0] = ((i + 1) + "");
                    dataList[i][1] = toolsPo.getToolid();
                    dataList[i][2] = toolsPo.getName();
                    dataList[i][3] = toolStatus;
                    dataList[i][4] = toolsPo.getValidity();
                }
                ExcelUtils.exportWithResponse(sheetName, titleName, titleName2, fileName, columnNumber, columnWidth, columnName, dataList, response);
            } else {
                logger.error("获取逾過期工具数据失败");
            }
        } catch (Exception e) {
            logger.info("/app/excel/warningToolsExpireExcel 异常");
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/depositoryExcel", method = RequestMethod.GET)
    public void depositoryExcel(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.setAttribute("excel", "1");
            ReturnMsg returnMsg = depositoryService.getDepositoryMsgList(request);
            if (returnMsg.getCode() == 1) {
                String userid = request.getParameter("userid");
                String username = usersService.getUserNameById(Integer.valueOf(userid));
                String now = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                ArrayList<Object> arrayList = (ArrayList<Object>) returnMsg.getData();
                String sheetName = "倉庫信息表格";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = sdf.format(new Date());
                String titleName = "倉庫信息報表 (" + now + ")";
                String titleName2 = "打印人："+username+"                  打印時間：" + date;
                String fileName = "倉庫信息表格";
                int columnNumber = 7;
                int[] columnWidth = {8, 20, 18, 18, 15, 18, 8};
                String[] columnName = {"No.", "日期", "倉庫編號", "倉庫名稱", "工具編號", "工具名稱", "數量"};
                String[][] dataList = new String[arrayList.size() + 1][7];
                for (int i = 0; i < arrayList.size(); i++) {
                    HashMap<Object, Object> retMap = (HashMap<Object, Object>) arrayList.get(i);
                    DepositoryPO depositoryPo = (DepositoryPO) retMap.get("depositoryPo");
                    dataList[i][0] = ((i + 1) + "");
                    dataList[i][1] = depositoryPo.getTime();
                    dataList[i][2] = depositoryPo.getOsdid();
                    dataList[i][3] = depositoryPo.getOsdname();
                    dataList[i][4] = depositoryPo.getToolid();
                    dataList[i][5] = depositoryPo.getToolname();
                    dataList[i][6] = "1";
                }
                dataList[arrayList.size()][0] = ("合計");
                dataList[arrayList.size()][1] = ("");
                dataList[arrayList.size()][2] = ("");
                dataList[arrayList.size()][3] = ("");
                dataList[arrayList.size()][4] = ("");
                dataList[arrayList.size()][5] = ("");
                dataList[arrayList.size()][6] = arrayList.size() + "個";
                ExcelUtils.exportWithResponse(sheetName, titleName, titleName2, fileName, columnNumber, columnWidth, columnName, dataList, response);
            } else {
                logger.error("获取倉庫数据失败");
            }
        } catch (Exception e) {
            logger.info("/app/excel/depositoryExcel 异常");
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/depositoryStatisticExcel", method = RequestMethod.GET)
    public void depositoryStatisticExcel(HttpServletRequest request, HttpServletResponse response) {
        try {
            ReturnMsg<Object> returnMsg = depositoryService.getDepositoryStatisticMsgList(request);
            if (returnMsg.getCode() == 1) {
                String userid = request.getParameter("userid");
                String username = usersService.getUserNameById(Integer.valueOf(userid));
                String now = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                ArrayList<Object> arrayList = (ArrayList<Object>) returnMsg.getData();
                int allRow = Integer.parseInt(returnMsg.getMsgbox());
                String sheetName = "倉庫統計信息表格";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = sdf.format(new Date());
                String titleName = "倉庫統計信息報表 (" + now + ")";
                String titleName2 = "打印人："+username+"                  打印時間：" + date;
                String fileName = "倉庫統計信息表格";
                int columnNumber = 6;
                int[] columnWidth = {8, 20, 18, 18, 15, 8};
                String[] columnName = {"No.", "日期", "倉庫編號", "倉庫名稱", "工具種類名稱", "數量"};
                String[][] dataList = new String[allRow + 1][6];
                int number = 0;
                int allCount = 0;
                for (int i = 0; i < arrayList.size(); i++) {
                    HashMap<Object, Object> retMap = (HashMap<Object, Object>) arrayList.get(i);
                    List<DepositoryStatisticDTO> dtoList = (List<DepositoryStatisticDTO>) retMap.get("statistic");
                    Integer count = (Integer) retMap.get("count");
                    for (int j = 0; j < dtoList.size(); j++) {
                        if (i == 0) {
                            number = j;
                        } else {
                            number = number + 1;
                        }
                        DepositoryStatisticDTO statisticDTO = dtoList.get(j);
                        dataList[number][0] = ((i + 1) + "");
                        dataList[number][1] = statisticDTO.getDate();
                        dataList[number][2] = statisticDTO.getOsdId();
                        dataList[number][3] = statisticDTO.getOsdName();
                        dataList[number][4] = statisticDTO.getTypeName();
                        dataList[number][5] = statisticDTO.getCount() + "個";
                    }
                    number++;
                    dataList[number][0] = ("倉庫合計");
                    dataList[number][1] = ("");
                    dataList[number][2] = ("");
                    dataList[number][3] = ("");
                    dataList[number][4] = ("");
                    dataList[number][5] = (count + "個");
                    allCount = allCount + count;
                }
                dataList[allRow][0] = ("全倉統計");
                dataList[allRow][1] = ("");
                dataList[allRow][2] = ("");
                dataList[allRow][3] = ("");
                dataList[allRow][4] = ("");
                dataList[allRow][5] = (allCount + "個");
                ExcelUtils.exportWithResponse(sheetName, titleName, titleName2, fileName, columnNumber, columnWidth, columnName, dataList, response);
            } else {
                logger.error("获取倉庫統計数据失败");
            }
        } catch (Exception e) {
            logger.info("/app/excel/depositoryStatisticExcel 异常");
            e.printStackTrace();
        }
    }
}
