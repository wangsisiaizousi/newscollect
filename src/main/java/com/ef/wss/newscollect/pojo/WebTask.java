package com.ef.wss.newscollect.pojo;

public class WebTask {
    private Integer id;

    private String taskUrl;

    private String taskType;

    private Integer taskDeep;

    private Integer taskRank;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTaskUrl() {
        return taskUrl;
    }

    public void setTaskUrl(String taskUrl) {
        this.taskUrl = taskUrl == null ? null : taskUrl.trim();
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType == null ? null : taskType.trim();
    }

    public Integer getTaskDeep() {
        return taskDeep;
    }

    public void setTaskDeep(Integer taskDeep) {
        this.taskDeep = taskDeep;
    }

    public Integer getTaskRank() {
        return taskRank;
    }

    public void setTaskRank(Integer taskRank) {
        this.taskRank = taskRank;
    }
    public WebTask(String taskType, int taskDeep, int taskRank, String taskUrl) {
    	this.taskUrl = taskUrl;
    	this.taskDeep = taskDeep;
    	this.taskRank = taskRank;
    	this.taskType = taskType;
    }

	public WebTask(Integer id, String taskUrl, String taskType, Integer taskDeep, Integer taskRank) {
		super();
		this.id = id;
		this.taskUrl = taskUrl;
		this.taskType = taskType;
		this.taskDeep = taskDeep;
		this.taskRank = taskRank;
	}
    
}