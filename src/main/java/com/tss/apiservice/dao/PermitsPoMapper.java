package com.tss.apiservice.dao;

import com.tss.apiservice.po.PermitsPo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface PermitsPoMapper {
    int deleteByPrimaryKey(String permitid);

    int insertSelective(PermitsPo record);

    PermitsPo selectByPrimaryKey(String permitid);

    PermitsPo selectByName(String name);

    List<PermitsPo> selectListByMap(Map HashMap);

    int updateByPrimaryKeySelective(PermitsPo record);

    List<PermitsPo> selectByPermitsPo(PermitsPo permitsPo);
}