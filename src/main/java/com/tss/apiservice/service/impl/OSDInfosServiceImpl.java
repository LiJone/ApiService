package com.tss.apiservice.service.impl;

import com.tss.apiservice.common.PageUtil;
import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.dao.EngineerinfoPoMapper;
import com.tss.apiservice.dao.OSDInfosPoMapper;
import com.tss.apiservice.dao.UsersPoMapper;
import com.tss.apiservice.dto.OSDsDto;
import com.tss.apiservice.po.EngineerinfoPo;
import com.tss.apiservice.po.OSDInfosPo;
import com.tss.apiservice.service.OSDInfosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@Service
public class OSDInfosServiceImpl implements OSDInfosService {

    @Autowired
    private OSDInfosPoMapper osdInfosPoMapper;

    @Autowired
    private EngineerinfoPoMapper engineerinfoPoMapper;

    @Autowired
    private UsersPoMapper usersPoMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ReturnMsg<Object> addOSDinfos(String userid, OSDsDto osDsDto) {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");

        if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(osDsDto.getOsdid())) {
            returnMsg.setMsgbox("參數異常...");
            return returnMsg;
        }
        OSDInfosPo ret = osdInfosPoMapper.selectByPrimaryKey(osDsDto.getOsdid());
        if (ret == null) {
            OSDInfosPo osdInfosPo = new OSDInfosPo();
            //获取机构id
            String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
            osdInfosPo.setOrgid(orgid);
            osdInfosPo.setOsdid(osDsDto.getOsdid());
            osdInfosPo.setName(osDsDto.getName());
            osdInfosPoMapper.insertSelective(osdInfosPo);
            returnMsg = new ReturnMsg<>(ReturnMsg.SUCCESS, "成功");
        } else {
            returnMsg.setMsgbox("OSD盒子已存在...");
        }
        return returnMsg;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ReturnMsg updateOSDsMsg(String userid, OSDsDto osDsDto) {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        if (StringUtils.isEmpty(osDsDto.getOsdid()) || StringUtils.isEmpty(userid)) {
            returnMsg.setMsgbox("參數異常");
        } else {
            OSDInfosPo ret = osdInfosPoMapper.selectByPrimaryKey(osDsDto.getOsdid());
            if (ret == null) {
                returnMsg.setMsgbox("箱子不存在...");
            } else {
                OSDInfosPo osdInfosPo = new OSDInfosPo();

                //获取机构id
                String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
                osdInfosPo.setOrgid(orgid);
                osdInfosPo.setName(osDsDto.getName());
                osdInfosPo.setOsdid(osDsDto.getOsdid());
                osdInfosPoMapper.updateByPrimaryKeySelective(osdInfosPo);

                //修改了盒子，需要把工程中盒子名称也修改掉
                EngineerinfoPo engineerinfoPo = engineerinfoPoMapper.selectByOsdid(osDsDto.getOsdid());
                if(engineerinfoPo != null){
                    engineerinfoPo.setName(osDsDto.getName());
                    engineerinfoPoMapper.updateByPrimaryKeySelective(engineerinfoPo);
                }

                returnMsg.setCode(ReturnMsg.SUCCESS);
                returnMsg.setMsgbox("成功");
            }
        }
        return returnMsg;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ReturnMsg deleteOSDsMsg(String userid, OSDsDto osDsDto) {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        if (StringUtils.isEmpty(osDsDto.getOsdid()) || StringUtils.isEmpty(userid)) {
            returnMsg.setMsgbox("參數異常");
        } else {
            //删除盒子，需要判断盒子是否在工程中使用，如果使用，则不给删除
            EngineerinfoPo engineerinfoPo = engineerinfoPoMapper.selectByOsdid(osDsDto.getOsdid());
            if(engineerinfoPo != null){
                returnMsg.setMsgbox("盒子與工程已綁定，請先修改工程...");
                return returnMsg;
            }

            osdInfosPoMapper.deleteByPrimaryKey(osDsDto.getOsdid());
            returnMsg.setCode(ReturnMsg.SUCCESS);
            returnMsg.setMsgbox("成功");
        }
        return returnMsg;
    }

    @Override
    public ReturnMsg getOSDsMsgList(HttpServletRequest request) {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        String userid = request.getParameter("userid");
        String pageSize = request.getParameter("pageSize");
        String currentPage = request.getParameter("currentPage");
        String name = request.getParameter("name");
        if (StringUtils.isEmpty(userid)) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //查看许可证列表，根据许可证查找标签
            //获取机构id
            String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("orgid", orgid);
            if (!StringUtils.isEmpty(name)) {
                hashMap.put("name", name);
            }

            //根据带有符合条件的总数，进行分页查询的操作
            List<OSDInfosPo> osdInfosPos = osdInfosPoMapper.selectListByMap(hashMap);

            if (StringUtils.isEmpty(pageSize)) {
                pageSize = "10";
            }
            if (StringUtils.isEmpty(currentPage)) {
                currentPage = "1";
            }
            if (osdInfosPos.size() < 1) {
                returnMsg.setMsgbox("找不到符合條件數據...");
            } else {
                PageUtil pageUtil = new PageUtil(osdInfosPos.size(), Integer.valueOf(pageSize), Integer.valueOf(currentPage), 5);
                int startrow = pageUtil.getStartrow();
                int pagesize = pageUtil.getPagesize();
                hashMap.put("startrow", startrow);
                hashMap.put("pagesize", pagesize);
                List<OSDInfosPo> osdInfosPoList = osdInfosPoMapper.selectListByMap(hashMap);
                pageUtil.setPageData(osdInfosPoList);
                returnMsg = new ReturnMsg<>(ReturnMsg.SUCCESS, "成功", pageUtil);
            }
        }
        return returnMsg;
    }

    @Override
    public ReturnMsg getAllNum(String userid) {
        ReturnMsg returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        if (StringUtils.isEmpty(userid)) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //获取机构id
            String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
            List<Integer> allNum = osdInfosPoMapper.getAllNumByOrgId(orgid);
            returnMsg.setData(allNum);
            returnMsg.setCode(ReturnMsg.SUCCESS);
            returnMsg.setMsgbox("成功");
        }
        return returnMsg;
    }
}
