package com.ef.wss.newscollect.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import com.ef.wss.newscollect.analysiscommon.AnalysisUtil;
import com.ef.wss.newscollect.analysiscommon.GsonUtils;
import com.ef.wss.newscollect.analysiscommon.HtmlUtil;
import com.ef.wss.newscollect.entity.AnalysisInfo;
import com.ef.wss.newscollect.entity.LinkType;
import com.ef.wss.newscollect.service.AnalysisService;
import com.ef.wss.newscollect.service.CollectService;

@Service
public class AnalysisServiceImpl implements AnalysisService {
	@Autowired
	private AnalysisUtil analysisUtil;
	@Autowired
	private CollectService collectService;

	@Override
	public Map<String, String> analysisLinksAndType(String url) {
		String html = collectService.getHtml(url);
		if(html==null||html.trim().equals("")) {
			return null;
		}
		Document doc = Jsoup.parse(html);
		Map<String, String> linkTypeMap = analysisUtil.getLinkType(url, doc, HtmlUtil.getHost(url));
		return linkTypeMap;
	}
	
	

}
