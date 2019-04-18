/*
 * Copyright (c) 2010-2017 EEFUNG Software Co.Ltd. All rights reserved.
 * 版权所有(c)2010-2017湖南蚁坊软件有限公司。保留所有权利。
 */
package com.ef.wss.newscollect.analysiscommon;

import com.antfact.ant.common.utils.DomainUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>常用工具类</p>
 * <p>
 * 创建日期 2017年11月24日
 *
 * @author Lu Zhu(zhulu@eefung.com)
 * @since $version$
 */
@Component
public class CommonUtil {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private CloseableHttpClient httpclient = HttpClients.custom().build();

    /**
     * 在字符串中得到第任意个指定字符的下标
     *
     * @param ch    指定字符
     * @param count 第任意个
     * @param str   字符串
     * @return -1,表示没找到
     */
    public int getCharIndex(String ch, int count, String str) {
        int index = -1;
        try {
            if (StringUtils.countMatches(str, ch) >= count) {
                for (int i = 0; i < count; i++) {
                    int tem = str.indexOf(ch) + 1;
                    index += tem;
                    str = str.substring(tem);
                }
            }
        } catch (Exception e) {
            logger.info(str + " getCharIndex error", e);
        }

        return index;
    }


    /**
     * 根据元素提取链接
     *
     * @param el
     * @return
     */
    public String getLinkByElement(Element el) {
        if (el == null) {
            return "";
        }
        String link = el.absUrl("href");// 得到链接地址
        if (StringUtils.isBlank(link)) {
            link = el.attr("href");
        }
        return link;
    }
    /**
     * 根据元素提取文本
     *
     * @param el
     * @return
     */
    public String getTextByElement(Element el) {
        if (el == null) {
            return "";
        }
        String text = el.text();// 得到链接文本内容
        if (StringUtils.isBlank(text)) {// 从链接的title属性获得文本
            text = el.attr("title");
        }
        if (StringUtils.isBlank(text)) {// 从链接的孩子的title属性获得文本
            text = el.children().attr("title");
        }
        return text.trim().replaceAll(" ", "");
    }

    /**
     * 最小公共子串
     *
     * @param rowStr
     * @param columnStr
     * @return
     */
    public String lcs(String rowStr, String columnStr) {
        String lcs = "";
        try {
            int rowSize = columnStr.length();// 行数
            int columnSize = rowStr.length();// 列数
            char[] rowChars = rowStr.toCharArray();
            char[] columnChars = columnStr.toCharArray();
            int[][] arrays = new int[rowSize + 1][columnSize + 1]; // 第一位表示行，第二位表示竖
            int maxLen = 0;
            int index = 0;
            for (int i = 1; i < rowSize + 1; i++) {// 给矩阵赋值
                for (int j = 1; j < columnSize + 1; j++) {
                    if (columnChars[i - 1] == rowChars[j - 1]) {
                        arrays[i][j] = arrays[i - 1][j - 1] + 1;
                        if (arrays[i][j] > maxLen) {
                            maxLen = arrays[i][j];
                            index = j;
                        }
                    } else {
                        arrays[i][j] = 0;
                    }
                }
            }
            lcs = rowStr.substring(index - maxLen, index);
        } catch (Exception e) {
            logger.info("param-rowStr:{},param-columnStr:{}", rowStr, columnStr);
            logger.error("lcs error !", e);
        }
        return lcs;
    }

    /**
     * 根据url构建其可能存在其他形式的链接
     *
     * @param url
     * @return
     */
    public List<String> buildLink(String url) {
        List<String> urls = new ArrayList<String>();
        try {
            if (StringUtils.isNotBlank(url) && url.startsWith("http")) {
                String replacePrefix = "";
                if (url.startsWith("http:")) {
                    replacePrefix = url.replace("http:", "https:");
                } else if (url.startsWith("https:")) {
                    replacePrefix = url.replace("https:", "http:");
                }
                if (StringUtils.isNotBlank(replacePrefix)) {
                    urls.add(replacePrefix);
                }
                if (url.endsWith("/")) {
                    urls.add(url.substring(0, url.length() - 1));
                } else {
                    urls.add(url + "/");
                }
                if (replacePrefix.endsWith("/")) {
                    urls.add(replacePrefix.substring(0, replacePrefix.length() - 1));
                } else {
                    urls.add(replacePrefix + "/");
                }
            }
        } catch (Exception e) {
            logger.error("buildLink error!", e);
        }

        return urls;
    }

    /**
     * 求一组数的方差
     *
     * @param list
     * @return
     */
    public float calculateVariance(List<Integer> list) {
        try {
            if (list.size() <= 1) {
                return -1;
            }
            int total = 0;
            for (Integer integer : list) {
                total += integer;
            }
            float avg = total / (float) list.size();
            float sumOfSquare = 0;// 平方和
            for (Integer integer : list) {
                sumOfSquare += (integer - avg) * (integer - avg);
            }
            return sumOfSquare / list.size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;

    }

    /**
     * map排序
     *
     * @param result
     * @return
     */
    public List<Map.Entry<String, Float>> sort(Map<String, Float> result, final String order) {
        List<Map.Entry<String, Float>> list = null;
        list = new ArrayList<Map.Entry<String, Float>>(result.entrySet());
        // 然后通过比较器来实现排序
        Collections.sort(list, new Comparator<Map.Entry<String, Float>>() {
            public int compare(Entry<String, Float> o1,
                               Entry<String, Float> o2) {
                if ("asc".equals(order.toLowerCase())) {
                    return o1.getValue().compareTo(o2.getValue());
                } else {
                    return o2.getValue().compareTo(o1.getValue());
                }

            }
        });
        return list;
    }

    /**
     * 统计句子中的单词个数
     *
     * @param sentence
     * @return
     */
    public int statsWordCount(String sentence) {
        int count = 0;
        try {
            if (StringUtils.isNotBlank(sentence)) {
                String[] arrays = sentence.split(" ");

                for (String string : arrays) {
                    if (Pattern.compile("[\\u4e00-\\u9fa5]+").matcher(string).find()) {
                        count += string.length();
                    } else {
                        count++;
                    }
                }
            }
        } catch (Exception e) {
            logger.error(sentence + "-statsWordCount error!", e);
        }
        return count;
    }

    /**
     * 得到某个元素的className
     *
     * @param element
     * @return
     */
    public String getClassName(Element element) {
        if (element != null && element.classNames().size() > 0) {
            return element.className();
        }
        return "";
    }

    /**
     * 得到某个元素的idName
     *
     * @param element
     * @return
     */
    public String getIdName(Element element) {
        if (element != null) {
            return element.id();
        }
        return "";
    }


    /**
     * 提取一个元素的结构
     *
     * @param element
     * @return
     */
    public String filterEl(Element element) {
        if (element != null) {
            return element.html().replaceAll(">[^<>]+<", "><")
                    .replaceAll("\"[^\"]+\"", "").replaceAll("\\d+", "");
        }
        return "";

    }

    /**
     * 判断元素是否底部导航元素
     *
     * @param element
     * @return
     */
    public boolean footerEl(Element element) {
        if (element != null) {
            String text = element.text().toLowerCase();
            if (text.contains("copyright") || text.contains("内容声明") || text.contains("版权声明")
                    || text.contains("版权所有")) {
                return true;
            }
            // 含有备案号，元素内容定位错误
            Pattern p = Pattern.compile("((京|津|黑|吉|辽|冀|豫|鲁|晋|陕|内蒙古|宁|陇"
                    + "|甘|新|青|藏|鄂|皖|苏|沪|浙|闽|湘|赣|川|蜀|渝|黔|贵|滇|云|粤|桂|琼)(icp|ICP)备\\d+(号)?)");
            Matcher m = p.matcher(text);
            if (m.find()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 采集网页源码
     *
     * @param url
     * @return
     */
    public String getSourceCode(String url) {
        String sourceData = "";
        try {
            sourceData = Jsoup
                    .connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31")
                    .execute().parse().toString();
        } catch (IOException e) {
            logger.error(url + "-getSourceCode error!", e);
        }
        return sourceData;
    }

    /**
     * 得到文档对象
     *
     * @param url
     * @return
     */
    public Document getSourceDoc(String url) {
        try {
            return Jsoup
                    .connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31")
                    .execute().parse();
        } catch (IOException e) {
            logger.error(url + "-getSourceDoc error!", e);
        }
        return null;
    }

    /**
     * 将文件的每一行读取到map
     *
     * @param filePath
     * @return
     */
    public Map<String, String> readFileToMap(String filePath) {
        Map<String, String> urls = new HashMap<String, String>();
        BufferedReader bufferedReader = null;
        try {
            String url = null;
            bufferedReader = new BufferedReader(new FileReader(filePath));
            while ((url = bufferedReader.readLine()) != null) {
                urls.put(url, "");
            }
            bufferedReader.close();
        } catch (Exception e) {
            logger.error("readFileToMap error!", e);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    logger.error("readFileToMap error!", e);
                }
            }
        }
        return urls;
    }

    /**
     * 删除文件
     *
     * @param filePath
     */
    public void delFile(String filePath) {

        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }

    }

    /**
     * 将数据写到文件
     *
     * @param filePath
     * @param data
     * @param delete
     */
    public void writeDataToFile(String filePath, String data, boolean delete) {

        // 删除文件
        if (delete) {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        }

        // 写文件
        BufferedWriter bufferedWriter = null;
        File targetFile = new File(filePath);
        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }
        try {

            bufferedWriter = new BufferedWriter(new FileWriter(targetFile, true));
            bufferedWriter.write(data);
        } catch (IOException e) {
            logger.error("writeDataToFile error!", e);
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    logger.error("writeDataToFile error!", e);
                }
            }
        }
    }

    /**
     * 得到get请求的结果
     *
     * @param url
     * @return
     */
    public String httpGet(String url) {
        String data = "";
        HttpGet httpGet = null;
        CloseableHttpResponse response = null;

        try {
            httpGet = new HttpGet(url);
            response = httpclient.execute(httpGet);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    data = EntityUtils.toString(resEntity, "utf-8");
                }
            }
        } catch (Exception e) {
            logger.error(url + "-httpGet error!", e);
        } finally {
            if (httpGet != null) {
                httpGet.releaseConnection();
            }
            if (response != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    logger.error(url + "-httpGet error!", e);
                }
            }
        }
        return data;
    }

    /**
     * 链接格式化（https替换成http，www.替换成""）
     *
     * @param link
     * @return
     */
    public String linkFormatting(String link) {
        //链接格式化
        String formate = link.toLowerCase();
        try {
            formate = formate.replaceAll("//www\\.", "//").
                    replaceAll("http://", "").
                    replaceAll("https://", "");

            if (formate.endsWith("/")) {
                formate = formate.substring(0, formate.length() - 1);
            }
            if (formate.contains("#")) {// 处理包含#的链接
                formate = formate.substring(0, formate.indexOf("#"));// 提取#以前的字符
            }
        } catch (Exception e) {
            logger.error(link + "-linkFormatting error!", e);
        }
        return formate;
    }

    /**
     * 求一组数的平均值
     *
     * @param nums
     * @return
     */
    public int getAvg(List<Integer> nums) {
        int avg = 0;
        if (nums != null) {
            int max = 0;
            int min = 0;
            for (Integer integer : nums) {
                if (integer > max) {
                    max = integer;
                }
                if (integer < min) {
                    min = integer;
                }
            }

            int total = 0;
            int count = 0;
            for (Integer integer : nums) {
                if (integer != min && integer != max) {
                    total += integer;
                    count++;
                }
            }
            if (total > 0 && count > 0) {
                avg = total / count;
            }
        }
        return avg;
    }

    /**
     * 获取map中的随机key
     *
     * @param map
     * @return
     */
    public String getMapRandomKey(Map<String, String> map) {

        try {
            String[] keys = map.keySet().toArray(new String[0]);
            Random random = new Random();
            String randomKey = keys[random.nextInt(keys.length)];
            return randomKey;
        } catch (Exception e) {
            logger.error("getMapRandomKey error!", e);
        }
        return "";
    }

    /**
     * 得到同一顶级域名下的二级域名
     * @param url
     * @param domian
     * @return
     */
    public String getSecondaryDomain(String url, String domian) {
        String secondaryDomain = "";
        if (url.contains("//www.")) {
            return secondaryDomain;
        }
        url = url.substring(url.indexOf("//") + 2);
        if (url.contains("/")) {
            url = url.substring(0, url.indexOf("/"));
        }
        if (url.endsWith(domian) && !url.equals(domian)) {
            secondaryDomain = url.substring(0, url.indexOf("."));
        }
        return secondaryDomain;
    }

    /**
     * 判断源码是否json格式
     *
     * @param sourceCode
     * @return
     */
    public JsonObject sourceCodeToJsonObject(String sourceCode) {
        try {
            if (sourceCode.endsWith(")")) {
                sourceCode = sourceCode.substring(sourceCode.indexOf("(") + 1, sourceCode.lastIndexOf(")"));
            }
            JsonObject jsonObject = new JsonParser().parse(sourceCode).getAsJsonObject();
            return jsonObject;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 从json格式的源码中分析链接
     * @param sourceCode
     * @param url
     * @param jsonObjKey
     * @param jsonArrayKey
     * @return
     */
    public List<String> analysisJson(String sourceCode, String url, String jsonObjKey, String jsonArrayKey,String sample) {
        List<String> links = new ArrayList<>();

        try {
            JsonObject jsonObject = sourceCodeToJsonObject(sourceCode);
            if(jsonObject!= null){
                JsonArray jsonArray = jsonObject.get(jsonArrayKey).getAsJsonArray();
                String prefix = "";
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject jsonObject1 = jsonArray.get(i).getAsJsonObject();
                    if (jsonObject1.has(jsonObjKey)) {
                        String data = jsonObject1.get(jsonObjKey).getAsString();
                        String link = data;
                        if(StringUtils.isNotBlank(sample)){
                            link = sample.replace("{"+jsonObjKey+"}",data);
                        }
                        if(link.startsWith("//")){
                            if(StringUtils.isBlank(prefix)){
                                prefix = url.substring(0, url.indexOf(":") + 1);
                            }
                            link=prefix+link;
                        }
                        if(link.startsWith("/")){
                            if(StringUtils.isBlank(prefix)){
                                String topDomain = DomainUtils.matchDomainName(url);
                                prefix = url.substring(0, url.indexOf(topDomain) + topDomain.length());
                            }
                            link=prefix+link;
                        }
                        links.add(link);
                    }
                }
            }
        } catch (Exception e) {
            logger.info("{} -analysisJson error ! jsonObjKey:{},jsonArrayKey:{}",url,jsonObjKey,jsonArrayKey);
            logger.error(url +"-analysisJson error!",e);
        }
        return links;
    }
}
