package com.tss.apiservice.dao;


import com.tss.apiservice.po.UsersSetPo;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersSetPoMapper {
    int deleteByPrimaryKey(Integer userid);

    int insertSelective(UsersSetPo record);

    //UsersSetPo selectByPrimaryKey(Integer userid);
    UsersSetPo selectByPrimaryKey(String orgid);

    int updateByPrimaryKeySelective(UsersSetPo record);
}