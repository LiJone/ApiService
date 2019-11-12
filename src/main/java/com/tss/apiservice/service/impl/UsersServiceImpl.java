package com.tss.apiservice.service.impl;

import com.tss.apiservice.common.PageUtil;
import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.common.utils.MQAllSendMessage;
import com.tss.apiservice.common.utils.MQCode;
import com.tss.apiservice.dao.UsersPoMapper;
import com.tss.apiservice.dao.UsersPowerPoMapper;
import com.tss.apiservice.dao.UsersSetPoMapper;
import com.tss.apiservice.dto.UsersDto;
import com.tss.apiservice.dto.UsersSetDto;
import com.tss.apiservice.po.UsersPo;
import com.tss.apiservice.po.UsersPowerPo;
import com.tss.apiservice.po.UsersSetPo;
import com.tss.apiservice.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class UsersServiceImpl implements UsersService {

    @Autowired
    UsersPoMapper usersPoMapper;

    @Autowired
    UsersPowerPoMapper usersPowerPoMapper;

    @Autowired
    UsersSetPoMapper usersSetPoMapper;

    @Autowired
    ApiServiceMQImpl apiServiceMQ;

    @Override
    public ReturnMsg userLogin(String username, String password) {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            UsersPo usersPo = usersPoMapper.selectByUserName(username);
            if (usersPo == null) {
                returnMsg.setMsgbox("用戶不存在...");
            } else {
                usersPo = usersPoMapper.selectByNamePassword(username, password);
                if (usersPo == null) {
                    returnMsg.setMsgbox("密碼錯誤...");
                } else {
                    //登录成功
                    usersPo.setPassword(null);
                    usersPo.setOrgid(null);
                    returnMsg = new ReturnMsg<>(ReturnMsg.SUCCESS, "成功", usersPo);
                }
            }
        }
        return returnMsg;
    }

    @Override
    public ReturnMsg getUserList(HttpServletRequest request) {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        String userid = request.getParameter("userid");
        String pageSize = request.getParameter("pageSize");
        String currentPage = request.getParameter("currentPage");
        String username = request.getParameter("username");

        //企业编码修改
        UsersPo usersPo = usersPoMapper.selectByPrimaryKey(Integer.valueOf(userid));
//        String superiorid = null;
//        if (usersPo.getLevel() == 1) {
//            superiorid = userid;
//        } else {
//            superiorid = usersPo.getSuperiorid().toString();
//        }

        //获取用户列表，必须获取自己下级的用户列表
        if (StringUtils.isEmpty(/*superiorid*/usersPo.getOrgid())) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //查看工具列表，根据工具查找标签，查找工具证件
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("orgid", usersPo.getOrgid());

            if (!StringUtils.isEmpty(username)) {
                hashMap.put("username", username);
            }

            //根据带有符合条件的总数，进行分页查询的操作
            List<UsersPo> usersPos = usersPoMapper.selectListByMap(hashMap);
            if (StringUtils.isEmpty(pageSize)) {
                pageSize = "10";
            }
            if (StringUtils.isEmpty(currentPage)) {
                currentPage = "1";
            }
            if (usersPos.size() < 1) {
                returnMsg.setMsgbox("找不到符合條件數據...");
            } else {
                PageUtil pageUtil = new PageUtil(usersPos.size(), Integer.valueOf(pageSize), Integer.valueOf(currentPage), 5);
                int startrow = pageUtil.getStartrow();
                int pagesize = pageUtil.getPagesize();
                hashMap.put("startrow", startrow);
                hashMap.put("pagesize", pagesize);
                List<UsersPo> usersPosList = usersPoMapper.selectListByMap(hashMap);

                HashMap<String, Object> retMap = null;
                ArrayList<Object> arrayList = new ArrayList<>();
                UsersPowerPo usersPowerPo = null;
                for (int i = 0; i < usersPosList.size(); i++) {
                    retMap = new HashMap<>();
                    usersPowerPo = usersPowerPoMapper.selectByUserid(usersPosList.get(i).getUserid());
                    retMap.put("usersPowerPo", usersPowerPo);
                    retMap.put("usersPo", usersPosList.get(i));
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
    public ReturnMsg addUser(String userid, UsersDto usersDto) {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");

        UsersPo usersPo = null;
        if (StringUtils.isEmpty(userid) ||
                StringUtils.isEmpty(usersDto.getUsername()) ||
                StringUtils.isEmpty(usersDto.getPassword()) ||
                StringUtils.isEmpty(usersDto.getPower()) ||
                StringUtils.isEmpty(usersDto.getOsdnum())) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //权限值 70 80 以上的用户都拥有
            usersPo = usersPoMapper.selectByUserName(usersDto.getUsername());
            if (usersPo == null) {
                //获取添加人的用户信息和权限
                UsersPo usersPo_Make = usersPoMapper.selectByPrimaryKey(Integer.valueOf(userid));
                UsersPowerPo usersPowerPo_Make = usersPowerPoMapper.selectByUserid(Integer.valueOf(userid));
                if (usersPowerPo_Make.getPower() >= 60) {
                    usersPo = new UsersPo();
                    usersPo.setUsername(usersDto.getUsername());
                    usersPo.setPassword(usersDto.getPassword());
                    usersPo.setLevel(2);
//                    if (usersPo_Make.getLevel() == 1) {
//                        //操作用户为企业管理员，非企业普通人员
//                        usersPo.setSuperiorid(Integer.valueOf(userid));
//                    } else {
//                        usersPo.setSuperiorid(usersPo_Make.getSuperiorid());
//                    }

                    //获取机构id
                    String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
                    usersPo.setOrgid(orgid);
                    usersPoMapper.insertSelective(usersPo);
                    usersPo = usersPoMapper.selectByUserName(usersPo.getUsername());

                    UsersPowerPo usersPowerPo = new UsersPowerPo();
                    usersPowerPo.setUserid(usersPo.getUserid());
                    usersPowerPo.setPower(Integer.valueOf(usersDto.getPower()));
                    usersPowerPo.setOsdid(usersDto.getOsdid());
                    usersPowerPo.setOsdnum(Integer.valueOf(usersDto.getOsdnum()));
                    usersPowerPoMapper.insertSelective(usersPowerPo);

                    returnMsg.setMsgbox("成功");
                    returnMsg.setCode(ReturnMsg.SUCCESS);
                } else {
                    returnMsg.setMsgbox("用戶權限不夠...");
                }
            } else {
                returnMsg.setMsgbox("用戶名已存在...");
            }
        }
        return returnMsg;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ReturnMsg updateUser(String userid, UsersDto usersDto) {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");

        UsersPo usersPo = null;
        if (StringUtils.isEmpty(userid) ||
                StringUtils.isEmpty(usersDto.getUsername()) ||
                StringUtils.isEmpty(usersDto.getPassword()) ||
                StringUtils.isEmpty(usersDto.getPower()) ||
                StringUtils.isEmpty(usersDto.getOsdnum())) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            //需要修改的用户
            usersPo = usersPoMapper.selectByPrimaryKey(Integer.valueOf(usersDto.getUserid()));
            if (usersPo != null) {
                //修改人必须是权限大于 60以上才可以修改
                UsersPowerPo usersPowerPo_Make = usersPowerPoMapper.selectByUserid(Integer.valueOf(userid));
                if (usersPowerPo_Make.getPower() >= 70) {
                    usersPo.setUsername(usersDto.getUsername());
                    usersPo.setPassword(usersDto.getPassword());

                    UsersPo usersPo_make = usersPoMapper.selectByPrimaryKey(Integer.valueOf(userid));
//                    if (usersPo_make.getLevel() == 1) {
////                        usersPo.setSuperiorid(Integer.valueOf(userid));
////                    } else {
////                        usersPo.setSuperiorid(usersPo_make.getSuperiorid());
////                    }

                    usersPo.setLevel(2);
                    usersPoMapper.updateByPrimaryKeySelective(usersPo);

                    UsersPowerPo usersPowerPo = usersPowerPoMapper.selectByUserid(usersPo.getUserid());
                    usersPowerPo.setUserid(usersPo.getUserid());
                    usersPowerPo.setPower(Integer.valueOf(usersDto.getPower()));
                    usersPowerPo.setOsdid(usersDto.getOsdid());
                    usersPowerPo.setOsdnum(Integer.valueOf(usersDto.getOsdnum()));
                    usersPowerPoMapper.updateByPrimaryKeySelective(usersPowerPo);
                    returnMsg.setMsgbox("成功");
                    returnMsg.setCode(ReturnMsg.SUCCESS);

                } else {
                    returnMsg.setMsgbox("用戶權限不夠...");
                }
            } else {
                returnMsg.setMsgbox("用戶不存在...");
            }
        }
        return returnMsg;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ReturnMsg deleteUser(String userid, UsersDto usersDto) {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");

        if (StringUtils.isEmpty(userid) ||
                StringUtils.isEmpty(usersDto.getUserid())) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            UsersPowerPo usersPowerPo = usersPowerPoMapper.selectByUserid(Integer.valueOf(userid));//使用者权限
//            UsersPo usersPoUser = usersPoMapper.selectByPrimaryKey(Integer.valueOf(userid));//使用者
//            UsersPo usersPoNoUser = usersPoMapper.selectByPrimaryKey(Integer.valueOf(usersDto.getUserid()));//被使用着
            if (usersPowerPo.getPower() >= 60) {
                usersPoMapper.deleteByPrimaryKey(Integer.valueOf(usersDto.getUserid()));
                UsersPowerPo usersPowerPoNouser = usersPowerPoMapper.selectByUserid(Integer.valueOf(usersDto.getUserid()));
                usersPowerPoMapper.deleteByPrimaryKey(usersPowerPoNouser.getId());
                returnMsg.setMsgbox("成功");
                returnMsg.setCode(ReturnMsg.SUCCESS);
            } else {
                returnMsg.setMsgbox("用戶權限不夠...");
            }
        }
        return returnMsg;
    }

    @Override
    public ReturnMsg getUserPower(HttpServletRequest request) {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        String userid = request.getParameter("userid");

        UsersPo usersPo = null;
        if (StringUtils.isEmpty(userid)) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            UsersPowerPo usersPowerPo = usersPowerPoMapper.selectByUserid(Integer.valueOf(userid));
            returnMsg.setMsgbox("成功");
            returnMsg.setCode(ReturnMsg.SUCCESS);
            returnMsg.setData(usersPowerPo);
        }
        return returnMsg;
    }

    @Override
    public ReturnMsg getUserSetting(HttpServletRequest request) {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        String userid = request.getParameter("userid");

        UsersPo usersPo = null;
        if (StringUtils.isEmpty(userid)) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            UsersPo usersPo_Make = usersPoMapper.selectByPrimaryKey(Integer.valueOf(userid));
            UsersSetPo usersSetPo = usersSetPoMapper.selectByPrimaryKey(usersPo_Make.getOrgid());
//            if (usersPo_Make.getLevel() == 1) {
//                usersSetPo = usersSetPoMapper.selectByPrimaryKey(Integer.valueOf(userid));
//            } else if (usersPo_Make.getLevel() == 2) {
//                usersSetPo = usersSetPoMapper.selectByPrimaryKey(usersPo_Make.getSuperiorid());
//            }
            //获取机构id
            //String orgid = usersPoMapper.selectOrgIdByUserId(Integer.parseInt(userid));
           // usersPo.setOrgid(orgid);
            returnMsg.setMsgbox("成功");
            returnMsg.setCode(ReturnMsg.SUCCESS);
            returnMsg.setData(usersSetPo.getTimeout());
        }
        return returnMsg;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ReturnMsg setUserSetting(String userid, UsersSetDto usersSetDto) throws ParseException {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        if (StringUtils.isEmpty(userid)) {
            returnMsg.setMsgbox("參數異常...");
        } else {

            UsersSetPo usersSetPo = new UsersSetPo();

            //企业编码
            UsersPo usersPo = usersPoMapper.selectByPrimaryKey(Integer.valueOf(userid));
//            if (usersPo.getLevel() == 1) {
//                usersSetPo.setUserid(Integer.valueOf(userid));
//            } else {
//                usersSetPo.setUserid(usersPo.getSuperiorid());
//            }
            usersSetPo.setOrgid(usersPo.getOrgid());

            if (!StringUtils.isEmpty(usersSetDto.getTimeout())) {
                usersSetPo.setTimeout(usersSetDto.getTimeout());
            }
            UsersSetPo usersSetPoOld = usersSetPoMapper.selectByPrimaryKey(usersPo.getOrgid());
            if (usersSetPoOld == null) {
                usersSetPoMapper.insertSelective(usersSetPo);
            } else {
                usersSetPoMapper.updateByPrimaryKeySelective(usersSetPo);
            }
            //发送修改到MQ
            MQAllSendMessage.sendUserSettingToMq(usersPo.getOrgid(), usersSetDto.getTimeout(), MQCode.USER_SETTING_UPDATE, apiServiceMQ);
            returnMsg.setMsgbox("成功");
            returnMsg.setCode(ReturnMsg.SUCCESS);

        }
        return returnMsg;
    }

    @Override
    public String getUserNameById(Integer userid) {
        UsersPo usersPo = usersPoMapper.selectByPrimaryKey(userid);
        return usersPo.getUsername();
    }
}
