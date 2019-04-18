package com.ef.wss.newscollect.mapper;


import org.apache.ibatis.annotations.Mapper;

import com.ef.wss.newscollect.pojo.LinkType;


@Mapper
public interface LinkTypeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(LinkType record);

    int insertSelective(LinkType record);

    LinkType selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(LinkType record);

    int updateByPrimaryKey(LinkType record);
    
    LinkType seleLinkTypeByUrl(String url);
    
}