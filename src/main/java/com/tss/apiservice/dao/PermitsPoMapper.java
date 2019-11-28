package com.tss.apiservice.dao;

import com.tss.apiservice.po.*;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface PermitsPoMapper {
    int deleteByPrimaryKey(String permitid);

    int insertSelective(PermitsPo record);

    PermitsPo selectByPrimaryKey(String permitid);

    PermitsPo selectByName(String name);

    List<PermitsPo> selectListByMap(HashMap<String, Object> map);

    int updateByPrimaryKeySelective(PermitsPo record);

    List<PermitTypePO> getPermitType();

    List<String> getAllNumByOrgId(String orgid);

    void insertPermitsImage(PermitsImagePO permitsImage);

    List<PermitsImagePO> selectPermitsImageByPermitId(String permitId);

    void deletePermitsImageByPermitId(HashMap<String, Object> map);

    void updatePermitsImage(PermitsImagePO permitsImage);

    List<String> getAllNameByOrgId(String orgId);
}