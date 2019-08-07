package com.tss.apiservice.dao;

import com.tss.apiservice.po.SafeobjsPo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SafeobjsPoMapper {
    int deleteByPrimaryKey(String objnum);

    int deleteByOsdid(String osdid);

    int insertSelective(SafeobjsPo record);

    SafeobjsPo selectByPrimaryKey(@Param("objnum") String objnum, @Param("objtype") Integer objtype);

    List<SafeobjsPo> selectByOsdid(String osdid);

    int updateByPrimaryKeySelective(SafeobjsPo record);
}