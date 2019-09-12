package com.tss.apiservice.dao;

import com.tss.apiservice.po.CertTypePO;
import com.tss.apiservice.po.StaffsPo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface StaffsPoMapper {
    int deleteByPrimaryKey(String staffid);


    int insertSelective(StaffsPo record);

    //员工列表分页数据查找
    List<StaffsPo> selectListByMap(Map HashMap);

    //根据条件查找唯一性，报表中使用
    List<StaffsPo> selectListByMap02(Map HashMap);

    //List<StaffsPo> selectListByUserid(Integer userid);
    List<StaffsPo> selectListByOrgid(String orgid);

    StaffsPo selectByPrimaryKey(String staffid);

    int updateByPrimaryKeySelective(StaffsPo record);

    List<CertTypePO> getCerType();
}