package com.ef.wss.newscollect.generalcommon;

/**
 * 
 * <p>网页提起的标题、正文和时间对象。</p>
 *
 * 创建日期 2015年8月2日
 * 
 * @author zhaoliang(zhaoliang@eefung.com)
 * @since $version$
 */

public class WebDocument {

    /** 文档标题 */
    public String title;

    /** 文档发表时间 */
    public String time;

    /** 文档正文内容 */
    public String content;

    /** 正文的html源码 */
    public String source;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) { 
        this.source = source;
    }

    @Override
    public String toString() {
        return "WebDocument [title=" + title + ", time=" + time + ", content=" + content + ", source=" + source + "]";
    }
}
