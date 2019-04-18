/*
 * Copyright (c) 2010-2015 EEFUNG Software Co.Ltd. All rights reserved.
 * 版权所有(c)2010-2015湖南蚁坊软件有限公司。保留所有权利。
 */
package com.ef.wss.newscollect.analysiscommon;
/**
 * <p>常量类</p>
 * <p>
 * 创建日期 2018年2月9日
 *
 * @author Lu Zhu(zhulu@eefung.com)
 * @since $version$
 */
public interface AnalysisConstants {

    /**
     * 链接类型之栏目链接
     */
    String LINK_TYPE_COLUMN = "1";
    /**
     * 链接类型之内容链接
     */
    String LINK_TYPE_CONTENT = "2";
    /**
     * 链接类型之无效链接
     */
    String LINK_TYPE_INVALID = "3";
    /**
     * 链接类型之站外链接
     */
    String LINK_TYPE_OUTSITE = "4";

    /**
     * 链接类型之站外链接不明确
     */
    String LINK_TYPE_UNCLEAR = "5";


    /**
     * 栏目链接文本的最大长度
     */
    int COLUMN_TEXT_MAX = 4;
    /**
     * 内容链接文本最小长度
     */
    int CONTENT_TEXT_MIN = 7;

    /**
     * 字母后缀
     */
    String SUFFIX_LETTER = "/[a-zA-Z]+";

    /**
     * 源码类型为html
     */
    int SOURCE_CODE_TYPE_HTML = 1;

    /**
     * 源码类型为JSON
     */
    int SOURCE_CODE_TYPE_JSON = 2;


}
