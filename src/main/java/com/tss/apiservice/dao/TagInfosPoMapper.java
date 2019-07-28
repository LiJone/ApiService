package com.tss.apiservice.dao;

import com.tss.apiservice.po.TagInfosPo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagInfosPoMapper {
    int deleteByPrimaryKey(String tagid);

    int deleteByTagPo(TagInfosPo tagInfosPo);

    int insertSelective(TagInfosPo record);

    TagInfosPo selectByPrimaryKey(String tagid);

    TagInfosPo selectByObjnum(String objnum);

    List<TagInfosPo> selectByTagPo(TagInfosPo tagInfosPo);

    int updateByPrimaryKeySelective(TagInfosPo record);
}