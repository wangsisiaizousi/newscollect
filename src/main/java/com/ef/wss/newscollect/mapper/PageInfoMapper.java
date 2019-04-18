package com.ef.wss.newscollect.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.ef.wss.newscollect.pojo.PageInfo;

@Mapper
public interface PageInfoMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(PageInfo record);

	int insertSelective(PageInfo record);

	PageInfo selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(PageInfo record);

	int updateByPrimaryKeyWithBLOBs(PageInfo record);

	int updateByPrimaryKey(PageInfo record);

	PageInfo selectByUrl(String url);
}