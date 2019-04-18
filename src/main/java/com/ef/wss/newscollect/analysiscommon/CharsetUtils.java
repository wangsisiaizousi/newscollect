package com.ef.wss.newscollect.analysiscommon;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CharsetUtils {
    private static final String DEFAULT_CHARSET = "UTF-8";

    private static final String WEB_CHAREST_REGEX_META = "<[mM][eE][tT][aA]\\s[^>]*charset=['\"]?([A-Za-z\\d\\-]+)['\"]?";
    private static final Pattern META_CHARSET_PATTERN = Pattern.compile(WEB_CHAREST_REGEX_META);

    private static final String WEB_CHAREST_REGEX_XML = "<\\?[xX][mM][lL]\\s[^>]*encoding=['\"]([^'\"]+)['\"]";
    private static final Pattern XML_CHARSET_PATTERN = Pattern.compile(WEB_CHAREST_REGEX_XML);

    private static final Log LOG = LogFactory.getLog(CharsetUtils.class);

    public static String parseCharSet(byte[] content) {
        if (content == null) {
            return null;
        }
        String charset = null;// 编码
        String strContent = null;// 内容
        if (content.length > 2000) {
            byte[] contentByte2000 = new byte[2000];
            System.arraycopy(content, 0, contentByte2000, 0, 2000);
            try {
                strContent = new String(contentByte2000, DEFAULT_CHARSET);
            } catch (UnsupportedEncodingException e) {
                LOG.error(e.getMessage(), e);
            }
            charset = getWebPageCharset(strContent);
            if (charset == null && strContent.length() > 4000) {
                byte[] contentByte4000 = new byte[4000];
                System.arraycopy(content, 0, contentByte4000, 0, 4000);
                try {
                    strContent = new String(contentByte4000, DEFAULT_CHARSET);
                } catch (UnsupportedEncodingException e) {
                    LOG.error(e.getMessage(), e);
                }
                charset = getWebPageCharset(strContent);
            }
        } else {
            try {
                strContent = new String(content, DEFAULT_CHARSET);
            } catch (UnsupportedEncodingException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        if (charset == null) {
            charset = getWebPageCharset(strContent);
        }
        if (charset != null) {
            if ("GB2312".equals(charset.toUpperCase())) {
                charset = "gbk";
            }
        }
        return charset;
    }

    /**
     * 从网页内容中提取网页编码。
     * 
     * @param content
     *            网页内容
     * @return 网页编码
     */
    public static String getWebPageCharset(String content) {
        if (content == null || content.trim().length() == 0) {
            return null;
        }
        String charset = null;
        Matcher matcher = META_CHARSET_PATTERN.matcher(content);
        if (matcher.find()) {
            charset = matcher.group(1);
        }
        if (charset == null) {
            matcher = XML_CHARSET_PATTERN.matcher(content);
            if (matcher.find()) {
                charset = matcher.group(1);
            }
        }
        return charset;
    }

}
