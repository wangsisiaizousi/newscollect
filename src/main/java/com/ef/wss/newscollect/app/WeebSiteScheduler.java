package com.ef.wss.newscollect.app;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import com.ef.wss.newscollect.analysiscommon.GsonUtils;
import com.ef.wss.newscollect.pojo.WebTask;
import com.ef.wss.newscollect.service.SchedulerService;
import com.ef.wss.newscollect.service.WebTaskService;
  
@Component
@EnableAsync
public class WeebSiteScheduler {
	private final int MINUTE = 1000 * 60;
	@Autowired
	private SchedulerService schedulerService;
	@Autowired
	private WebTaskService webTaskService;
	protected final Logger logger = LoggerFactory.getLogger(getClass());
    @Value("${tasks.chedule.period:10}")
    private int tasksChedulePeriod;
	@PostConstruct
	private void init() {
		initWebSite();
		Timer timer = new Timer();
		TimerTask scheduleTask = new TimerTask() {
			@Override
			public void run() {
				schedulerService.schedulerSeedTask();
			}
		};

		TimerTask contentTask = new TimerTask() {
			@Override
			public void run() {
				schedulerService.schedulercontentTask();
			}
		};
		logger.info("开启调度种子链接定时器！");
		timer.schedule(scheduleTask, 0, MINUTE*tasksChedulePeriod);
		logger.info("开启内容任务定时采集");
		timer.schedule(contentTask, MINUTE, MINUTE*tasksChedulePeriod);

	}

	/**
	 * 把网站链接进行初始化分析，把分析对象入库
	 */
	public void initWebSite() {
		List<String> links = schedulerService.getLinks();
		logger.info("首页构建成任务并入库");
		List<WebTask> webTasks = webTaskService.buildWebTasks("1", 1, 1, links);
		logger.info("初始化首页任务入库：{}", GsonUtils.getGson().toJson(webTasks));
		webTaskService.insertWebTasks(webTasks);
	}

}
