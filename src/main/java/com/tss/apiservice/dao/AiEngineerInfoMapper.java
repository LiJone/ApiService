package com.tss.apiservice.dao;

import com.tss.apiservice.po.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface AiEngineerInfoMapper {

    List<AiEngineerInfoPO> selectListByMap(Map<String, Object> map);

    AiEngineerInfoPO selectByAiNum(String jobNum);

    OsdBindInfoPO selectByOsdNum(String osdNum);

    void insertOsdInfo(OsdBindInfoPO osdBindInfo);

    PermitBindInfoPO selectByPermitNum(String id);

    void insertPermitInfo(PermitBindInfoPO permitBindInfo);

    void insertToolInfo(ToolBindInfoPO toolInfo);

    void insertWSWPInfo(WSWPInfoPO wswpInfo);

    void insertAiEngineerInfo(AiEngineerInfoPO aiEngineer);

    void updateAiEngineerInfo(AiEngineerInfoPO aiEngineer);
}
