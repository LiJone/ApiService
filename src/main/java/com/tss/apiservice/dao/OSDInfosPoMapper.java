package com.tss.apiservice.dao;


import com.tss.apiservice.po.OSDInfosPo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface OSDInfosPoMapper {
    int deleteByPrimaryKey(String osdid);

    int insertSelective(OSDInfosPo record);

    OSDInfosPo selectByPrimaryKey(String osdid);

    List<OSDInfosPo> selectListByMap(Map HashMap);

    int updateByPrimaryKeySelective(OSDInfosPo record);

    List<String> getAllNumByOrgId(String orgid);

    List<String> getAllNameByOrgId(String orgId);
}