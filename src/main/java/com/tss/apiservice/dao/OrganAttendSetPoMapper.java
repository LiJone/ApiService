package com.tss.apiservice.dao;

import com.tss.apiservice.po.AttendOTSetPo;
import com.tss.apiservice.po.AttendSetPo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 壮Jone
 */
@Repository
public interface OrganAttendSetPoMapper {

    /**
     * 获取改机构考勤设置
     * @param orgid
     * @return
     */
    AttendSetPo selectAttendSet(String orgid);

    /**
     * 获取改机构考勤加班设置
     * @param orgid
     * @return
     */
    List<AttendOTSetPo> selectAttendOTSet(String orgid);

    /**
     * 检测是否存在改机构的考勤设置
     * @param orgid
     * @return
     */
    Integer checkIsExist(String orgid);

    /**
     * 删除旧的考勤加班设置
     * @param orgid
     */
    void deleteOldAttendOTSet(String orgid);

    /**
     * 添加新的考勤加班设置
     * @param attendOTSetPo
     */
    void insertNewAttendOTSet(AttendOTSetPo attendOTSetPo);

    /**
     * 添加考勤设置
     * @param attendSetPo
     */
    void insertSelective(AttendSetPo attendSetPo);

    /**
     * 修改考勤设置
     * @param attendSetPo
     */
    void updateByPrimaryKeySelective(AttendSetPo attendSetPo);
}
