package com.huangwei.http;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

import javax.net.ssl.SSLContext;

import com.huangwei.util.ExceptionUtil;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HttpClient工具<br>
 * <br>
 * 基于Apache HttpClient4.x
 */
public class HttpClient {
	protected static Logger logger = LoggerFactory.getLogger(HttpClient.class);

	/** 最小缓冲区大小 */
	private static final int MIN_BUFFER_SIZE = 32;
	/** 默认缓冲区大小 */
	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
	/** 连接超时时间（单位：毫秒） */
	private static final int CONNECT_TIMEOUT = 1000 * 10;
	/** 读取数据超时时间（单位：毫秒） */
	private static final int SOCKET_TIMEOUT = 1000 * 60 * 3;

	/** 字符集 - UTF-8 */
	public static final Charset CHARSET_UTF8 = StandardCharsets.UTF_8;
	/** 字符集 - GB2312（1980年） */
	public static final Charset CHARSET_GB2312 = Charset.forName("GB2312");
	/** 字符集 - GBK（1995年） */
	public static final Charset CHARSET_GBK = Charset.forName("GBK");
	/** 字符集 - GB18030（2000年、2005年） */
	public static final Charset CHARSET_GB18030 = Charset.forName("GB18030");

	/** 浏览器UserAgent */
	public static final String[] BROWSER_AGENT;

	static {
		BROWSER_AGENT = new String[] {
				// 火狐/Firefox
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:80.0) Gecko/20100101 Firefox/80.0",
				// 谷歌/Chrome
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.83 Safari/537.36",
				// 微软IE11
				"Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0)",
				// 微软Edge(Chromium内核)
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.83 Safari/537.36 Edg/85.0.564.44"
				// 可继续添加...
		};
	}

	/**
	 * GET请求 - JSON数据（UTF-8编码）
	 * 
	 * @param url
	 *            请求地址（不能为空）
	 * @return NULL 或 非空字符串
	 * @throws IllegalArgumentException
	 *             参数错误
	 * @throws Exception
	 *             通信异常/IO错误
	 */
	public static String getJson(String url) throws Exception {
		return getJson(url, null);
	}

	/**
	 * GET请求 - JSON数据（UTF-8编码）
	 * 
	 * @param url
	 *            请求地址（不能为空）
	 * @param parameter
	 *            参数（可为空）
	 * @return NULL 或 非空字符串
	 * @throws IllegalArgumentException
	 *             参数错误
	 * @throws Exception
	 *             通信异常/IO错误
	 */
	public static String getJson(String url, Map<String, String> parameter) throws Exception {
		Response response = getString(url, header4Json(), parameter, CHARSET_UTF8);
		return response == null ? null : response.getData();
	}

	/**
	 * GET请求 - JSON数据（UTF-8编码）
	 * 
	 * @param url
	 *            请求地址（不能为空）
	 * @param headers
	 *            请求头（可为空）
	 * @param parameter
	 *            参数（可为空）
	 * @return NULL 或 非空字符串
	 * @throws IllegalArgumentException
	 *             参数错误
	 * @throws Exception
	 *             通信异常/IO错误
	 */
	public static String getJson(String url, Map<String, String> headers, Map<String, String> parameter)
			throws Exception {
		Response response = getString(url, header4Json(headers), parameter, CHARSET_UTF8);
		return response == null ? null : response.getData();
	}

	/**
	 * GET请求 - 字符串数据
	 * 
	 * @param url
	 *            请求地址（不能为空）
	 * @param charset
	 *            字符集（用于响应解码；为空：UTF-8）
	 * @return 响应
	 * @throws IllegalArgumentException
	 *             参数错误
	 * @throws Exception
	 *             通信异常/IO错误
	 */
	public static Response getString(String url, Charset charset) throws Exception {
		return getString(url, null, null, charset);
	}

	/**
	 * GET请求 - 字符串数据
	 * 
	 * @param url
	 *            请求地址（不能为空）
	 * @param parameter
	 *            参数（可为空）
	 * @param charset
	 *            字符集（用于响应解码；为空：UTF-8）
	 * @return 响应
	 * @throws IllegalArgumentException
	 *             参数错误
	 * @throws Exception
	 *             通信异常/IO错误
	 */
	public static Response getString(String url, Map<String, String> parameter, Charset charset) throws Exception {
		return getString(url, null, parameter, charset);
	}

	/**
	 * GET请求 - 字符串数据
	 * 
	 * @param url
	 *            请求地址（不能为空）
	 * @param headers
	 *            请求头（可为空）
	 * @param parameter
	 *            参数（可为空）
	 * @param charset
	 *            字符集（用于响应解码；为空：UTF-8）
	 * @return 响应
	 * @throws IllegalArgumentException
	 *             参数错误
	 * @throws Exception
	 *             通信异常/IO错误
	 */
	public static Response getString(String url, Map<String, String> headers, Map<String, String> parameter,
			Charset charset) throws Exception {
		return doGet(url, headers, parameter, true, charset);
	}

	/**
	 * GET请求 - 字节数据
	 * 
	 * @param url
	 *            请求地址（不能为空）
	 * @param headers
	 *            请求头（可为空）
	 * @param parameter
	 *            参数（可为空）
	 * @return 响应
	 * @throws IllegalArgumentException
	 *             参数错误
	 * @throws Exception
	 *             通信异常/IO错误
	 */
	public static Response getByte(String url, Map<String, String> headers, Map<String, String> parameter)
			throws Exception {
		return doGet(url, headers, parameter, false, null);
	}

	/**
	 * GET请求
	 * 
	 * @param url
	 *            请求地址（不能为空）
	 * @param headers
	 *            请求头（可为空）
	 * @param parameter
	 *            参数（可为空）
	 * @param decode
	 *            是否将响应解码为字符串（true:解码 false:不解码）
	 * @param charset
	 *            字符集（用于响应解码；为空：UTF-8）
	 * @return 响应（字符串或字节）
	 * @throws IllegalArgumentException
	 *             参数错误
	 * @throws Exception
	 *             通信异常/IO错误
	 */
	private static Response doGet(String url, Map<String, String> headers, Map<String, String> parameter,
			boolean decode, Charset charset) throws Exception {
		checkUrl(url);
		if (charset == null) {
			charset = CHARSET_UTF8;
		}

		long timestamp = 0;// 时间戳（用于计算请求耗时）
		CloseableHttpClient client = getClientInstance(url);// HttpClient实例
		CloseableHttpResponse response = null;
		try {
			HttpGet get = new HttpGet(buildUrl(url, parameter));// GET方法实例
			setRequestHeader(get, headers);// 设置请求头
			setTimeout(get, CONNECT_TIMEOUT, SOCKET_TIMEOUT);// 设置超时时间

			timestamp = System.nanoTime();
			response = client.execute(get);
		} catch (Exception e) {
			logger.error("[GET请求]出错！url:" + url + " headers:" + headers + " parameter:..." + " charset:" + charset
					+ " elapsedMilliseconds:" + elapsedMilliseconds(timestamp) + " exception: ["
					+ ExceptionUtil.detail(e) + "]");
			closeClient(client, response);
			throw e;
		}
		// 注意：参数和响应可能存在敏感信息，不能打印！
		int statusCode = -1;// HTTP状态码
		try {
			statusCode = response.getStatusLine().getStatusCode();
			Response result = null;
			if (decode) {
				result = new Response(statusCode, getStringResponse(response.getEntity(), charset));
			} else {
				result = new Response(statusCode, getByteResponse(response.getEntity()));
			}
			logger.info("[GET请求]url:" + url + " headers:" + headers + " parameter:..." + " charset:" + charset
					+ " statusCode:" + statusCode + " elapsedMilliseconds:" + elapsedMilliseconds(timestamp)
					+ " response:...");
			return result;
		} catch (Exception e) {
			logger.info("[GET请求]处理响应出错！url:" + url + " headers:" + headers + " parameter:..." + " charset:" + charset
					+ " statusCode:" + statusCode + " elapsedMilliseconds:" + elapsedMilliseconds(timestamp)
					+ " exception: [" + ExceptionUtil.detail(e) + "]");
			throw e;
		} finally {
			closeClient(client, response);
		}
	}

	/**
	 * POST请求 - JSON数据（UTF-8编码）
	 * 
	 * @param url
	 *            请求地址（不能为空）
	 * @return NULL 或 非空字符串
	 * @throws IllegalArgumentException
	 *             参数错误
	 * @throws Exception
	 *             通信异常/IO错误
	 */
	public static String postJson(String url) throws Exception {
		return postJson(url, null);
	}

	/**
	 * POST请求 - JSON数据（UTF-8编码）
	 * 
	 * @param url
	 *            请求地址（不能为空）
	 * @param parameter
	 *            参数（可为空；允许的类型为：String, List&lt;NameValuePair&gt;, Map&lt;String, Object&gt;）
	 * @return NULL 或 非空字符串
	 * @throws IllegalArgumentException
	 *             参数错误
	 * @throws Exception
	 *             通信异常/IO错误
	 */
	public static String postJson(String url, Object parameter) throws Exception {
		Response response = postString(url, header4Json(), parameter, CHARSET_UTF8, CHARSET_UTF8);
		return response == null ? null : response.getData();
	}

	/**
	 * POST请求 - JSON数据（UTF-8编码）
	 * 
	 * @param url
	 *            请求地址（不能为空）
	 * @param headers
	 *            请求头（可为空）
	 * @param parameter
	 *            参数（可为空；允许的类型为：String, List&lt;NameValuePair&gt;, Map&lt;String, Object&gt;）
	 * @return NULL 或 非空字符串
	 * @throws IllegalArgumentException
	 *             参数错误
	 * @throws Exception
	 *             通信异常/IO错误
	 */
	public static String postJson(String url, Map<String, String> headers, Object parameter) throws Exception {
		Response response = postString(url, header4Json(headers), parameter, CHARSET_UTF8, CHARSET_UTF8);
		return response == null ? null : response.getData();
	}

	/**
	 * POST请求 - 字符串数据
	 * 
	 * @param url
	 *            请求地址（不能为空）
	 * @param parameter
	 *            参数（可为空；允许的类型为：String, List&lt;NameValuePair&gt;, Map&lt;String, Object&gt;）
	 * @param charset
	 *            请求字符集（用于参数编码及响应解码；为空：UTF-8）
	 * @return 响应
	 * @throws IllegalArgumentException
	 *             参数错误
	 * @throws Exception
	 *             通信异常/IO错误
	 */
	public static Response postString(String url, Object parameter, Charset charset) throws Exception {
		return postString(url, null, parameter, charset, charset);
	}

	/**
	 * POST请求 - 字符串数据
	 * 
	 * @param url
	 *            请求地址（不能为空）
	 * @param headers
	 *            请求头（可为空）
	 * @param parameter
	 *            参数（可为空；允许的类型为：String, List&lt;NameValuePair&gt;, Map&lt;String, Object&gt;）
	 * @param charset
	 *            请求字符集（用于参数编码及响应解码；为空：UTF-8）
	 * @return 响应
	 * @throws IllegalArgumentException
	 *             参数错误
	 * @throws Exception
	 *             通信异常/IO错误
	 */
	public static Response postString(String url, Map<String, String> headers, Object parameter, Charset charset)
			throws Exception {
		return postString(url, headers, parameter, charset, charset);
	}

	/**
	 * POST请求 - 字符串数据
	 * 
	 * @param url
	 *            请求地址（不能为空）
	 * @param headers
	 *            请求头（可为空）
	 * @param parameter
	 *            参数（可为空；允许的类型为：String, List&lt;NameValuePair&gt;, Map&lt;String, Object&gt;）
	 * @param requestCharset
	 *            请求字符集（用于参数编码；为空：UTF-8）
	 * @param responseCharset
	 *            响应字符集（用于响应解码；为空：与请求字符集相同）
	 * @return 响应
	 * @throws IllegalArgumentException
	 *             参数错误
	 * @throws Exception
	 *             通信异常/IO错误
	 */
	public static Response postString(String url, Map<String, String> headers, Object parameter, Charset requestCharset,
			Charset responseCharset) throws Exception {
		return doPost(url, headers, parameter, requestCharset, true, responseCharset);
	}

	/**
	 * POST请求 - 字节数据
	 * 
	 * @param url
	 *            请求地址（不能为空）
	 * @param headers
	 *            请求头（可为空）
	 * @param parameter
	 *            参数（可为空；允许的类型为：String, List&lt;NameValuePair&gt;, Map&lt;String, Object&gt;）
	 * @param requestCharset
	 *            请求字符集（用于参数编码；为空：UTF-8）
	 * @return 响应
	 * @throws IllegalArgumentException
	 *             参数错误
	 * @throws Exception
	 *             通信异常/IO错误
	 */
	public static Response postByte(String url, Map<String, String> headers, Object parameter, Charset requestCharset)
			throws Exception {
		return doPost(url, headers, parameter, requestCharset, false, null);
	}

	/**
	 * POST请求
	 * 
	 * @param url
	 *            请求地址（不能为空）
	 * @param headers
	 *            请求头（可为空）
	 * @param parameter
	 *            参数（可为空；允许的类型为：String, List&lt;NameValuePair&gt;, Map&lt;String, Object&gt;）
	 * @param requestCharset
	 *            请求字符集（用于参数编码；为空：UTF-8）
	 * @param decode
	 *            是否将响应解码为字符串（true:解码 false:不解码）
	 * @param responseCharset
	 *            响应字符集（用于响应解码；为空：与请求字符集相同）
	 * @return 响应
	 * @throws IllegalArgumentException
	 *             参数错误
	 * @throws Exception
	 *             通信异常/IO错误
	 */
	private static Response doPost(String url, Map<String, String> headers, Object parameter, Charset requestCharset,
			boolean decode, Charset responseCharset) throws Exception {
		checkUrl(url);
		final String _url = url.replaceAll("\\?.*", "");// 不打印URL中可能含有敏感信息的部分
		if (requestCharset == null) {
			requestCharset = CHARSET_UTF8;
		}
		if (responseCharset == null) {
			responseCharset = requestCharset;
		}
		// 参数处理
		HttpEntity parameterEntity = null;
		if (parameter != null) {
			try {
				parameterEntity = parameterHandle(parameter, requestCharset);
			} catch (Exception e) {
				throw new IllegalArgumentException("参数错误：" + e.getMessage());
			}
		}

		long timestamp = 0;// 时间戳（用于计算请求耗时）
		CloseableHttpClient client = getClientInstance(url);// HttpClient实例
		CloseableHttpResponse response = null;
		try {
			HttpPost post = new HttpPost(url);// POST方法实例
			setRequestHeader(post, headers);// 设置请求头
			setTimeout(post, CONNECT_TIMEOUT, SOCKET_TIMEOUT);// 设置超时时间
			// 设置参数
			if (parameterEntity != null) {
				post.setEntity(parameterEntity);
			}

			timestamp = System.nanoTime();
			response = client.execute(post);
		} catch (Exception e) {
			logger.error("[POST请求]出错！url:" + _url + " headers:" + headers + " parameter:..." + " requestCharset:"
					+ requestCharset + " responseCharset:" + responseCharset + " elapsedMilliseconds:"
					+ elapsedMilliseconds(timestamp) + " exception: [" + ExceptionUtil.detail(e) + "]");
			closeClient(client, response);
			throw e;
		}
		// 注意：参数和响应可能存在敏感信息，不能打印！
		int statusCode = -1;// HTTP状态码
		try {
			statusCode = response.getStatusLine().getStatusCode();
			Response result = null;
			if (decode) {
				result = new Response(statusCode, getStringResponse(response.getEntity(), responseCharset));
			} else {
				result = new Response(statusCode, getByteResponse(response.getEntity()));
			}
			logger.info("[POST请求]url:" + _url + " headers:" + headers + " parameter:..." + " requestCharset:"
					+ requestCharset + " responseCharset:" + responseCharset + " statusCode:" + statusCode
					+ " elapsedMilliseconds:" + elapsedMilliseconds(timestamp) + " response:...");
			return result;
		} catch (Exception e) {
			logger.error("[POST请求]处理响应出错！url:" + _url + " headers:" + headers + " parameter:..." + " requestCharset:"
					+ requestCharset + " responseCharset:" + responseCharset + " statusCode:" + statusCode
					+ " elapsedMilliseconds:" + elapsedMilliseconds(timestamp) + " exception: ["
					+ ExceptionUtil.detail(e) + "]");
			throw e;
		} finally {
			closeClient(client, response);
		}
	}

	/**
	 * 检查请求地址
	 * 
	 * @param url
	 *            请求地址（不能为空）
	 * @return true:正常 false:异常
	 * @throws IllegalArgumentException
	 *             URL为空 或 非HTTP协议地址
	 */
	private static boolean checkUrl(String url) {
		if (url == null || "".equals(url = url.trim())) {
			throw new IllegalArgumentException("请求地址不能为空！");
		}
		if (!url.matches("(?i)http(s)?://.*")) {
			throw new IllegalArgumentException("非HTTP协议地址！");
		}

		return true;
	}

	/**
	 * 构造含参URL
	 * 
	 * @param url
	 *            请求地址（不能为空）
	 * @param parameter
	 *            参数（可为空）
	 * @return 含有参数的URL（非空）
	 * @throws IllegalArgumentException
	 *             URL为空 或 URL格式错误
	 */
	public static String buildUrl(String url, Map<String, String> parameter) {
		if (url == null || "".equals(url = url.trim())) {
			throw new IllegalArgumentException("请求地址不能为空！");
		}
		try {
			URIBuilder uri = new URIBuilder(url);
			// 设置参数
			if (parameter != null && !parameter.isEmpty()) {
				for (Map.Entry<String, String> e : parameter.entrySet()) {
					uri.addParameter(e.getKey(), e.getValue());
				}
			}
			return uri.build().toString();
		} catch (Exception e) {
			throw new IllegalArgumentException("构造含参URL出错！", e);
		}
	}

	/**
	 * 获取HttpClient实例
	 * 
	 * @param url
	 *            请求地址（不能为空）
	 * @return HttpClient实例
	 */
	public static CloseableHttpClient getClientInstance(final String url) {
		if (url == null || "".equals(url.trim())) {
			throw new IllegalArgumentException("请求地址不能为空！");
		}

		if (url.toLowerCase().startsWith("https://")) {
			return createInsecureSSLClient();
		} else {
			return HttpClients.createDefault();
		}
	}

	/** 创建一个不安全的SSL客户端（未载入SSL证书） */
	public static CloseableHttpClient createInsecureSSLClient() {
		try {
			/* 未载入SSL证书，不安全 */
			SSLContext sslContext = SSLContextBuilder.create().loadTrustMaterial(TrustAllStrategy.INSTANCE).build();
			// SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
			// public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			// return true;/* 未进行客户认证，信任所有 */
			// }
			// }).build();
			return HttpClients.custom().setSSLSocketFactory(
					new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE)).build();
		} catch (Exception e) {
			logger.error("创建SSL客户端出现异常：" + ExceptionUtil.detail(e));
			return HttpClients.createDefault();
		}
	}

	/** 关闭HttpClient */
	private static void closeClient(CloseableHttpClient client, CloseableHttpResponse response) {
		if (response != null) {
			try {
				response.close();
			} catch (Exception e) {
				logger.error("关闭HttpResponse出错！exception: [" + ExceptionUtil.detail(e) + "]");
			}
		}
		if (client != null) {
			try {
				client.close();
			} catch (Exception e) {
				logger.error("关闭HttpClient出错！exception: [" + ExceptionUtil.detail(e) + "]");
			}
		}
	}

	/**
	 * 设置超时时间
	 * 
	 * @param request
	 *            请求（不能为空）
	 * @param connectTimeout
	 *            连接超时时间（大于等于零）（单位：毫秒）
	 * @param socketTimeout
	 *            数据传输超时时间（大于等于零）（单位：毫秒）
	 * @throws IllegalArgumentException
	 *             请求对象为空
	 */
	public static void setTimeout(HttpRequestBase request, int connectTimeout, int socketTimeout) {
		if (request == null) {
			throw new IllegalArgumentException("请求对象不能为空！");
		}

		request.setConfig(
				RequestConfig.custom().setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build());
	}

	/**
	 * 设置请求头
	 * 
	 * @param request
	 *            请求（不能为空）
	 * @throws IllegalArgumentException
	 *             请求对象为空
	 */
	public static void setRequestHeader(HttpRequestBase request, Map<String, String> headers) {
		if (request == null) {
			throw new IllegalArgumentException("请求对象不能为空！");
		}
		if (headers == null || headers.isEmpty()) {
			return;
		}
		// 设置请求头
		for (Map.Entry<String, String> e : headers.entrySet()) {
			request.setHeader(e.getKey(), e.getValue());
		}
	}

	/**
	 * 请求头 - 无缓存（no-cache）
	 */
	public static Map<String, String> header4NoCache() {
		Map<String, String> headers = new LinkedHashMap<String, String>();
		headers.put("Pragma", "no-cache");// HTTP/1.0兼容写法
		headers.put("Cache-Control", "no-cache");
		return headers;
	}

	/**
	 * 请求头 - 模拟浏览器
	 */
	public static Map<String, String> header4Browser() {
		Map<String, String> headers = header4NoCache();
		headers.put("User-Agent", BROWSER_AGENT[0]);
		headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		headers.put("Accept-Encoding", "gzip, deflate, br");
		headers.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
		// headers.put("Connection", "keep-alive");
		return headers;
	}

	/**
	 * 请求头 - 随机模拟浏览器
	 */
	public static Map<String, String> header4RandomBrowser() {
		Map<String, String> headers = header4Browser();
		headers.put("User-Agent", BROWSER_AGENT[new Random().nextInt(100) % BROWSER_AGENT.length]);
		return headers;
	}

	/**
	 * 请求头 - JSON（UTF-8编码）
	 * 
	 * @return JSON请求头
	 */
	public static Map<String, String> header4Json() {
		return header4Json(null);
	}

	/**
	 * 请求头 - JSON（UTF-8编码）
	 * 
	 * @param customHeaders
	 *            自定义请求头（可为空）
	 * @return JSON请求头
	 */
	public static Map<String, String> header4Json(Map<String, String> customHeaders) {
		Map<String, String> headers = header4NoCache();
		if (customHeaders != null) {
			headers.putAll(customHeaders);
		}
		headers.put("Content-Type", "application/json; charset=utf-8");
		headers.put("Accept", "application/json; charset=utf-8");
		return headers;
	}

	/**
	 * POST参数处理
	 * 
	 * @param parameter
	 *            参数（允许的类型为：String, List&lt;NameValuePair&gt;, Map&lt;String, Object&gt;）
	 * @param charset
	 *            字符集（用于参数编码；为空：UTF-8）
	 * @return org.apache.http.HttpEntity
	 * @throws Exception
	 *             错误原因
	 */
	@SuppressWarnings("unchecked")
	private static HttpEntity parameterHandle(Object parameter, Charset charset) throws Exception {
		if (parameter == null) {
			return null;
		}
		if (charset == null) {
			charset = CHARSET_UTF8;
		}
		// String实例
		if (parameter instanceof String) {
			return new StringEntity((String) parameter, charset);
		}
		// List实例、Map实例
		List<NameValuePair> list = null;
		if (parameter instanceof List) {
			List<?> temp = (List<?>) parameter;
			if (!temp.isEmpty()) {
				Object o = temp.get(0);
				if (o instanceof NameValuePair) {
					list = (List<NameValuePair>) parameter;
				} else {
					throw new Exception("不支持的参数类型：List<" + o.getClass().getCanonicalName() + ">");
				}
			}
		} else if (parameter instanceof Map) {
			Map<String, Object> map = (Map<String, Object>) parameter;
			if (!map.isEmpty()) {
				list = new ArrayList<NameValuePair>();
				for (Map.Entry<String, Object> e : map.entrySet()) {
					list.add(new BasicNameValuePair(e.getKey(), String.valueOf(e.getValue())));
				}
			}
		} else {
			throw new Exception("不支持的参数类型：" + parameter.getClass().getCanonicalName());
		}
		if (list == null || list.isEmpty()) {
			return null;
		}
		return new UrlEncodedFormEntity(list, charset);
	}

	/**
	 * 获取响应数据 - 字符串
	 * 
	 * @param entity
	 *            响应实体（不能为空）
	 * @param charset
	 *            字符集（用于响应解码；为空：UTF-8）
	 * @return NULL 或 数据（字符串）
	 * @throws Exception
	 *             异常
	 */
	private static String getStringResponse(HttpEntity entity, Charset charset) throws Exception {
		if (entity == null) {
			logger.warn("[获取响应-字符串]响应实体为空...responseEntity:" + entity + " charset:" + charset);
			return null;
		}
		if (charset == null) {
			charset = CHARSET_UTF8;
		}

		String result = EntityUtils.toString(entity, charset);
		return result.length() == 0 ? null : result;
	}

	/**
	 * 获取响应数据 - 字节
	 * 
	 * @param entity
	 *            响应实体（不能为空）
	 * @return NULL 或 数据（字符串）
	 * @throws Exception
	 *             异常
	 */
	private static byte[] getByteResponse(HttpEntity entity) throws Exception {
		if (entity == null) {
			logger.warn("[获取响应-字符串]响应实体为空...responseEntity:" + entity);
			return null;
		}
		final InputStream input = entity.getContent();
		if (input == null) {
			return null;
		}

		int contentLength = (int) entity.getContentLength();
		if (contentLength < MIN_BUFFER_SIZE) {
			contentLength = MIN_BUFFER_SIZE;
		}
		final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		// ContentLength不可信，不能作为判断数据是否完整的依据
		try (ByteArrayOutputStream output = new ByteArrayOutputStream(Math.min(buffer.length, contentLength))) {
			int n = 0;
			while ((n = input.read(buffer)) > 0) {
				output.write(buffer, 0, n);
			}
			return output.toByteArray();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (Exception e) {
					logger.error("关闭响应流出错！exception: [" + ExceptionUtil.detail(e) + "]");
				}
			}
		}
	}

	/**
	 * 耗时计算 - 毫秒
	 * 
	 * @param nanoTimestamp
	 *            纳秒时间戳（System.nanoTime()）
	 * @return N毫秒
	 */
	public static long elapsedMilliseconds(long nanoTimestamp) {
		return Math.abs(System.nanoTime() - nanoTimestamp) / 1000000L;
	}

	/**
	 * HTTP响应
	 */
	public static class Response {

		/** HTTP状态码 */
		private int code;
		/** (字符串)数据 */
		private String data;
		/** (字节)响应体 */
		private byte[] body;

		/**
		 * HTTP响应
		 * 
		 * @param code
		 *            状态码
		 * @param data
		 *            数据
		 */
		public Response(int code, String data) {
			super();
			this.code = code;
			this.data = data;
		}

		/**
		 * HTTP响应
		 * 
		 * @param code
		 *            状态码
		 * @param body
		 *            响应体
		 */
		public Response(int code, byte[] body) {
			super();
			this.code = code;
			this.body = body;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("[clazz=Response, code=").append(code).append(", data=").append(data).append(
					", body=").append(body == null ? null : "[...]").append(", length=").append(
							data == null ? (body == null ? 0 : body.length) : data.length()).append("]");
			return builder.toString();
		}

		/** HTTP状态码 */
		public int getCode() {
			return code;
		}

		/** (字符串)数据 */
		public String getData() {
			return data;
		}

		/** (字节)响应体 */
		public byte[] getBody() {
			return body;
		}

	}

	public static void main(String[] args) {
		String url = null;
		Charset requestCharset = null, responseCharset = null;
		Map<String, String> parameter = new LinkedHashMap<String, String>();
		Response response = null;
		try {
			url = "https://cloud.tencent.com";
			requestCharset = CHARSET_UTF8;
			responseCharset = CHARSET_UTF8;
			// 发送请求
			// response = getJson(url, parameter);
			// response = postJson(url, parameter);
			// response = getString(url, parameter, responseCharset);
			response = postString(url, header4Browser(), parameter, requestCharset, responseCharset);
			// response = getByte(url, parameter, parameter);
			// response = postByte(url, parameter, parameter, requestCharset);
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
