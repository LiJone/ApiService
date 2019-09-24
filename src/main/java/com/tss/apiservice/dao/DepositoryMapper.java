package com.tss.apiservice.dao;

import com.tss.apiservice.po.DepositoryPO;
import com.tss.apiservice.po.ToolsPo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author 壮Jone
 */
@Repository
public interface DepositoryMapper {

    /**
     * 条件查询仓库记录
     * @param hashMap
     * @return
     */
    List<DepositoryPO> selectListByMap(Map<Object, Object> hashMap);

}
