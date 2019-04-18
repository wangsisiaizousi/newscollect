package com.ef.wss.newscollect.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import com.ef.wss.newscollect.pojo.WebTask;

@Mapper
public interface WebTaskMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(WebTask record);

	int insertSelective(WebTask record);

	WebTask selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(WebTask record);

	int updateByPrimaryKey(WebTask record);

	List<WebTask> selectAll();

	WebTask selectByUrl(String url);
}