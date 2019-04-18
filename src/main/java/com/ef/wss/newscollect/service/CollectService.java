package com.ef.wss.newscollect.service;



public interface CollectService {
	/**
	 * 提取url的HTML源码。
	 * 
	 * @param url
	 * @return
	 */
	String getHtml(String url);
	String getHtml(String url,boolean useProxy);
	
}
