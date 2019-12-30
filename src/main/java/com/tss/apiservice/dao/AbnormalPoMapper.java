package com.tss.apiservice.dao;

import com.tss.apiservice.po.AbnormalPo;
import com.tss.apiservice.po.vo.AbnormalExceptionVo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface AbnormalPoMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(AbnormalPo record);

    AbnormalPo selectByPrimaryKey(Integer id);

    List<AbnormalExceptionVo> selectByHashMap(Map map);

    int updateByPrimaryKeySelective(AbnormalPo record);

    Integer selectByNumberAndType(Map<String, Object> map);

    void deleteByNumberAndType(Map<String, Object> hashMap);
}