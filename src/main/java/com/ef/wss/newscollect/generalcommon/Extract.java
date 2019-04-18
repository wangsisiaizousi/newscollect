package com.ef.wss.newscollect.generalcommon;

import static org.apache.commons.lang3.ArrayUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.ef.wss.newscollect.analysiscommon.GsonUtils;

import java.io.UnsupportedEncodingException;
import java.util.Stack;

/**
 * 
 * <p>网页正文、标题和时间的提取。</p>
 *
 * 创建日期 2015年7月31日
 * 
 * @author zhaoliang(zhaoliang@eefung.com)
 * @since $version$
 */
public class Extract {
    private static final Log LOGGER = LogFactory.getLog(Extract.class);

    /**
     * <p>通过url提取标题、时间、正文和包含正文的标签。<p>
     * 
     * @param url
     * @return 包含正文，标题，时间和包含正文标签源码的{@code WebDocument}对象。
     */
    public static WebDocument extractByURL(String url) {
        String html = HtmlUtil.getHtml(url);
        WebDocument webDocument = extractByHTML(html);
        return webDocument;
    }
 
    /**
     * <p>通过url提取标题、时间、正文和包含正文的标签，支持翻墙。<p>
     * 
     * @param url
     * @return 包含正文，标题，时间和包含正文标签源码的{@code WebDocument}对象。
     */
    public static WebDocument extractByURLWithProxy(String url) {
        String html = HtmlUtil.getHtmlWithProxy(url);
        WebDocument webDocument = extractByHTML(html);
        return webDocument;
    }

    /**
     * <p>通过HTML提取标题、时间、正文和包含正文的标签。<p>
     * 
     * @param html
     * @return 包含正文，标题，时间和包含正文标签源码的{@code WebDocument}对象。
     */
    public static WebDocument extractByHTML(String html) {

        try {
            Article articleObjcet = new Article(html);

            String title = Title2.extract(html);
            String time = TimeExtract3.findTime(html);
            String content = articleObjcet.getResultArticle();
            String source = articleObjcet.getSourceArticle();

            WebDocument webDocument = new WebDocument();

            if (isNotEmpty(title)) {
                webDocument.setTitle(title);
            }
            if (isNotEmpty(time)) {
                webDocument.setTime(time);
            }
            if (isNotEmpty(content)) {
                webDocument.setContent(content);
            }
            if (isNotEmpty(source)) {
                webDocument.setSource(source);
            }
            articleObjcet = null;
            return webDocument;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * <p>通过HTML提取标题的标签。<p>
     * 
     * @param html
     * @return 包含标题的{@code WebDocument}对象。
     */
    public static WebDocument extractTitle(byte[] html) {
        return extractArticle(html, null);
    }

    /**
     * <p>通过HTML提取标题的标签。<p>
     * 
     * @param html
     * @param charset
     * @return 包含标题的{@code WebDocument}对象。
     */
    public static WebDocument extractTitle(byte[] html, String charset) {
        String parseHtml = bytearray2String(html, charset);
        LOGGER.info("parseHtml=" + parseHtml);
        String title = Title2.extract(parseHtml);
        WebDocument webDocument = new WebDocument();
        if (isNotEmpty(title)) {
            LOGGER.info("title=" + title);
            webDocument.setTitle(title);
        }
        return webDocument;
    }
    
    /**
     * <p>通过HTML提取标题的标签。<p>
     * 
     * @param html
     * @param charset
     * @return 包含标题的{@code WebDocument}对象。
     */
    public static WebDocument extractTitle(String html, String charset) {
        String parseHtml = html;
        String title = Title2.extract(parseHtml);
        WebDocument webDocument = new WebDocument();
        if (isNotEmpty(title)) {
            LOGGER.info("title=" + title);
            webDocument.setTitle(title);
        }
        return webDocument;
    }

    /**
     * <p>通过HTML提取时间的标签。<p>
     * 
     * @param html
     * @return 包含时间的{@code WebDocument}对象。
     */
    public static WebDocument extractTime(byte[] html) {
        return extractTime(html, null);
    }

    /**
     * <p>通过HTML提取时间的标签。<p>
     * 
     * @param html
     * @param charset
     * @return 包含时间的{@code WebDocument}对象。
     */
    public static WebDocument extractTime(byte[] html, String charset) {
        String parseHtml = bytearray2String(html, charset);
        String time = TimeExtract3.findTime(parseHtml);
        WebDocument webDocument = new WebDocument();
        if (isNotEmpty(time)) {
            webDocument.setTime(time);
        }
        return webDocument;
    }
    
    /**
     * <p>通过HTML提取时间的标签。<p>
     * 
     * @param html
     * @param charset
     * @return 包含时间的{@code WebDocument}对象。
     */
    public static WebDocument extractTime(String html, String charset) {
        String parseHtml = html;
        String time = TimeExtract3.findTime(parseHtml);
        WebDocument webDocument = new WebDocument();
        if (isNotEmpty(time)) {
            webDocument.setTime(time);
        }
        return webDocument;
    }

    /**
     * <p>通过HTML提取正文的标签。<p>
     * 
     * @param html
     * @return 包含正文的{@code WebDocument}对象。
     */
    public static WebDocument extractArticle(byte[] html) {
        return extractArticle(html, null);
    }

    /**
     * <p>通过HTML提取正文的标签。<p>
     * 
     * @param html
     * @param charset
     * @return 包含正文的{@code WebDocument}对象。
     */
    public static WebDocument extractArticle(byte[] html, String charset) {
        String parseHtml = bytearray2String(html, charset);
        String content = new Article(parseHtml).getResultArticle();
        WebDocument webDocument = new WebDocument();
        if (isNotEmpty(content)) {
            webDocument.setContent(content);
        }
        return webDocument;
    }
    
    /**
     * <p>通过HTML提取正文的标签。<p>
     * 
     * @param html
     * @param charset
     * @return 包含正文的{@code WebDocument}对象。
     */
    public static WebDocument extractArticle(String html, String charset) {
        String parseHtml = html;
        String content = new Article(parseHtml).getResultArticle();
        WebDocument webDocument = new WebDocument();
        if (isNotEmpty(content)) {
            webDocument.setContent(content);
        }
        return webDocument;
    }
    
    /**
     * <p>通过HTML提取正文源码的标签。<p>
     * 
     * @param html
     * @param charset
     * @return 包含正文的{@code WebDocument}对象。
     */
    public static WebDocument extractArticleSource(String html, String charset) {
        String parseHtml = html;
        String content = new Article(parseHtml).getSourceArticle();
        WebDocument webDocument = new WebDocument();
        if (isNotEmpty(content)) {
            webDocument.setSource(content);
        }
        return webDocument;
    }

    /**
     * <p>通过HTML提取标题、时间、正文。<p>
     * 
     * @param html
     * @return 包含正文，标题和时间的{@code WebDocument}对象。
     */
    public static WebDocument extractByHTMLFromByte(byte[] html) {
        return extractByHTML(new String(html));
    }

    /**
     * <p>通过html提取标题、时间、正文。<p>
     * 
     * @param html
     * @param charset
     * @return 包含正文，标题和时间的{@code WebDocument}对象。
     */
    public static WebDocument extractByHTMLFromByte(byte[] html, String charset) {
        if (isEmpty(html)) {
            return null;
        }
        if (isEmpty(charset)) {
            charset = CharsetUtils.parseCharSet(html);
        }
        if (isEmpty(charset)) {
            charset = "utf-8";
        }
        try {
            return extractByHTML(new String(html, charset));
        } catch (UnsupportedEncodingException e) {
            return extractByHTML(new String(html));
        }
    }

    /**
     * <p>将byte[]数组根据{@code charset}转化成字符串。<p>
     * 
     * @param html
     * @param charset
     * @return
     */
    public static String bytearray2String(byte[] html, String charset) {
        if (isEmpty(html)) {
            throw new IllegalArgumentException("bytearray2String方法中的参数html为空。");
        }
        if (isEmpty(charset)) {
            charset = CharsetUtils.parseCharSet(html);
        }
        if (isEmpty(charset)) {
            charset = "utf-8";
        }

        try {
            return new String(html, charset);
        } catch (UnsupportedEncodingException e) {
            return new String(html);
        }
    }
public static void main(String[] args) {

	System.out.println(reverse(-123));
	
}
public static int reverse(int x) {
	long res=0;
	while(x!=0) {
		int i = x%10;
		res = res*10+i;
		if(res>Integer.MAX_VALUE||res<Integer.MIN_VALUE)return 0;
		x/=10;
	}
	return (int)res;
}
}
