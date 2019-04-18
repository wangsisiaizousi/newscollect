package com.ef.wss.newscollect.service;



import java.util.Map;



public interface AnalysisService {
	/**
	 * 通过url 分析一个网站的链接及其相应的种类
	 * @param url
	 * @return
	 */
	Map<String, String> analysisLinksAndType(String url);
}
