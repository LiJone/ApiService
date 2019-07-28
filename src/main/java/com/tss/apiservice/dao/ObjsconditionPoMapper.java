package com.tss.apiservice.dao;

import com.tss.apiservice.po.ObjsconditionPo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObjsconditionPoMapper {
    int deleteByPrimaryKey(Integer id);

    int deleteByOsdid(String osdid);

    int deleteByStaffid(String staffid);

    int insertSelective(ObjsconditionPo record);

    ObjsconditionPo selectByPrimaryKey(Integer id);

    List<ObjsconditionPo> selectByStaffid(String staffid);

    List<ObjsconditionPo> selectByOsdid(String osdid);

    int updateByPrimaryKeySelective(ObjsconditionPo record);
}