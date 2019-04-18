package com.ef.wss.newscollect.rest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ef.wss.newscollect.analysiscommon.GsonUtils;
import com.ef.wss.newscollect.generalcommon.Extract;
import com.ef.wss.newscollect.generalcommon.WebDocument;
import com.ef.wss.newscollect.pojo.WebTask;
import com.ef.wss.newscollect.service.WebTaskService;
import com.ef.wss.newscollect.service.impl.AsyncScheduler;

@RestController
public class RestService {
	@Autowired
	private WebTaskService webTaskService;
	@Autowired
	private AsyncScheduler asyncScheduler;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@RequestMapping(value = "/getWebDocument", method = RequestMethod.POST, produces = "application/json")
	public String getWebDocument(@RequestParam(value = "html", required = true) String html) {
		WebDocument document = null;
		document = Extract.extractByHTML(html);
		return GsonUtils.getGson().toJson(document);
	}

	@RequestMapping(value = "/addTask", method = RequestMethod.GET)
	public String addWebTask(@RequestParam(value = "link", required = true) String link) {
		WebTask webTask = webTaskService.buildWebTask("1", 1, 1, link);
		if (webTaskService.queryWebTaskByUrl(webTask.getTaskUrl()) == null) {
			webTaskService.insertWebTask(webTask);
			asyncScheduler.seedTaskAndContentTask(webTask);
			logger.info("添加链接已插入且构建成任务并执行");
			return "success";
		} else {
			logger.info("首页链接已存在或者插入链接出错");
			return "error";
		}
	}

	public static int myAtoi(String str) {
		str = str.trim();
		if (str == null || str.equals("")) {
			return 0;
		}
		long res = 0;
		boolean isfushu = false;
		if (str.charAt(0) == '-') {
			isfushu = true;
			str = str.substring(1, str.length());
		} else if (str.charAt(0) == '+') {
			str = str.substring(1, str.length());
		}
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c <= '9' && c >= '0') {
				res = res * 10 + c - '0';
				if (!isfushu && res > Integer.MAX_VALUE) {
					return Integer.MAX_VALUE;
				}
				if (isfushu && -1 * res < Integer.MIN_VALUE) {
					return Integer.MIN_VALUE;
				}
			} else {
				break;
			}
		}
		return isfushu ? -1 * (int) res : (int) res;
	}
}
