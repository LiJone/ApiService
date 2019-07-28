package com.tss.apiservice.service.impl;

import com.tss.apiservice.dao.TagInfosPoMapper;
import com.tss.apiservice.po.TagInfosPo;
import com.tss.apiservice.service.TagInfosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TagInfosServiceImpl implements TagInfosService {

    @Autowired
    TagInfosPoMapper tagInfosPoMapper;

    @Override
    public int insertSelective(TagInfosPo record) {
        return tagInfosPoMapper.insertSelective(record);
    }

    @Override
    public TagInfosPo selectByPrimaryKey(String tagid) {
        return tagInfosPoMapper.selectByPrimaryKey(tagid);
    }
}
