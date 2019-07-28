package com.tss.apiservice.dao;

import com.tss.apiservice.po.AttendOTRecordPo;
import com.tss.apiservice.po.AttendancePo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface AttendancePoMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(AttendancePo record);

    AttendancePo selectByPrimaryKey(Integer id);

    /**
     * 获取该员工所有的上班数据
     * @param map
     * @return
     */
    List<AttendancePo> selectListByMap(Map map);

    /**
     * 获取该员工所有的上午上班的记录
     * @param map
     * @return
     */
    Integer selectCountAmontimeIsNotNull(Map map);

    /**
     * 获取该员工所有的下午上班的记录
     * @param map
     * @return
     */
    Integer selectCountPmontimeIsNotNull(Map map);

    int updateByPrimaryKeySelective(AttendancePo record);

    /**
     * 获取该样员工所以加班时长
     * @param map
     * @return
     */
    Float selectAllAddHourOttimeIsNotNull(Map map);

    /**
     * 获取当前员工日新
     * @param map
     * @return
     */
    Integer selectDaySalary(Map map);

    /**
     * 获取当前考勤该员工加班信息
     * @param id
     * @return
     */
    List<AttendOTRecordPo> selectWorkAddInfo(Integer id);

    /**
     * 获取一条考勤的加班时间
     * @param id
     * @return
     */
    Float selectAddHour(Integer id);
}