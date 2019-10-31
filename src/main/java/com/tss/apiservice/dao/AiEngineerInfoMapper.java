package com.tss.apiservice.dao;

import com.tss.apiservice.form.*;
import com.tss.apiservice.po.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author 壮Jone
 */
@Repository
public interface AiEngineerInfoMapper {

    /**
     * 根据条件获取工程列表
     * @param map 条件
     * @return 工程集合
     */
    List<AiEngineerInfoForm> selectListByMap(Map<String, Object> map);

    /**
     * 根据工程编号删除工程
     * @param jobNum 工程编号
     */
    void deleteByAiNum(String jobNum);

    /**
     * 根据工程编号获取工程信息
     * @param jobNum 工程编号
     * @return 工程
     */
    AiEngineerInfoPO selectByAiNum(String jobNum);

    /**
     * 根据盒子编号获取盒子信息
     * @param osdNum 盒子编号
     * @return 盒子
     */
    OsdBindInfoPO selectByOsdNum(String osdNum);

    /**
     * 新增盒子
     * @param osdBindInfo 盒子
     */
    void insertOsdInfo(OsdBindInfoPO osdBindInfo);

    /**
     * 根据许可证编号获取许可证信息
     * @param permitNum 许可证编号
     * @return 许可证
     */
    PermitBindInfoPO selectByPermitNum(String permitNum);

    /**
     * 新增许可证
     * @param permitBindInfo 许可证
     */
    void insertPermitInfo(PermitBindInfoPO permitBindInfo);

    /**
     * 根据功能编号获取功能信息
     * @param funcNum 功能编号
     * @return 功能
     */
    FuncBindInfoPO selectByFuncNum(String funcNum);

    /**
     * 新增功能
     * @param funcInfo 功能
     */
    void insertFuncInfo(FuncBindInfoPO funcInfo);

    /**
     * 新增工具
     * @param toolInfo 工具
     */
    void insertToolInfo(ToolBindInfoPO toolInfo);

    /**
     * 根据wswp编号获取wswp信息
     * @param cpNum cp编号
     * @return wswp
     */
    WSWPInfoPO selectByWswpNum(String cpNum);

    /**
     * 新增wswp
     * @param wswpInfo wswp
     */
    void insertWswpInfo(WSWPInfoPO wswpInfo);

    /**
     * 新增工程
     * @param aiEngineer 工程
     */
    void insertAiEngineerInfo(AiEngineerInfoPO aiEngineer);

    /**
     * 修改工程
     * @param aiEngineer 工程
     */
    void updateAiEngineerInfo(AiEngineerInfoPO aiEngineer);

    /**
     * 根据工程编号删除盒子
     * @param jobNum 工程编号
     */
    void deleteOsdByAiNum(String jobNum);

    /**
     * 根据工程编号删除许可证
     * @param jobNum 工程编号
     */
    void deletePermitByAiNum(String jobNum);

    /**
     * 根据工程编号删除wswp
     * @param jobNum 工程编号
     */
    void deleteWswpByAiNum(String jobNum);

    /**
     * 根据工程编号删除功能
     * @param jobNum 工程编号
     */
    void deleteFuncByAiNum(String jobNum);

    /**
     * 根据工程编号获取所有功能
     * @param jobNum 工程编号
     * @return 功能集合
     */
    List<FuncBindInfoPO> selectFuncByAiNum(String jobNum);

    /**
     * 根据功能编号删除盒子
     * @param funcNum 功能编号
     */
    void deleteOsdByFuncNum(String funcNum);

    /**
     * 根据功能编号删除许可证
     * @param funcNum 功能编号
     */
    void deletePermitByFuncNum(String funcNum);

    /**
     * 根据功能编号删除工具
     * @param funcNum 功能编号
     */
    void deleteToolByFuncNum(String funcNum);

    /**
     * 根据工程编号获取所有功能
     * @param jobNum 工程编号
     * @return 功能集合
     */
    List<FuncBindInfoForm> selectFuncFormByAiNum(String jobNum);

    /**
     * 根据工程编号获取所有功能
     * @param jobNum 许可证编号
     * @return 许可证集合
     */
    List<PermitBindInfoForm> selectPermitFormByAiNum(String jobNum);

    /**
     * 根据工程编号获取所有盒子
     * @param jobNum 工程编号
     * @return 盒子集合
     */
    List<OsdBindInfoForm> selectOsdFormByAiNum(String jobNum);

    /**
     * 根据工程编号获取所有wswp
     * @param jobNum 工程编号
     * @return wswp集合
     */
    List<WSWPInfoForm> selectWswpFormByAiNum(String jobNum);

    /**
     * 根据功能编号获取所有盒子
     * @param funcNum 功能编号
     * @return 盒子集合
     */
    List<OsdBindInfoForm> selectOsdFormByFuncNum(String funcNum);

    /**
     * 根据功能编号获取所有许可证
     * @param funcNum 功能编号
     * @return 许可证集合
     */
    List<PermitBindInfoForm> selectPermitFormByFuncNum(String funcNum);

    /**
     * 根据功能编号获取所有工具
     * @param funcNum 功能编号
     * @return 工具集合
     */
    List<ToolBindInfoForm> selectToolFormByFuncNum(String funcNum);

    /**
     * 根据cp编号查询是否符合条件
     * @param cpNum cp编号
     * @return
     */
    Integer getCpCountByCpNum(String cpNum);
}
