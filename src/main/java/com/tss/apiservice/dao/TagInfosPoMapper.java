package com.tss.apiservice.dao;

import com.tss.apiservice.po.TagInfosPo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface TagInfosPoMapper {
    int deleteByPrimaryKey(String tagid);

    int deleteByTagPo(TagInfosPo tagInfosPo);

    int insertSelective(TagInfosPo record);

    TagInfosPo selectByPrimaryKey(String tagid);

    TagInfosPo selectByObjnum(Map<String, Object> map);

    List<TagInfosPo> selectByTagPo(TagInfosPo tagInfosPo);

    int updateByPrimaryKeySelective(TagInfosPo record);
}