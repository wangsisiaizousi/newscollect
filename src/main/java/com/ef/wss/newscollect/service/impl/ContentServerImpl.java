package com.ef.wss.newscollect.service.impl;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ef.wss.newscollect.analysiscommon.HtmlUtil;
import com.ef.wss.newscollect.generalcommon.Extract;
import com.ef.wss.newscollect.generalcommon.WebDocument;
import com.ef.wss.newscollect.mapper.PageInfoMapper;
import com.ef.wss.newscollect.pojo.PageInfo;
import com.ef.wss.newscollect.service.ContentService;

@Component
public class ContentServerImpl implements ContentService {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private PageInfoMapper mapper;

	public void insertContent(PageInfo pageInfo) {
		try {
			mapper.insert(pageInfo);
		} catch (Exception e) {
			logger.error("插入content出错！");
		}
	}

	public void insertContents(List<PageInfo> pageInfos) {
		for (PageInfo pageInfo : pageInfos) {
			insertContent(pageInfo);
		}
	}

	@Override
	public PageInfo queryContentsByUrl(String url) {
		PageInfo info = null;
		try {
			info = mapper.selectByUrl(url);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(url);
			logger.error("查询content出错！");
			return null;
		}
		return info;
	}

	@Override
	public WebDocument getContentWebDocument(String url) {
		WebDocument document = null;
		try {
			String html = HtmlUtil.getHtml(url,true);
			document = Extract.extractByHTML(html);
		} catch (Exception e) {
			logger.error("源码采集出错！{}", e.getMessage());
			return null;
		}
		return document;
	}
}
