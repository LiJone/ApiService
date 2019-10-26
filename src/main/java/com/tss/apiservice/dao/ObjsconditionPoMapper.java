package com.tss.apiservice.dao;

import com.tss.apiservice.po.ObjsconditionPo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObjsconditionPoMapper {
    int deleteByOsdid(String osdid);

    int deleteByStaffid(String staffid);

    int insertSelective(ObjsconditionPo record);

    List<ObjsconditionPo> selectByStaffid(String staffid);

    List<ObjsconditionPo> selectByOsdid(String osdid);
}