package com.tss.apiservice.service.impl;

import com.tss.apiservice.dao.ToolscertPoMapper;
import com.tss.apiservice.po.ToolscertPo;
import com.tss.apiservice.service.ToolscertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ToolscertServiceImpl implements ToolscertService {

    @Autowired
    ToolscertPoMapper toolscertPoMapper;

    @Override
    public int insertSelective(ToolscertPo record) {
        return toolscertPoMapper.insertSelective(record);
    }
}
