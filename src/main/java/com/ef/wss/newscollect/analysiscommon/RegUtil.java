/*
 * Copyright (c) 2010-2017 EEFUNG Software Co.Ltd. All rights reserved.
 * 版权所有(c)2010-2017湖南蚁坊软件有限公司。保留所有权利。
 */
package com.ef.wss.newscollect.analysiscommon;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import com.antfact.ant.common.utils.GsonUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * <p>正则工具类</p>
 * <p>
 * 创建日期 2017年11月24日
 *
 * @author Lu Zhu(zhulu@eefung.com)
 * @since $version$
 */

@Component
public class RegUtil {

    @Value("${reg.util.special.characters}")
    private String specialCharactersStr;
    private List<String> specialCharacters;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @PostConstruct
    private void init() {
        logger.info("启动{}", this.getClass().getSimpleName());
        try {
            specialCharacters = Arrays.asList(specialCharactersStr.split(","));
            logger.info("specialCharacters:{}", GsonUtils.getGson().toJson(specialCharacters));
        } catch (Exception e) {
            logger.error("初始失败", e);
        }
    }


    /**
     * 将字符串替换正则
     *
     * @param string
     * @return
     */
    private String matchReg(String string) {
        try {
            if (string.matches("\\d+")) {
                return "\\d+";
            } else if (string.matches("[a-zA-Z]+")) {
                return "[a-zA-Z]+";
            } else if (string.matches("\\w+")) {
                return "\\w+";
            } else if (string.matches("[\\u4e00-\\u9fa5]+")) {
                return "[\\u4e00-\\u9fa5]+";
            }
        } catch (Exception e) {
            logger.error(string + "-matchReg error!", e);
        }
        return string;
    }


    /**
     * 提取字符串的结构
     *
     * @param string
     * @return
     */
    public String extractStructure(String string) {

        try {
            if (string.contains("{}")) {// 含有{}，不处理
                return string;
            }
            string = java.net.URLDecoder.decode(string, "UTF-8");// 字符串转码
            // 将匹配到的特殊字符替换成下标
            for (int i = 0; i < specialCharacters.size(); i++) {
                String s = specialCharacters.get(i);
                if (string.contains(s)) {
                    string = string.replaceAll(s.equals(".") ? "\\." : s.equals("?") ? "\\?" : s, "{" + i + "}");
                }

            }

            // 记录特殊字符出现的顺序
            List<Integer> indexList = new ArrayList<>();
            Pattern p = Pattern.compile("\\{(\\d+)\\}");
            Matcher m = p.matcher(string);
            while (m.find()) {//
                indexList.add(Integer.parseInt(m.group(1)));
            }
            // 将替换后的特殊字符转成特定形式
            string = string.replaceAll("\\{(\\d+)\\}", "\\{\\}");

            // 根据{}分组，将字符分割成最小单元，将最小单元去匹配正则
            String[] arrays = string.split("\\{\\}");
            StringBuffer reg = new StringBuffer();
            int i = 0;
            for (String a : arrays) {
                if (i < indexList.size()) {
                    reg.append(matchReg(a) + specialCharacters.get(indexList.get(i++)));
                } else {
                    reg.append(matchReg(a));
                }
            }
            String regStr = reg.toString();
            // 替换?并返回
            if (regStr.contains("?")) {
                regStr = regStr.replace("?", ".");
            }

            return regStr;
        } catch (Exception e) {
            logger.error(string + "-processSpecialCharacters error!", e);
        }
        return string;
    }


    /**
     * 对一组字符串根据提取的结构进行分组
     *
     * @param list 需要提取结构的字符串
     * @return
     */
    public Map<String, List<String>> grouping(List<String> list) {
        Map<String, List<String>> map = new HashMap<>();
        if (list != null) {
            for (String string : list) {
                String structure = extractStructure(string);
                if (!map.containsKey(structure)) {
                    map.put(structure, new ArrayList<String>());
                }
                map.get(structure).add(string);
            }
        }
        return map;
    }

}
