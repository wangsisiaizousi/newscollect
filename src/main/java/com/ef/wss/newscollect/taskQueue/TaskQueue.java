package com.ef.wss.newscollect.taskQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TaskQueue  {
	private BlockingQueue<Task> taskQueue ;
	private TaskExecutor[] executors;
	public TaskQueue(int size) {
		executors  = new TaskExecutor[size];
		taskQueue = new LinkedBlockingQueue<>();
	}
	
	public int addTask(Task task) {
		if(!taskQueue.contains(task)) {
			taskQueue.add(task);
		}
		return taskQueue.size();
	}
	
	void start() {
		for (int i = 0; i < executors.length; i++) {
			executors[i] = new TaskExecutor(taskQueue);
			executors[i].start();
        }	
	}
}
