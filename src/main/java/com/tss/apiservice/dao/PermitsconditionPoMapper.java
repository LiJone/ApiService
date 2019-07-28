package com.tss.apiservice.dao;

import com.tss.apiservice.po.PermitsconditionPo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermitsconditionPoMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(PermitsconditionPo record);

    PermitsconditionPo selectByPrimaryKey(Integer id);

    List<PermitsconditionPo> selectByPermitname(String permitname);

    int updateByPrimaryKeySelective(PermitsconditionPo record);

}