package com.ef.wss.newscollect.service;

import java.util.List;




public interface SchedulerService {
	public void schedulerSeedTask();
	public void schedulercontentTask();
	public List<String> getLinks();

}
