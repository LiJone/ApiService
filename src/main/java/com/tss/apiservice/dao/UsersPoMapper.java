package com.tss.apiservice.dao;

import com.tss.apiservice.po.UsersPo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface UsersPoMapper {
    int deleteByPrimaryKey(Integer userid);

    List<UsersPo> selectListByMap(Map HashMap);

    int insertSelective(UsersPo record);

    UsersPo selectByPrimaryKey(Integer userid);

    UsersPo selectByUserName(String username);

    UsersPo selectByNamePassword(String username, String password);

    int updateByPrimaryKeySelective(UsersPo record);
    /**
     * 获取机构id
     * @param userid
     * @return
     */
    String selectOrgIdByUserId(Integer userid);
}