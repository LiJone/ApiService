package com.tss.apiservice.dao;


import com.tss.apiservice.po.UsersPowerPo;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersPowerPoMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(UsersPowerPo record);

    UsersPowerPo selectByPrimaryKey(Integer id);

    UsersPowerPo selectByUserid(int userid);

    int updateByPrimaryKeySelective(UsersPowerPo record);
}