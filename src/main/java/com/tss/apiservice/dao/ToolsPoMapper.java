package com.tss.apiservice.dao;


import com.tss.apiservice.po.ToolTypePO;
import com.tss.apiservice.po.ToolsImagePO;
import com.tss.apiservice.po.ToolsPo;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface ToolsPoMapper {
    int deleteByPrimaryKey(String toolid);

    int insertSelective(ToolsPo record);

    ToolsPo selectByPrimaryKey(String toolid);

    List<ToolsPo> selectListByMap(HashMap<String, Object> map);

    int updateByPrimaryKeySelective(ToolsPo record);

    List<ToolTypePO> getToolType();

    List<String> getAllNumByOrgId(String orgid);

    void insertToolsImage(ToolsImagePO toolsImage);

    List<ToolsImagePO> selectToolsImageByToolId(String toolId);

    void deleteToolsImageByToolId(HashMap<String, Object> map);

    void updateToolsImage(ToolsImagePO toolsImage);

    List<String> getAllNameByOrgId(String orgId);
}