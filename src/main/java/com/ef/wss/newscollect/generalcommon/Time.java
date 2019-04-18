package com.ef.wss.newscollect.generalcommon;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 
 * <p>
 * 网页提取时间的算法。
 * </p>
 *
 * 创建日期 2015年7月31日
 * 
 * @author zhaoliang(zhaoliang@eefung.com)
 * @since $version$
 */
public class Time {

	private static final Log LOGGER = LogFactory.getLog(Time.class);

	// 用于存储匹配时间表达式的正则表达式。
	private static Map<String, String> regex_map = null;

	private static List<Pattern> patterns = new ArrayList<Pattern>();

	// 用于临时时间字符串
	private Map<String, Integer> res = null;

	private String resultTime = null;

	public String getResultTime() {
		return resultTime;
	}

	public Time(String htmlString) {
		resultTime = extractTimeForHtml(htmlString);
	}

	public Time(byte[] htmlByteArray) {
		resultTime = extractTimeForHtmlFromByte(htmlByteArray);
	}

	static {
		regex_map = loadXMLFile(Time.class.getClassLoader().getResourceAsStream("template.xml"));

		for (Map.Entry<String, String> entry : regex_map.entrySet()) {
			Pattern pattern = Pattern.compile(entry.getValue());
			patterns.add(pattern);
		}
	}

	/**
	 * 提出页面的正文时间。
	 * 
	 * @param html
	 *            需要处理的html页面。
	 * @return
	 */
	public String extractTimeForHtml(String html) {
		Document doc = Jsoup.parse(html);
		findTime(doc);

		String real = null;
		int biggest = 0;
		if (res != null && res.size() > 0) {
			for (String s : res.keySet()) {
				int priority = res.get(s);
				if (priority > biggest) {
					biggest = priority;
					real = s;
				}
			}
			return real;
		} else {
			return null;
		}
	}

	/**
	 * 提出页面的正文时间。
	 * 
	 * @param html
	 *            需要处理的html页面。
	 * @return
	 */
	public String extractTimeForHtmlFromByte(byte[] html) {
		String charset = CharsetUtils.parseCharSet(html);
		InputStream is = new ByteArrayInputStream(html);

		Document doc = null;
		try {
			doc = Jsoup.parse(is, charset != null ? charset : "UTF-8", "");
		} catch (IOException e) {
			e.printStackTrace();
		}
		findTime(doc);

		String real = null;
		int biggest = 0;
		if (res != null && res.size() > 0) {
			for (String s : res.keySet()) {
				int priority = res.get(s);
				if (priority > biggest) {
					biggest = priority;
					real = s;
				}
			}
			return real;
		} else {
			return null;
		}
	}

	/**
	 * 加载匹配时间的正则表达式至内存中。
	 * 
	 * @param xmlImputStream
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, String> loadXMLFile(InputStream xmlImputStream) {
		Map<String, String> regexList = new HashMap<String, String>();
		SAXReader reader = new SAXReader();

		org.dom4j.Document document = null;

		try {
			document = reader.read(xmlImputStream);
		} catch (DocumentException e) {
			LOGGER.error("读取xml文件出现问题", e);
		} finally {
			try {
				xmlImputStream.close();
			} catch (IOException e1) {
				e1.printStackTrace();
				LOGGER.error("读取xml文件的输入流关闭流失败。", e1);
			}
		}

		org.dom4j.Element root = document.getRootElement();
		org.dom4j.Element timeregex = root.element("timeregex");

		List<org.dom4j.Element> regex = timeregex.elements();

		for (org.dom4j.Element e : regex) {
			regexList.put(e.getStringValue().trim(), e.attributeValue("priority"));
		}

		return regexList;
	}

	/**
	 * 提取时间的核心代码。
	 * 
	 * @param e
	 */
	public void findTime(Element e) {
		if (e == null) {
			return;
		}
		String timeString = e.ownText();
		if (timeString != null && !timeString.isEmpty()) {
			Pattern p;
			Matcher m;
			for (String re : regex_map.keySet()) {
				p = Pattern.compile(re);
				m = p.matcher(timeString);
				while (m.find()) {
					if (timeString.length() < 100) {
						if (res == null) {
							res = new HashMap<String, Integer>();
							res.put(timeString, Integer.valueOf(regex_map.get(re)));
						} else {
							res.put(timeString, Integer.valueOf(regex_map.get(re)));
						}
					}
				}
			}
		}

		Elements children = e.children();
		if (children != null && children.size() > 0) {
			for (Element ee : children) {
				findTime(ee);
			}
		}
	}

	/**
	 * 从URL中取出时间。<br>
	 * 例如：http://news.cnr.cn/native/gd/20150731/t20150731_519373651.shtml
	 * 
	 * @param url
	 * @return
	 */
	public static String fetchTimeFromURL(String url) {
		if (url == null || url.length() <= 0) {
			return null;
		} else {
			String REGEX = "[0-9]{4}[/|-]?(((0[12345789]|(10|11|12)))[/|-]?(0[1-9]|[1-2][0-9]|3[0-1])?)";
			Pattern p = Pattern.compile(REGEX);
			Matcher m = p.matcher(url);
			if (m.find()) {
				return m.group(0);
			} else {
				return null;
			}
		}
	}
}
