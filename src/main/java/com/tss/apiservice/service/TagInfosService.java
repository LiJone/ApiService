package com.tss.apiservice.service;

import com.tss.apiservice.po.TagInfosPo;

public interface TagInfosService {
    int insertSelective(TagInfosPo record);

    TagInfosPo selectByPrimaryKey(String tagid);
}
