package com.ef.wss.newscollect.taskQueue;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskExecutor extends Thread {
	private BlockingQueue<Task> taskQueue;
	private Logger logger = LoggerFactory.getLogger(TaskExecutor.class);
	public TaskExecutor(BlockingQueue<Task>blockingQueue) {
		this.taskQueue = blockingQueue;
	}
	@Override
	public void run() {
		Task task; 
		while(!taskQueue.isEmpty()) {
			try {
				task = taskQueue.take();
				task.run();
			} catch (InterruptedException e) {
				logger.error("从队列里面取任务失败{}",e.getMessage());
			}
		}
	}

}
