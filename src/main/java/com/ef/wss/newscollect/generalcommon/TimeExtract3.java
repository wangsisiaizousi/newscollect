package com.ef.wss.newscollect.generalcommon;

/**
 * @author zhaoliang(zhaoliang@eefung.com)
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * <p>从网页提取时间算法。</p>
 *
 * 创建日期 2015年8月1日
 * @author  zhaoliang(zhaoliang@eefung.com)
 * @since $version$
 */
public class TimeExtract3 {
    private static final Log LOGGER = LogFactory.getLog(TimeExtract3.class);
    private static List<Pattern> patterns = new ArrayList<Pattern>();

    static {
        try {
            sortMapByValue(loadXMLFile(Time.class.getClassLoader().getResourceAsStream("template.xml")));
            LOGGER.info("装载template.xml文件成功。");
        } catch (Exception e) {
            LOGGER.error("TimeExtract3 装载template.xml文件失败", e);
        }
    }

    public static String findTime(String html) {
    	if(html==null) {
    		return null;
    	}
        String text = Jsoup.parse(html).body().text();
        String result = null;
        for (Pattern p : patterns) {
            Matcher m = p.matcher(text);
            if (m.find()) {
                result = m.group(0);
                return result;
            }
        }
        
        if(result == null){
            for (Pattern p : patterns) {
                Matcher m = p.matcher(html);
                if (m.find()) {
                    result = m.group(0);
                    return result;
                }
            }
        }
        
        return result;
    }

    /**
     * 加载匹配时间的正则表达式至内存中。
     * 
     * @param xmlImputStream
     * @return
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Integer> loadXMLFile(InputStream xmlImputStream) {
        Map<String, Integer> regexList = new HashMap<String, Integer>();
        SAXReader reader = new SAXReader();

        org.dom4j.Document document = null;

        try {
            document = reader.read(xmlImputStream);
        } catch (DocumentException e) {
        } finally {
            try {
                xmlImputStream.close();
            } catch (IOException e1) {
                LOGGER.error(e1.getMessage(),e1);
            }
        }

        org.dom4j.Element root = document.getRootElement();
        org.dom4j.Element timeregex = root.element("timeregex");

        List<org.dom4j.Element> regex = timeregex.elements();

        for (org.dom4j.Element e : regex) {
            regexList.put(e.getStringValue().trim(), Integer.valueOf(e.attributeValue("priority")));
        }

        return regexList;
    }

    /**
     * 将传入的map根据其key=value对中的value从大到小的顺序对key排序。
     * 
     * @param oldMap
     */
    private static void sortMapByValue(Map<String, Integer> oldMap) {
        ArrayList<Map.Entry<String, Integer>> sortedList = new ArrayList<Map.Entry<String, Integer>>(oldMap.entrySet());
        Collections.sort(sortedList, new Comparator<Map.Entry<String, Integer>>() {

            @Override
            public int compare(Entry<java.lang.String, Integer> arg0, Entry<java.lang.String, Integer> arg1) {
                return arg1.getValue() - arg0.getValue();
            }
        });
        
        for(Map.Entry<String, Integer> entry : sortedList){
            Pattern pattern = Pattern.compile(entry.getKey());
            patterns.add(pattern);
        }
    }

}
