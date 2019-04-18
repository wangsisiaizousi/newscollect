package com.ef.wss.newscollect.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import com.ef.wss.newscollect.analysiscommon.GsonUtils;
import com.ef.wss.newscollect.pojo.WebTask;
import com.ef.wss.newscollect.service.SchedulerService;
import com.ef.wss.newscollect.service.WebTaskService;

@Service
@EnableAsync
public class SchedulerServiceImpl implements SchedulerService {

	private Logger logger = LoggerFactory.getLogger(getClass());
	private List<String> links = new ArrayList<>();
	@Autowired
	private WebTaskService webTaskService;
	@Autowired
	private AsyncScheduler asyncScheduler;

	@Override
	public void schedulerSeedTask() {
		// 得到任务
		List<WebTask> tasks = webTaskService.queryWebTasks();
		if (tasks == null) {
			return;
		}
		asyncScheduler.perFormSeedTask(tasks);
	}

	@PostConstruct
	private void readJsonData() throws IOException {
		ClassPathResource resource = new ClassPathResource("webSite.json");
		InputStream inputStream = resource.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8")); // 实例化输入流，并获取网页代码
		String s; // 依次循环，至到读的值为空
		StringBuilder sb = new StringBuilder();
		while ((s = reader.readLine()) != null) {
			sb.append(s);
		}
		reader.close();
		String jsonString = sb.toString();
		List<Map<String, String>> jsonLinks = GsonUtils.getGson().fromJson(jsonString, List.class);
		for (Map<String, String> map : jsonLinks) {
			links.add(map.get("url"));
		}
	}

	/**
	 * 返回网站首页链接集合如果集合为空则返回null
	 */
	@Override
	public List<String> getLinks() {
		if (links.size() > 0) {
			return links;
		}
		return null;
	}

	@Override
	public void schedulercontentTask() {
		List<WebTask> tasks = webTaskService.queryWebTasks();
		List<WebTask> contentTasks = new ArrayList<>();
		if (tasks != null) {
			for (WebTask webTask : tasks) {
				if (webTask.getTaskType().equals("2")) {
					contentTasks.add(webTask);
				}
			}
		}
		logger.info("内容任务持久化开始");
		asyncScheduler.persistenceContent(contentTasks);
	}

}
