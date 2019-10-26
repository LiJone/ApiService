package com.tss.apiservice.service.impl;

import com.tss.apiservice.common.PageUtil;
import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.dao.DepositoryMapper;
import com.tss.apiservice.dao.UsersPoMapper;
import com.tss.apiservice.dto.DepositoryStatisticDTO;
import com.tss.apiservice.po.*;
import com.tss.apiservice.po.vo.AbnormalExceptionVo;
import com.tss.apiservice.service.DepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 壮Jone
 */
@Service
public class DepositoryServiceImpl implements DepositoryService {
    @Autowired
    private DepositoryMapper depositoryMapper;

    @Autowired
    UsersPoMapper usersPoMapper;

    @Override
    public ReturnMsg<Object> getDepositoryMsgList(HttpServletRequest request) throws ParseException {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        String userid = request.getParameter("userid");
        String pageSize = request.getParameter("pageSize");
        String currentPage = request.getParameter("currentPage");
        String numberBegin = request.getParameter("numberBegin");
        String numberEnd = request.getParameter("numberEnd");
        String osdname = request.getParameter("osdname");
        String toolname = request.getParameter("toolname");
        String excel = (String) request.getAttribute("excel");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        if (StringUtils.isEmpty(userid)) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //获取机构id
            String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
            //获取这个人的记录
            HashMap<Object, Object> map = new HashMap<>();
            map.put("orgid", orgid);

            if (!StringUtils.isEmpty(numberBegin)) {
                map.put("numberBegin", numberBegin);
            }
            if (!StringUtils.isEmpty(numberEnd)) {
                map.put("numberEnd", numberEnd);
            }
            if (!StringUtils.isEmpty(osdname)) {
                map.put("osdname", osdname);
            }
            if (!StringUtils.isEmpty(toolname)) {
                map.put("toolname", toolname);
            }
            if (StringUtils.isEmpty(pageSize)) {
                pageSize = "10";
            }
            if (StringUtils.isEmpty(currentPage)) {
                currentPage = "1";
            }
            PageUtil pageUtil = null;
            if (StringUtils.isEmpty(excel)) {
                List<DepositoryPO> count = depositoryMapper.selectListByMap(map);
                pageUtil = new PageUtil(count.size(), Integer.parseInt(pageSize), Integer.parseInt(currentPage), 5);
                int startrow = pageUtil.getStartrow();
                int pagesize = pageUtil.getPagesize();
                map.put("startrow", startrow);
                map.put("pagesize", pagesize);
            }
            List<DepositoryPO> depositoryPoList = depositoryMapper.selectListByMap(map);
            ArrayList<Object> list = new ArrayList<>();
            for (int i = 0; i < depositoryPoList.size(); i++) {
                map = new HashMap<>();
                map.put("depositoryPo", depositoryPoList.get(i));
                list.add(map);
            }
            returnMsg.setCode(ReturnMsg.SUCCESS);
            returnMsg.setMsgbox("成功");
            if (StringUtils.isEmpty(excel)) {
                assert pageUtil != null;
                pageUtil.setPageData(list);
                returnMsg.setData(pageUtil);
            } else {
                returnMsg.setData(list);
            }
        }
        return returnMsg;
    }

    @Override
    public ReturnMsg<Object> getDepositoryStatisticMsgList(HttpServletRequest request) {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        String userid = request.getParameter("userid");
        String numberBegin = request.getParameter("numberBegin");
        String numberEnd = request.getParameter("numberEnd");
        String osdname = request.getParameter("osdname");
        String osdid = request.getParameter("osdid");
        if (StringUtils.isEmpty(userid)) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //获取机构id
            String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
            HashMap<Object, Object> map = new HashMap<>();
            map.put("orgid", orgid);

            if (!StringUtils.isEmpty(numberBegin)) {
                map.put("numberBegin", numberBegin);
            }
            if (!StringUtils.isEmpty(numberEnd)) {
                map.put("numberEnd", numberEnd);
            }
            if (!StringUtils.isEmpty(osdname)) {
                map.put("osdname", osdname);
            }
            if (!StringUtils.isEmpty(osdid)) {
                map.put("osdid", osdid);
            }
            List<DepositoryPO> depositoryPoList = depositoryMapper.selectStatisticListByMap(map);
            Map<String, List<DepositoryPO>> collect = depositoryPoList.stream().collect(Collectors.groupingBy(DepositoryPO::getOsdid));
            ArrayList<Object> list = new ArrayList<>();
            for (String osdId : collect.keySet()) {
                List<DepositoryPO> depositoryPOS = collect.get(osdId);
                if (depositoryPOS != null && depositoryPOS.size() > 0) {
                    Map<Integer, List<DepositoryPO>> collect1 = depositoryPOS.stream().collect(Collectors.groupingBy(DepositoryPO::getTypeid));
                    List<DepositoryStatisticDTO> dtoList = new ArrayList<>();
                    for (List<DepositoryPO> typeId : collect1.values()) {
                        String[] s = typeId.get(0).getTime().split(" ");
                        DepositoryStatisticDTO statisticDTO = new DepositoryStatisticDTO();
                        statisticDTO.setDate(s[0]);
                        statisticDTO.setOsdId(osdId);
                        statisticDTO.setOsdName(depositoryPOS.get(0).getOsdname());
                        statisticDTO.setTypeName(typeId.get(0).getToolname());
                        statisticDTO.setCount(typeId.size());
                        dtoList.add(statisticDTO);
                    }
                    map = new HashMap<>();
                    map.put("statistic", dtoList);
                    map.put("count", dtoList.size());
                    list.add(map);
                }

            }
            returnMsg.setCode(ReturnMsg.SUCCESS);
            returnMsg.setMsgbox("成功");
            returnMsg.setData(list);
        }
        return returnMsg;
    }
}
