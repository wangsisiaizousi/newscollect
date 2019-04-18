package com.ef.wss.newscollect.generalcommon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jsoup.Jsoup;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * <p>提取网页正文的算法。</p>
 *
 * 创建日期 2015年8月1日
 * 
 * @author zhaoliang(zhaoliang@eefung.com)
 * @since $version$
 */
public class Article {

    private static final Log LOGGER = LogFactory.getLog(Article.class);

    public static Map<String, ArrayList<String>> map = null;
    private String resultArticle = null;
    private String sourceArticle = null;

    /**
     * 解析的内容长度限制
     */
    private static final int ARTICLE_LENGTH_LIMIT = 100000;

    public String getResultArticle() {
        return resultArticle;
    }

    public String getSourceArticle() {
        return sourceArticle;
    }

    public Article(String html) {
        resultArticle = extractArticleFromHtml(html);
        sourceArticle = extractSourceFormHtml();
    }

    public Article(byte[] htmlByteArray) {
        resultArticle = extractArticleFormHtmlByte(htmlByteArray);
        sourceArticle = extractSourceFormHtml();
    }

    static {
        InputStream inputStream = Article.class.getClassLoader().getResourceAsStream("template.xml");
        map = loadXMLFile(inputStream);
        try {
            inputStream.close();
        } catch (IOException e) {
            LOGGER.error("解析配置文件出错。", e);
        }
    }

    /*
     * 统计正文的长度。
     * private int sum = 0;
     */

    /* 包含正文的标签元素。 */
    private org.jsoup.nodes.Element element = null;

    /*
     * 正文的标签。
     * private String key = "";
     */
    /*
     * 判断正文是element的内容还是子元素。true表示是element本身的内容，否则是子元素的内容。
     * private boolean bodyflag = false;
     */

    public String extractArticleFromURL(String url) {
        org.jsoup.nodes.Document doc = null;
        try {
            doc = HtmlUtil.getHtmltoDucument(url);
        } catch (IOException e) {
            LOGGER.info("url打不开或连接出错", e);
            return null;
        }
        findElementsThatIncludeContent(doc.body());
        return extractArticleFromElement();
    }

    private String extractArticleFromHtml(String html) {
    	if (html==null) {
    		return null;
    	}
        org.jsoup.nodes.Document doc = null;
        try {
        	doc = Jsoup.parse(html);
		} catch (Exception e) {
			LOGGER.error("解析html源码出错{}",e);
			return null;
		}
        findElementsThatIncludeContent(doc.body());
        return extractArticleFromElement();
    }

    /**

     * 提取包含正文的标签内容。
     * 
     * @param html
     * @return
     */
    public String extractSourceFormHtml() {
        if (element == null) {
            return null;
        }
        return element.html();
    }

    public String extractArticleFormHtmlByte(byte[] html) {
        String charset = CharsetUtils.parseCharSet(html);
        LOGGER.info("charset：" + charset);
        org.jsoup.nodes.Document doc = null;
        InputStream is = new ByteArrayInputStream(html);
        try {
            doc = Jsoup.parse(is, charset != null ? charset : "UTF-8", "");
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
        findElementsThatIncludeContent(doc.body());
        if (element == null) {
            return null;
        }
        return extractArticleFromElement();
    }

    private String extractArticleFromElement() {
        /*
         * if (bodyflag) {
         * return element.ownText();
         * }
         * 
         * if (key.isEmpty()) {
         * return null;
         * }
         */

        String result = "";
        // org.jsoup.select.Elements contentElement = element.getElementsByTag(key);
        /*
         * if (contentElement != null) {
         * for (org.jsoup.nodes.Element e : contentElement) {
         * // 增加正文长度限制
         * if (result.length() <= ARTICLE_LENGTH_LIMIT) {
         * result = result + e.text() + "\n";
         * } else {
         * break;
         * }
         * }
         * return result.trim();
         * } else {
         * return null;
         * }
         */
        if (element != null && ARTICLE_LENGTH_LIMIT > element.text().length()) {
            return element.text();
        } else {
            return null;
        }
    }

    /**
     * 加载 template.xml 配置文件
     * 
     * @param inputstream
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map<String, ArrayList<String>> loadXMLFile(InputStream inputstream) {

        Map<String, ArrayList<String>> xmlMap = new HashMap<String, ArrayList<String>>();
        xmlMap.put("contains", new ArrayList<String>());
        xmlMap.put("exclusive", new ArrayList<String>());

        SAXReader reader = new SAXReader();
        Document document = null;
        try {
            document = reader.read(inputstream);
        } catch (DocumentException e1) {
            LOGGER.error("解析配置文件出错", e1);
        } finally {
            try {
                inputstream.close();
            } catch (IOException e1) {
                LOGGER.error("关闭流失败。", e1);
            }
        }
        Element root = document.getRootElement();
        List<Element> contains = root.elements("contains");
        Element contain = contains.get(0);
        List<Element> con = contain.elements();
        for (Element e : con) {
            xmlMap.get("contains").add(e.getStringValue().trim());
        }
        List<Element> exclusive = root.elements("exclusive");
        Element exclude = exclusive.get(0);
        List<Element> exc = exclude.elements();
        for (Element e : exc) {
            xmlMap.get("exclusive").add(e.getStringValue().trim());
        }
        return xmlMap;
    }

    /**
     * 采用递归提取正文的核心算法。
     * 
     * @param htmls
     */
    private void findElementsThatIncludeContent(org.jsoup.nodes.Element htmls) {
        boolean bl = false;
        int count = 0;
        if (htmls == null || htmls.children().size() <= 0) {
            return;
        }
        // 移除a标签、页尾元素和隐藏元素的干扰
        htmls.select("span[style~=display.*?none.*],p[style~=display.*?none.*],div[style~=display.*?none.*]").remove();
        htmls.select("a,h1").remove();
        htmls
            .select("div[class~=.*?(?i)foot.*?],div[id~=.*?(?i)foot.*?],div[id~=.*?(?i)bottom.*?],div[class~=.*?(?i)bottom.*?],div[class~=.*?(?i)news.*?],div[id~=.*?(?i)news.*?]")
            .remove();
        if (htmls.select("p:contains(ICP备),span:contains(ICP备),div:contains(ICP备)").size() > 0) {
            htmls.select("p:contains(ICP备),span:contains(ICP备)").first().parent().remove();
        }

        String htmlstext = htmls.text();
        List<org.jsoup.nodes.Element> elements = htmls.children();
        for (org.jsoup.nodes.Element tmpElement : elements) {
            int len = tmpElement.text().length();
            if (tmpElement.className().toLowerCase().contains("content")) {
                htmlstext = tmpElement.parent().text();
            }
            
//             System.out.println("======================================beg");
//             System.out.println(tmpElement.text());
//             System.out.println(htmls.text());
//             System.out.println(tmpElement.className());
//             System.out.println("======================================end");
//             System.out.println(tmpElement.text().length() * 1.0 / htmlstext.length() - htmls.ownText().length());
             
            if (len > count && tmpElement.text().length() * 1.0 / htmlstext.length() - htmls.ownText().length() > 0.5) {

                bl = true;
                element = tmpElement;
                count = len;
            }
        }
        if (bl) {
            findElementsThatIncludeContent(element);
        }

    }
    /*
     * public static void main(String[] args) {
     * String html = "<html>\r\n" +
     * "<head>\r\n" +
     * "<title>\r\n" +
     * "test\r\n" +
     * "</title>\r\n" +
     * "</head>\r\n" +
     * "<body>\r\n" +
     * "<div>\r\n" +
     * "123123131321313\r\n" +
     * "</div>\r\n" +
     * "<div>\r\n" +
     * "<a href=\"www.baidu.com\">\r\n" +
     * "2019/09/21 12:12:00\r\n" +
     * "</a>\r\n" +
     * "213\r\n" +
     * "</div>\r\n" +
     * "<div>\r\n" +
     * "<p>\r\n" +
     * "2019/09/21 12:12\r\n" +
     * "</p>\r\n" +
     * "<div class =\"Foot-b\">\r\n" +
     * "hahahhahahahh\r\n" +
     * "</div>\r\n" +
     * "</div>\r\n" +
     * "</body>\r\n" +
     * "</html>";
     * Article article = new Article();
     * article.findElementsThatIncludeContent(Jsoup.parse(html).body());
     * System.out.println(article.resultArticle);
     * }
     */
}
