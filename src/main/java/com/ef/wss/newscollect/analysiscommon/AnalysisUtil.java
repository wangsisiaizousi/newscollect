/*
 * Copyright (c) 2010-2018 EEFUNG Software Co.Ltd. All rights reserved.
 * 版权所有(c)2010-2018湖南蚁坊软件有限公司。保留所有权利。
 */
package com.ef.wss.newscollect.analysiscommon;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;


import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.antfact.ant.common.utils.DomainUtils;
import com.antfact.ant.common.utils.GsonUtils;

import javax.annotation.PostConstruct;


/**
 * <p>页面分析工具类</p>
 * <p>
 * 创建日期 2018年2月8日
 *
 * @author Lu Zhu(zhulu@eefung.com)
 * @since $version$
 */
@Component
public class AnalysisUtil {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private RegUtil regUtil;
    private String[] invalidPageKeywords = {};// 无效页面关键字
    @Value("${invalid.page.keywords}")
    private String invalidPageKeywordsStr;

    private String[] loginPageKeywords = {};// 登录页关键字
    @Value("${login.page.keywords}")
    private String loginPageKeywordsStr;

    private String[] invalidLinkKeywords = {};// 无效链接
    @Value("${invalid.link.keywords}")
    private String invalidLinkKeywordsStr;

    private String[] invalidTextKeywords = {};// 无效文本
    @Value("${invalid.text.keywords}")
    private String invalidTextKeywordsStr;

    private List<String> columnKeywords;//栏目关键字

    @Value("${column.keywords}")
    protected String columnKeywordStr;

    @Value("${harmfulInfo.minCount}")
    private int minCount;//页面所含有害信息最小值

    @Value("${harmfulInfo.keywords}")//有害关键字
    private String harmfulKeywords;
    private String [] harmfulKeywordsArray = {} ;


    @PostConstruct
    private void init() {
        logger.info("启动{}", this.getClass().getSimpleName());
        invalidPageKeywords = invalidPageKeywordsStr.split(",");
        loginPageKeywords = loginPageKeywordsStr.split(",");
        invalidLinkKeywords = invalidLinkKeywordsStr.split(",");
        invalidTextKeywords = invalidTextKeywordsStr.split(",");
        columnKeywords = Arrays.asList(columnKeywordStr.split(","));
        harmfulKeywordsArray = harmfulKeywords.split(",");
        logger.info("init! invalidPageKeywords-{},loginPageKeywords-{},invalidLinkKeywords-{},invalidTextKeywords-{}" +
                        "harmfulKeywordsArray-{}",
                GsonUtils.getGson().toJson(invalidPageKeywords),
                GsonUtils.getGson().toJson(loginPageKeywords),
                GsonUtils.getGson().toJson(invalidLinkKeywords),
                GsonUtils.getGson().toJson(invalidTextKeywords),
                GsonUtils.getGson().toJson(columnKeywords),
                GsonUtils.getGson().toJson(harmfulKeywordsArray)
        );
    }


    /**
     * 判断无效链接
     *
     * @param link
     * @param text
     * @return
     */
    public boolean invalidLink(String link, String text) {

        if (StringUtils.isBlank(link)) {
            return true;//链接为空，无效
        } else {
            String _link = link.toLowerCase();
            if (!_link.startsWith("http")) {//链接不以http开头为无效链接
                return true;
            }
            for (String ilk : invalidLinkKeywords) {//链接包含无效链接关键字为无效链接
                if (_link.contains(ilk)) {
//                    logger.info("invalidLink!{}-{}", link, text);
                    return true;
                }
            }
        }
        if (StringUtils.isNotBlank(text)) {
            text = text.replace(" ", "").trim().toLowerCase();
            if (text.length() > AnalysisConstants.COLUMN_TEXT_MAX) {//链接文本长度大于栏目链接文本最大长度，不根据链接文本判断链接是否有效
                return false;
            } else {
                for (String itk : invalidTextKeywords) {
                    if (text.contains(itk)) {
//                        logger.info("invalidLink!{}-{}", link, text);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 通过title元素文本与H元素文本的最大公共字串来得到标题
     *
     * @param doc
     * @param url
     * @return
     */
    public String getTitleByLcs(Document doc, String url) {
        String title = "";
        try {
            if (doc != null) {
                String titleElText = doc.select("title").text();
                String tem = "";
                if (titleElText.contains("|")) {
                    tem = titleElText.substring(titleElText.indexOf("|"), titleElText.length());
                }
                if (titleElText.contains("-")) {
                    if (StringUtils.isBlank(tem)) {
                        tem = titleElText;
                    }
                    tem = tem.substring(0, tem.indexOf("-"));
                }
                if (StringUtils.isNotBlank(tem)) {
                    logger.info("{} change to {},url:{}!", titleElText, tem, url);
                    titleElText = tem;
                }

                if (commonUtil.statsWordCount(titleElText) < AnalysisConstants.CONTENT_TEXT_MIN) {
                    return title;
                }

                Elements titles = doc.select("h1,h2,h3,h4,h5");// 查找标题可能存在的标签
                if (titleElText.length() > 0) {
                    for (Element element : titles) {
                        String lcs = commonUtil.lcs(titleElText, element.text());// 计算标题和标题文本的最大公共子串
                        if (lcs.length() > title.length() && commonUtil.statsWordCount(titleElText) >= AnalysisConstants.CONTENT_TEXT_MIN) {
                            title = lcs;
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error(url + "-getTitleByLcs error!", e);
        }
        return title;
    }


    /**
     * 判断页面源码是否无效(源码过短，链接过少)
     *
     * @param sourceCode
     * @param url
     * @param doc
     * @return
     */
    public boolean isInvalidPage(String sourceCode, String url, Document doc) {
        if(doc == null){
            try {
                doc = Jsoup.parse(sourceCode, url);
            } catch (Exception e) {
            }
        }
        if (StringUtils.isBlank(sourceCode) || doc == null || sourceCode.length() < 500) {// 源码长度小于500,无效页面
            logger.info("invalidPage cause sourceCode too short! {}", url);
            return true;
        }
        try {
            if ( doc.select("a").size() < 10) {
                logger.info("invalidPage cause aSize too little! {}", url);
                return true;
            }
        } catch (Exception e) {//异常忽略
        }
        return false;
    }

    /**
     * 判断页面内容是否无效（如404页面，登录注册页面）
     * @param url
     * @param doc
     * @return
     */
    public boolean isInvalidPageByPageType( String url, Document doc) {

        try {
            // 根据title文本判定是否为无效页面
            String title = doc.select("title").text();
            if (title.length() > 0) {
                for (String ipk : invalidPageKeywords) {
                    if (title.contains(ipk)) {
                        logger.info("invalidPage! cause invalidPageKeywords {}-{}", ipk,url);
                        return true;
                    }
                }
            }
            //判断是否登录页
            if(doc.select("a").size()<100){
                Elements els = doc.select("form");
                int loginKeywordsCount = 0;
                for (Element element : els) {
                    for (String lpk : loginPageKeywords) {
                        loginKeywordsCount += StringUtils.countMatches(element.text(), lpk);
                    }
                }
                if (loginKeywordsCount >= loginPageKeywords.length) {
                    logger.info("invalidPage! cause loginPage {}", url);
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("{} invalidPage error!",url,e);
        }
        return false;
    }



    /**
     * 得到有效页面的所有链接类型
     *
     * @param sourceCode
     * @param url
     * @param doc
     * @param domain
     * @return Map：key-链接 value-链接类型（1：栏目 2：内容 3：无效 4：站外）
     */
    public Map<String, String> analysisWebPageLinkType(String sourceCode, String url, Document doc, String domain) {
        Map<String, String> linkTypes = new HashMap<String, String>();
        try {
            if (doc == null) {
                try {
                    doc = Jsoup.parse(sourceCode, url);// 根据源码得到文档对象
                } catch (Exception e) {
                    return linkTypes;
                }
            }
            if (StringUtils.isBlank(domain)) {
                domain = DomainUtils.matchDomainName(url);// 域名缺省，默认顶级域名进行站外链接过滤
            }
            linkTypes = getLinkType(url, doc, domain);//得到页面上的所有链接
        } catch (Exception e) {
            logger.error(url + "-analysisLinkType error!", e);
        }
        return linkTypes;
    }

    /**
     * 得到页面上所有链接类型
     * @param url
     * @param doc
     * @param domain
     * @return Map：key-链接 value-链接类型（1：栏目 2：内容 3：无效 4：站外）
     */
    public Map<String,String> getLinkType(String url, Document doc, String domain){
    	doc.setBaseUri(url);
        Map<String, String> linkTypes = new HashMap<String, String>();
        Map<String, String> linkTextMap = new HashMap<>();// 链接和链接文本
        Map<String, String> formatLinkMap = new HashMap<>();// 格式化的链接和链接
        List<String> links = new ArrayList<>();
        Map<String, String> textIsBlank = new HashMap<>();
        /**
         * 过滤无效链接（命中无效关键字），站外链接（顶级域名不同），处理含#的链接，空白文本链接
         * 将链接格式化后存储
         */
        String formatUrl = commonUtil.linkFormatting(url); // 格式化链接,避免解析出相似度极高的链接
        formatLinkMap.put(formatUrl, url);
        Elements els = doc.select("a");
        for (Element element : els) {// 迭代页面有效链接
            String link = commonUtil.getLinkByElement(element);
            String text = commonUtil.getTextByElement(element);
            //处理#
            if(link.contains("#")){
                link = link.substring(0,link.indexOf("#"));
            }
            if (invalidLink(link, text)) {
                linkTypes.put(link, AnalysisConstants.LINK_TYPE_INVALID);// 无效链接
            } else if (!link.contains(domain+"/")) {
                linkTypes.put(link, AnalysisConstants.LINK_TYPE_OUTSITE);// 站外链接
            }else{//有效链接
                String [] dirs =  link.split("/");//得到链接的目录层级
                if(dirs.length == 3){
                    linkTypes.put(link, AnalysisConstants.LINK_TYPE_COLUMN);//一级目录判断成栏目页
                } else if(dirs.length > 3){
                    if (StringUtils.isBlank(text)) {// 空白链接文本判断
                        textIsBlank.put(link, "");
                    } else {
                        String formatLink = commonUtil.linkFormatting(link); // 格式化链接
                        if (!formatLinkMap.containsKey(formatLink)) {
                            formatLinkMap.put(formatLink, link);
                            linkTextMap.put(link, text);
                            links.add(formatLink);
                        }
                    }
                }
            }
        }
        for (Entry<String, String> entry : textIsBlank.entrySet()) {// 处理空白文本链接
            String formatLink = commonUtil.linkFormatting(entry.getKey()); // 格式化链接
            if (!formatLinkMap.containsKey(formatLink)) {
                links.add(formatLink);
                formatLinkMap.put(formatLink, entry.getKey());
                linkTextMap.put(entry.getKey(), entry.getValue());
            }
        }
        linkTypes.putAll(analysisLinkType(links,formatLinkMap,linkTextMap));//分析链接类型
        return linkTypes;
    }




    /**
     * 链接格式化后，分析其内容、栏目链接
     * @param links 格式化后的链接
     * @param formatLinkMap 格式化链接-链接
     * @param linkTextMap 链接-链接文本
     * @return
     */
    private Map<String,String> analysisLinkType(List<String> links,Map<String, String> formatLinkMap,Map<String, String> linkTextMap){
        Map<String, String> linkTypes = new HashMap<String, String>();
        Map<String, List<String>> result = regUtil.grouping(links);// 正则分组
        if(result.size() == 2){//两个分组,多的判断成内容，少的判断成栏目
            int maxSize = 0;
            String content = "";
            for (Entry<String, List<String>> entry : result.entrySet()){
                if(entry.getValue().size()>maxSize){
                    content = entry.getKey();
                    maxSize = entry.getValue().size();
                }
            }
            for (Entry<String, List<String>> entry : result.entrySet()) {
                String regStr = entry.getKey();
                List<String> formates = entry.getValue();
                String type = AnalysisConstants.LINK_TYPE_COLUMN;
                if(regStr.equals(content)){
                    type = AnalysisConstants.LINK_TYPE_CONTENT;
                }
                for (String string : formates) {
                    linkTypes.put(formatLinkMap.get(string), type);
                }
            }
        }else{
            for (Entry<String, List<String>> entry : result.entrySet()) {
                String regStr = entry.getKey();
                List<String> formates = entry.getValue();
                //对于该组链接数大于4的分组，根据链接文本平均长度判断链接类型（栏目或内容）
                if (formates.size() > 4) {
                    Map<String, String> tem = new HashMap<>();
                    List<Integer> nums = new ArrayList<>();
                    // 求链接文本的平均长度
                    for (String string : formates) {// 累加文本长度
                        String link = formatLinkMap.get(string);
                        String text = linkTextMap.get(link);
                        nums.add(commonUtil.statsWordCount(text));
                        tem.put(link, AnalysisConstants.LINK_TYPE_COLUMN);
                    }
                    int avg = commonUtil.getAvg(nums);// 求该组链接文本的平均值
                    if (avg > 0) {
                        if (avg > AnalysisConstants.CONTENT_TEXT_MIN) {// 链接文本平均长度大于内容链接文本最小长度，判定成内容
                            for (Entry<String, String> te : tem.entrySet()) {
                                tem.put(te.getKey(), AnalysisConstants.LINK_TYPE_CONTENT);
                            }
                        }
                        linkTypes.putAll(tem);
                        continue;
                    }
                }
                //该组链接均以字母结尾判断成栏目
                String suffix = regStr.substring(regStr.lastIndexOf("/"), regStr.length());// 得到后缀
                if(suffix.contains(".")){
                    suffix = suffix.substring(0,suffix.indexOf("."));
                }
                if (AnalysisConstants.SUFFIX_LETTER.equals(suffix)) {// 后缀为全字母，默认为栏目，匹配到栏目关键字判断成栏目
                    for (String string : formates) {
                        String link = formatLinkMap.get(string);
                        linkTypes.put(link, AnalysisConstants.LINK_TYPE_COLUMN);
                    }
                    continue;
                }

                /**
                 * 单个链接类型判断
                 */
                for (String string : formates){
                    String link = formatLinkMap.get(string);
                    boolean isColumn = false;
                    for (String ck : columnKeywords) {//判断是否匹配到栏目关键字
                        if (link.toLowerCase().contains(ck)) {
                            isColumn=true;
                        }
                    }
                    if(isColumn){
                        linkTypes.put(link, AnalysisConstants.LINK_TYPE_COLUMN);
                    }else{
                        linkTypes.put(link, AnalysisConstants.LINK_TYPE_CONTENT);
                    }
                }
            }
        }
        return linkTypes;
    }


    /**
     * 判断是否特殊的栏目页（包含内容简介）
     *
     * @param doc
     * @param url
     * @return
     */
    public boolean isSpecialColumnPage(Document doc, String url) {

        try {
            if (doc != null) {
                Elements divEls = doc.select("div");
                for (Element divEl : divEls) {
                    String className = commonUtil.getClassName(divEl);//得到className
                    if (StringUtils.isNotBlank(className)) {
                        if (divEl.text().length() - divEl.select("a").text().length() > 20) {
                            int size = doc.select("." + className).size();
                            if (size > 1) {
                                logger.info("this is columnPage!className:{},sameDivSize:{},url:{}", className, size, url);
                                return true;
                            }
                        }

                    }
                }
            }
        } catch (Exception e) {
            logger.error(url + "-analysisLinkType error!", e);
        }
        return false;
    }


    /**
     * 判断页面是否为内容页
     *
     * @param document
     * @param url
     * @return
     */
    public boolean isContentPage(Document document, String url) {

        try {
            Document doc = document.clone();
            if (url.split("/").length == 3) {//一级地址不是内容页
                return false;
            }
            //页面标题长度大于内容链接文本最小长度，判断成内容页面
            String title = getTitleByLcs(doc, url);//得到页面标题
            if (commonUtil.statsWordCount(title) >= AnalysisConstants.CONTENT_TEXT_MIN) {
                logger.info("this is contentPage!title:{},url:{}", title, url);
                return true;
            }
            //计算纯文本占全文比例超过50%判断成内容页
            Map<Element, String> eMap = new HashMap<>();//最大的单条链接元素
            int fullTextLen = doc.text().length();//全文文本长度
            Elements aEls = doc.select("a");
            for (Element aEl : aEls) {//迭代链接元素并统计单条链接最长链接文本
                Element pEl = aEl;
                while (pEl.parent().select("a").size() == 1) {
                    pEl = pEl.parent();
                }
                eMap.put(pEl, "");
            }
            for (Element element : eMap.keySet()) {//移除链接元素
                element.remove();
            }
            int textLen = doc.text().length();
            float proportion = textLen / (float) fullTextLen;//统计纯文本占全文比例
            if (proportion > 0.5) {
                if (isSpecialColumnPage(doc, url)) {//判断是否特殊的栏目页
                    return false;
                }
                logger.info("this is contentPage!proportion:{},url:{}", proportion, url);
                return true;
            }

        } catch (Exception e) {
            logger.error(url + "-isContentPage error!", e);
        }
        return false;
    }


    /**
     * 判断是否有害页面
     * @param sourceCode
     * @param url
     * @return
     */
    public  boolean isHarmfulPage(String sourceCode,String url){
        int count = 0;
        StringBuffer harmfulSb = new StringBuffer();
        for (String hk : harmfulKeywordsArray) {
            if (sourceCode.contains(hk)) {
                harmfulSb.append(hk + ",");
                count += StringUtils.countMatches(sourceCode, hk);
                if (count >= minCount) {
                    logger.info("this is harmful page! url:{},keywords:{},total:{}", url,
                            harmfulSb.subSequence(0, harmfulSb.length() - 1), count);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断网页是否被黑
     * @param doc
     * @param url
     * @return
     */
    public boolean wasHacked(Document doc, String url) {
        Elements aEls = doc.select("a");
        int minAElsSize = 200;// 根据链接元素总数，限制网站被黑判断，小于该值，不进行判断
        int minSecondaryDomain = 10;// 根据不同二级域名总数，限制网站被黑判断，小于该值，不进行判断
        float minRandomSecondaryDomainProportion = 0.6f;// 随机二级域名比例超过此值，认为该网站被黑
        if (aEls.size() <= minAElsSize) {
            return false;
        }

        // 根据二级域名的随机性判断网站被黑 (根据二级域名的随机性和长度的相似性)
        Map<String, String> secondaryDomain = new HashMap<>();
        String domian = DomainUtils.matchDomainName(url);
        // 提取同一顶级域名下的所有二级域名并存储
        for (Element element : aEls) {
            String href = element.absUrl("href");
            if (StringUtils.isNotBlank(href)) {
                String secoDomain = commonUtil.getSecondaryDomain(href, domian);
                if(StringUtils.isNotBlank(secoDomain)){
                    secondaryDomain.put(secoDomain, "");
                }
            }
        }
        if (secondaryDomain.size() < minSecondaryDomain) {// 二级域名总数小于指定标准，不进行后续判断
            return false;
        }
        // 统计随机二级域名
        Pattern randomDomainPattern = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{2,}$");// 随机二级域名正则
        List<Integer> lenList = new ArrayList<Integer>();
        int randomTotal = 0;// 统计随机字符总数
        for (Entry<String, String> entry : secondaryDomain.entrySet()) {
            String data = entry.getKey();
            lenList.add(data.length());
            if (randomDomainPattern.matcher(data).find()) {
                randomTotal++;
            }
        }
        // 根据随机二级域名在二级域名中的比例判断网站是否被黑
        float proportion = randomTotal / (float) secondaryDomain.size();// 随机二级域名所占比例
        if (proportion > minRandomSecondaryDomainProportion) {
            logger.info("{} was hacked! causerandomSecondaryDomain proportion too heavy .|proportion:{}", url,
                    proportion);
            return true;
        }
        // 根据隐藏的js判断网站被黑
        Elements els = doc.select("div").attr("style", "display: none");
        for (Element element : els) {
            if (element.children().size() == 1 && element.child(0).hasAttr("src")) {
                String data = element.child(0).attr("src");
                if (!data.startsWith("http") && data.endsWith(".js")) {
                    logger.info("{} was hacked! cause contanin hide js .|data:{},aElsSize:{},proportion:{}", url, data);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断网页是否论坛（根据含有thread，forum链接占全文链接的比例，超过60%为论坛）
     * @param doc
     * @return
     */
    public boolean isForum(Document doc, String url) {

        String[] keys = { "thread", "forum" };// 论坛关键字列表
        float minProportion = 0.6f;// 根据论坛关键字判断内容为论坛的最小比例
        String sourceData = doc.html();
        int total = 0;// 统计论坛关键字总数
        for (String key : keys) {
            total += StringUtils.countMatches(sourceData, key);
        }
        float hrefTotal = doc.select("a").size();
        float proportion = total / hrefTotal;// 论坛关键字总数占全文链接的比例
        if (proportion > minProportion) {
            logger.info(" {} is forum,cause proportion more than {}.|hrefTotal:{},total:{},proportion:{}", url,
                    minProportion, hrefTotal, total,
                    proportion);
            return true;
        }
        return false;
    }


}
