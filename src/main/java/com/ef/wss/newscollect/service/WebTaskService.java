package com.ef.wss.newscollect.service;

import java.util.List;

import com.ef.wss.newscollect.pojo.WebTask;

/**
 * 种子或者内容服务类
 * 
 * @author EEFUNG
 *
 */
public interface WebTaskService {

	public WebTask buildWebTask(String taskType, int taskDeep, int taskRank, String taskUrl);

	public List<WebTask> buildWebTasks(String taskType, int taskDeep, int taskRank, List<String> taskUrls);

	void insertWebTask(WebTask webTask);

	void insertWebTasks(List<WebTask> webTasks);

	List<WebTask> queryWebTasks();

	WebTask queryWebTaskByUrl(String url);

}
