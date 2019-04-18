package com.ef.wss.newscollect.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import com.ef.wss.newscollect.analysiscommon.GsonUtils;
import com.ef.wss.newscollect.pojo.LinkType;
import com.ef.wss.newscollect.pojo.PageInfo;
import com.ef.wss.newscollect.pojo.WebTask;
import com.ef.wss.newscollect.generalcommon.WebDocument;
import com.ef.wss.newscollect.service.AnalysisService;
import com.ef.wss.newscollect.service.ContentService;
import com.ef.wss.newscollect.service.WebTaskService;

@Component
@EnableAsync
public class AsyncScheduler {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private AnalysisService analysisService;
	@Autowired
	private WebTaskService webTaskService;
	@Autowired
	private LinkTypeImpl linkTypeImpl;
	@Autowired
	private ContentService contentService;

	@Async
	public void perFormSeedTask(List<WebTask> tasks) {
		int linkTypeCount = 0;
		int webTaskCount = 0;
		for (WebTask webTask : tasks) {
			if (webTask.getTaskType().equals("1")) {
				Map<String, String> linkTypeMap = analysisService.analysisLinksAndType(webTask.getTaskUrl());
				if (linkTypeMap == null) {
					logger.info("分析任务失败！" + GsonUtils.getGson().toJson(webTask));
					continue;
				}
				for (String key : linkTypeMap.keySet()) {
					LinkType linkType = new LinkType(key, linkTypeMap.get(key));
					if (linkType.getTypeS().equals("3") || linkType.getTypeS().equals("4")
							|| linkTypeImpl.queryLinkTypeByUrl(linkType.getUrl()) != null) {
						continue;
					}
					linkTypeCount++;
					linkTypeImpl.insertLinkType(linkType);
					WebTask buildTask = webTaskService.buildWebTask(linkType.getTypeS(), webTask.getTaskDeep() - 1,
							webTask.getTaskRank(), key);
					if ((buildTask.getTaskDeep() > 0 || buildTask.getTaskType().equals("2"))
							&& webTaskService.queryWebTaskByUrl(buildTask.getTaskUrl()) == null) {
						webTaskService.insertWebTask(buildTask);
						webTaskCount++;
					}

				}
			}
		}
		logger.info("把有效链接构建成的任务入库量为：{}", webTaskCount);
		logger.info("有链接务持久化结果数量：{}", linkTypeCount);
	}


	@Async
	public void persistenceContent(List<WebTask> webTasks) {
		int webTasksCount = 0;
		for (WebTask webTask : webTasks) {
			if (contentService.queryContentsByUrl(webTask.getTaskUrl()) != null) {
				continue;
			}
			WebDocument document = contentService.getContentWebDocument(webTask.getTaskUrl());
			if (document == null) {
				continue;
			}
			String url = webTask.getTaskUrl();
			String title = document.getTitle() == null ? "" : document.getTitle();
			String content = document.getContent() == null ? "" : document.getContent();
			String time = document.getTime() == null ? "" : document.getTime();
			String source = document.getSource() == null ? "" : document.getSource();
			PageInfo pageInfo = new PageInfo(url, title, time, String.valueOf(new Date().getTime()), content, source);
			contentService.insertContent(pageInfo);
			webTasksCount++;
		}
		logger.info("内容任务持久化结果为{}条", webTasksCount);
	}
	@Async
	public void seedTaskAndContentTask(WebTask seedTask) {
		ArrayList<WebTask> seedTasks = new ArrayList<WebTask>();
		seedTasks.add(seedTask);
		perFormSeedTask(seedTasks);
		List<WebTask> tasks = webTaskService.queryWebTasks();
		List<WebTask> contentTasks = new ArrayList<>();
		if (tasks != null) {
			for (WebTask webTask : tasks) {
				if (webTask.getTaskType().equals("2") && webTask.getTaskUrl().contains(seedTask.getTaskUrl())) {
					contentTasks.add(webTask);
				}
			}
		}
		persistenceContent(contentTasks);
	}
}
