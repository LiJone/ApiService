package com.tss.apiservice.dao;

import com.tss.apiservice.po.ToolscertPo;
import org.springframework.stereotype.Repository;

@Repository
public interface ToolscertPoMapper {
    int deleteByPrimaryKey(String certid);

    int deleteByToolid(String toolid);

    int insertSelective(ToolscertPo record);

    ToolscertPo selectByPrimaryKey(String certid);

    ToolscertPo selectByToolid(String toolid);

    int updateByPrimaryKeySelective(ToolscertPo record);
}