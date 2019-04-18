package com.ef.wss.newscollect.pojo;

public class PageInfo {
	private Integer id;

	private String url;

	private String title;

	private String timeS;

	private String commitTime;

	private String article;

	private String source;

	public PageInfo() {

	}

	public PageInfo(Integer id, String url, String title, String timeS, String commitTime, String article,
			String source) {
		super();
		this.id = id;
		this.url = url;
		this.title = title;
		this.timeS = timeS;
		this.commitTime = commitTime;
		this.article = article;
		this.source = source;
	}

	public PageInfo(String url, String title, String time, String commitTime, String article, String source) {
		this.url = url;
		this.title = title;
		this.article = article;
		this.commitTime = commitTime;
		this.source = source;
		this.timeS = time;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url == null ? null : url.trim();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title == null ? null : title.trim();
	}

	public String getTimeS() {
		return timeS;
	}

	public void setTimeS(String timeS) {
		this.timeS = timeS == null ? null : timeS.trim();
	}

	public String getCommitTime() {
		return commitTime;
	}

	public void setCommitTime(String commitTime) {
		this.commitTime = commitTime == null ? null : commitTime.trim();
	}

	public String getArticle() {
		return article;
	}

	public void setArticle(String article) {
		this.article = article == null ? null : article.trim();
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source == null ? null : source.trim();
	}
}