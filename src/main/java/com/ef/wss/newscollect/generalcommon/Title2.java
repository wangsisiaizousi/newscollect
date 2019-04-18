package com.ef.wss.newscollect.generalcommon;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * 
 * <p>
 * 功能描述,该部分必须以中文句号结尾。
 * </p>
 *
 * 创建日期 2015年8月11日
 * 
 * @author zhaoliang(zhaoliang@eefung.com)
 * @since $version$
 */
public class Title2 {
	public static final String TITLE = "title";
	public static final String H1 = "h1";
	public static final String REGEX = "[-|_|\\|]{1}";// waring mark如果标题中带有|-_等字符就会出现错误

	/**
	 * <p>
	 * 从html源码中提取标题。
	 * <p>
	 * 
	 * @param html
	 *            源码
	 * @return html的标题
	 */
	public static String extract(String html) {
		if (html == null) {
			return null;
		}
		String title = null;
		boolean titleFlag = true;
		String h1 = null;
		boolean h1Flag = true;

		Document doc = Jsoup.parse(html);
		if (doc == null) {
			return null;
		}
		for (Element e : doc.getAllElements()) {
			if (titleFlag && e.nodeName().equals(TITLE)) {
				title = e.ownText();
				titleFlag = false;
			}
			if (h1Flag && e.nodeName().equals(H1)) {
				h1 = e.ownText();
				h1Flag = false;
			}

			if (!h1Flag && !titleFlag) {
				break;
			}
		}

		if (!h1Flag && StringUtils.isNotEmpty(h1)) {
			if (StringUtils.isNotEmpty(title)) {
				if (title.contains(h1)) {
					return h1;
				} else {
					return title.split(REGEX)[0];
				}
			} else {
				return h1;
			}
		} else {
			if (StringUtils.isNotEmpty(title)) {
				return title.split(REGEX)[0];
			} else {
				return null;
			}
		}

	}
}
