package com.ef.wss.newscollect.analysiscommon;

/**
 * @author zhaoliang(zhaoliang@eefung.com)
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * <p>
 * 获取HTML的工具集。
 * </p>
 *
 * 创建日期 2015年8月3日
 * 
 * @author zhaoliang(zhaoliang@eefung.com)
 * @since $version$
 */
public class HtmlUtil {
	private static final Log LOGGER = LogFactory.getLog(HtmlUtil.class);

	public static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.122 Safari/537.36";
	public static String META_CHARSET_PATTERN = "<[mM][eE][tT][aA]\\s[^>]*charset=['\"]?([A-Za-z\\d\\-]+)['\"]?";
	public static String WEB_CHAREST_REGEX_XML = "<\\?[xX][mM][lL]\\s[^>]*encoding=['\"]([^'\"]+)['\"]";

	private static Map<String, Integer> foreignProxys = new HashMap<String, Integer>();
	private static List<String> randomIp = new ArrayList<String>();
	private static Random random = new Random();

	static {
		foreignProxys.put("192.168.60.15", 808);

		randomIp.add("192.168.60.15");
	}

	/**
	 * 随机获取一个国外代理IP。
	 * 
	 * @return
	 */
	private static String getRandomIp() {
		return randomIp.get(random.nextInt(randomIp.size()));
	}

	/**
	 * 提取url的HTML源码。
	 * 
	 * @param url
	 * @return
	 */
	public static String getHtml(String url) throws Exception{
		String html = getHtml(url, false);
		return html;
	}

	/**
	 * 通过代理提取url的HTML源码。
	 * 
	 * @param url
	 * @return
	 */
	public static String getHtmlWithProxy(String url) {
		return getHtml(url, true);
	}

	/**
	 * 提取url的HTML源码的{@link Document}对象。
	 * 
	 * @param url
	 * @return
	 */
	public static Document getHtmltoDucument(String url) throws IOException {
		return Jsoup.connect(url).timeout(10000).header("user-agent", USER_AGENT).get();
	}

	/**
	 * 通过代理提取url的HTML源码。
	 * 
	 * @param url
	 * @param useProxy
	 *            true表示需要翻墙代理。
	 * @return url的HTML字符串。
	 */
	public static String getHtml(String url, boolean useProxy) {
		String html = null;
		HttpGet httpget = new HttpGet(url);
		httpget.addHeader("user-agent", USER_AGENT); // 设置user-agent
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000).build();// 设置超时连接
		httpget.setConfig(requestConfig);
		CloseableHttpClient httpclient = null;
		if (useProxy) {
			String proxyIp = getRandomIp();// 随机获取一个代理IP和端口
			HttpHost proxy = new HttpHost(proxyIp, foreignProxys.get(proxyIp));
			DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
			httpclient = HttpClients.custom().setRoutePlanner(routePlanner).build(); // 设置代理
		} else {
			httpclient = HttpClients.custom().build();
		}
		CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(httpget);
		} catch (ClientProtocolException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		try {
			if (response == null) {
				return null;
			}
			HttpEntity entity = response.getEntity();
			entity.getContentEncoding();
			if (entity != null) {
				byte[] b = EntityUtils.toByteArray(entity);
				String source = new String(b);
				String str = CharsetUtils.getWebPageCharset(source);
				if ("gb2312".equals(str.toLowerCase())) {
					str = "gbk";
				}
				html = new String(b, str);
			}
		} catch (ParseException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			try {
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		return html;

	}

	/**
	 * 
	 *  * 取出域名  *   * @param url  * @return  
	 */
	public static String getHost(String url) {
		if (url == null || url.equals("")) {
			return "";
		}
		try {
			Pattern p = Pattern.compile("[^//]*$?\\.(com|cn|net|org|biz|info|cc|tv)", Pattern.CASE_INSENSITIVE);
			Matcher matcher = p.matcher(url);
			matcher = p.matcher(url);
			matcher.find();
			return matcher.group();
		} catch (Exception ex) {
			LOGGER.error("获取域名错误");
			return "";
		}
	}
}
