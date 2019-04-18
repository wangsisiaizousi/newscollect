package com.ef.wss.newscollect.service.impl;

import org.springframework.stereotype.Service;
import com.ef.wss.newscollect.analysiscommon.HtmlUtil;
import com.ef.wss.newscollect.service.CollectService;

@Service
public class CollectServiceImpl implements CollectService {

	@Override
	public String getHtml(String url) {
		return getHtml(url, false);
	}

	/**
	 * 通过代理提取url的HTML源码。
	 * 
	 * @param url
	 * @param useProxy
	 *            true表示需要翻墙代理。
	 * @return url的HTML字符串。
	 */
	public String getHtml(String url, boolean useProxy) {
		String html = null;
		try {
			html = HtmlUtil.getHtml(url, useProxy);
		} catch (Exception e) {
			return null;
		}

		return html;
	}

}
