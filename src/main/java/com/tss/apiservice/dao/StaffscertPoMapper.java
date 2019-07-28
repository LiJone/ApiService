package com.tss.apiservice.dao;


import com.tss.apiservice.po.StaffscertPo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffscertPoMapper {
    int deleteByPrimaryKey(String certid);

    int insertSelective(StaffscertPo record);

    StaffscertPo selectByPrimaryKey(String certid);

    List<StaffscertPo> selectByStaffid(String staffid);

    List<StaffscertPo> selectByStaffidValid(String staffid);

    int updateByPrimaryKeySelective(StaffscertPo record);
}