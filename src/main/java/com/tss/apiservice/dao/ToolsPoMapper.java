package com.tss.apiservice.dao;


import com.tss.apiservice.po.ToolTypePO;
import com.tss.apiservice.po.ToolsPo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ToolsPoMapper {
    int deleteByPrimaryKey(String toolid);

    int insertSelective(ToolsPo record);

    ToolsPo selectByPrimaryKey(String toolid);

    List<ToolsPo> selectListByMap(Map HashMap);

    int updateByPrimaryKeySelective(ToolsPo record);

    List<ToolTypePO> getToolType();
}