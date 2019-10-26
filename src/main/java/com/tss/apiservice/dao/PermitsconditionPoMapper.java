package com.tss.apiservice.dao;

import com.tss.apiservice.po.PermitsconditionPo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermitsconditionPoMapper {
    PermitsconditionPo selectByPrimaryKey(Integer id);

    List<PermitsconditionPo> selectByPermitTypeId(Integer permittypeid);
}