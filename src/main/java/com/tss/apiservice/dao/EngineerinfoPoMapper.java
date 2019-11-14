package com.tss.apiservice.dao;

import com.tss.apiservice.po.EngineerinfoPo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface EngineerinfoPoMapper {
    int deleteByPrimaryKey(Integer id);

    int deleteByOsdid(String osdid);

    int insertSelective(EngineerinfoPo record);

    EngineerinfoPo selectByPrimaryKey(Integer id);

    EngineerinfoPo selectByJobNum(String jobnum);

    EngineerinfoPo selectByOsdid(String osdid);

    List<EngineerinfoPo> selectListByMap(Map hashMap);

    int updateByPrimaryKeySelective(EngineerinfoPo record);

    List<String> getAllNumByOrgId(String orgid);
}