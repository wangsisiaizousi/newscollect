package com.ef.wss.newscollect.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ef.wss.newscollect.mapper.WebTaskMapper;
import com.ef.wss.newscollect.pojo.WebTask;
import com.ef.wss.newscollect.service.WebTaskService;

@Service
public class WebTaskServiceImpl implements WebTaskService {
	@Autowired
	private WebTaskMapper mapper;
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public WebTask buildWebTask(String taskType, int taskDeep, int taskRank, String taskUrl) {
		WebTask task = new WebTask(taskType, taskDeep, taskRank, taskUrl);
		return task;
	}

	@Override
	public List<WebTask> buildWebTasks(String taskType, int taskDeep, int taskRank, List<String> taskUrls) {
		List<WebTask> tasks = new ArrayList<>();
		WebTask task = null;
		for (String taskUrl : taskUrls) {
			task = new WebTask(taskType, taskDeep, taskRank, taskUrl);
			tasks.add(task);
		}
		return tasks;
	}

	@Override
	public void insertWebTask(WebTask webTask) {
		try {
			mapper.insert(webTask);
		} catch (Exception e) {
			logger.error("插入webtask 出错！{}",e.getMessage());
		}
	}

	@Override
	public void insertWebTasks(List<WebTask> webTasks) {
		for (WebTask webTask : webTasks) {
			mapper.insert(webTask);
		}
	}

	@Override
	public List<WebTask> queryWebTasks() {
		List<WebTask> tasks = null;
		try {
			tasks = mapper.selectAll();
		} catch (Exception e) {
			logger.error("查询webtask出错！{}",e.getMessage());
			return null;
		}
		return tasks;
	}

	@Override
	public WebTask queryWebTaskByUrl(String url) {
		WebTask task = null;
		try {
			task = mapper.selectByUrl(url);
		} catch (Exception e) {
			logger.error("查詢WebTask出錯！{}"+e.getMessage());
			return null;
		}
		return task;
	}

}
