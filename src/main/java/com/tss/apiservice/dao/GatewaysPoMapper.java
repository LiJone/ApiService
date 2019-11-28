package com.tss.apiservice.dao;

import com.tss.apiservice.po.GatewaysPo;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface GatewaysPoMapper {
    int deleteByPrimaryKey(String number);

    int insertSelective(GatewaysPo record);

    GatewaysPo selectByPrimaryKey(String number);

    GatewaysPo selectByName(String name);

    List<GatewaysPo> selectNumsByPage(Map<String, Object> map);

    int updateByPrimaryKeySelective(GatewaysPo record);

    List<String> getAllNameByOrgId(String orgId);
}