package com.tss.apiservice.dao;

import com.tss.apiservice.po.*;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface StaffsPoMapper {
    int deleteByPrimaryKey(String staffid);

    int insertSelective(StaffsPo record);

    //员工列表分页数据查找
    List<StaffsPo> selectListByMap(HashMap<String, Object> map);

    //根据条件查找唯一性，报表中使用
    List<StaffsPo> selectListByMap02(HashMap<Object, Object> map);

    //List<StaffsPo> selectListByUserid(Integer userid);
    List<StaffsPo> selectListByOrgid(String orgid);

    StaffsPo selectByPrimaryKey(String staffid);

    int updateByPrimaryKeySelective(StaffsPo record);

    List<CertTypePO> getCerType();

    List<String> selectCpListByPositionTypeId(String positionTypeId);

    List<String> getAllNumByOrgId(String orgid);

    void insertStaffsImage(StaffsImagePO staffsImage);

    List<StaffsImagePO> selectStaffsImageByStaffId(String staffId);

    void deleteStaffsImageByStaffId(HashMap<String, Object> map);

    void updateStaffsImage(StaffsImagePO staffsImage);
}