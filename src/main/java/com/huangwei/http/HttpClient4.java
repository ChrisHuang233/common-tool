package com.huangwei.http;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * HttpClient工具
 */
@Slf4j
public class HttpClient4 {

	/** 连接超时时间（单位：毫秒） */
	private static final int CONNECT_TIMEOUT = 1000 * 5;
	/** 读取数据超时时间 */
	private static final int SOCKET_TIMEOUT = 1000 * 180;

	/**
	 * GET请求
	 * 
	 * @param url
	 *            请求地址（不能为空）
	 * @param charset
	 *            字符编码集（为空：使用默认字符集）
	 * @return null 或 字符串
	 * @throws IllegalArgumentException
	 *             参数错误
	 * @throws Exception
	 *             错误/异常
	 */
	public static String doGet(String url, String charset) throws Exception {
		if (url == null || "".equals(url = url.trim()))
			throw new IllegalArgumentException("请求地址不能为空！");
		if (charset != null && !Charset.isSupported(charset))
			throw new IllegalArgumentException("不支持的字符编码集：" + charset);

		long beginTime = 0;
		CloseableHttpClient client = getClientInstance(url);// HttpClient实例
		CloseableHttpResponse response = null;
		try {
			log.info("地址：" + url);
			HttpGet get = new HttpGet(url);// GET方法实例
			setTimeout(get, CONNECT_TIMEOUT, SOCKET_TIMEOUT);// 设置超时时间
			customRequestHeader(get);// 定制请求头

			beginTime = System.nanoTime();
			response = client.execute(get);
		} catch (Exception e) {
			log.error("通讯异常：" + e);
			log.info("用时：" + (System.nanoTime() - beginTime) / 1000000L + "毫秒");
			closeClient(client, response);
			throw e;
		}

		try {
			int code = response.getStatusLine().getStatusCode();
			log.info("状态码：" + code);
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				log.warn("响应消息为空...");
				log.info("用时：" + (System.nanoTime() - beginTime) / 1000000L + "毫秒");
				return null;
			}

			String result = null;
			long len = entity.getContentLength();
			if (len != -1 && len < 2048) {/* 当返回值长度较小的时候，使用工具类 */
				if (charset == null) {
					result = EntityUtils.toString(entity);
				} else {
					result = EntityUtils.toString(entity, charset);
				}
			} else {/* 否则使用IO流来读取 */
				BufferedReader reader;
				if (charset == null) {
					reader = new BufferedReader(new InputStreamReader(entity.getContent()));
				} else {
					reader = new BufferedReader(new InputStreamReader(entity.getContent(), charset));
				}
				StringBuffer sb = new StringBuffer();
				String line;
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				reader.close();
				result = sb.toString();
			}
			log.info("返回：" + result);
			log.info("用时：" + (System.nanoTime() - beginTime) / 1000000L + "毫秒");
			return result.length() == 0 ? null : result;
		} catch (Exception e) {
			log.error("处理响应出现异常：" + e);
			log.info("用时：" + (System.nanoTime() - beginTime) / 1000000L + "毫秒");
			throw e;
		} finally {
			closeClient(client, response);
		}
	}

	/**
	 * POST请求
	 * 
	 * @param url
	 *            请求地址（不能为空）
	 * @param parameter
	 *            参数（可为空；允许的类型为：String, List&lt;NameValuePair&gt;, Map&lt;String, Object&gt;）
	 * @param charset
	 *            字符编码集（为空：使用默认字符集）
	 * @return null 或 字符串
	 * @throws IllegalArgumentException
	 *             参数错误
	 * @throws Exception
	 *             错误/异常
	 */
	public static String doPost(String url, Object parameter, String charset) throws Exception {
		if (url == null || "".equals(url = url.trim()))
			throw new IllegalArgumentException("请求地址不能为空！");
		if (charset != null && !Charset.isSupported(charset))
			throw new IllegalArgumentException("不支持的字符编码集：" + charset);

		HttpEntity entity = null;
		if (parameter != null) {/* 处理参数 */
			try {
				entity = parameterHandle(parameter, charset);
			} catch (Exception e) {
				throw new IllegalArgumentException("参数错误：" + e.getMessage());
			}
		}

		long beginTime = 0;
		CloseableHttpClient client = getClientInstance(url);// HttpClient实例
		CloseableHttpResponse response = null;
		try {
			log.info("地址：" + url);
			HttpPost post = new HttpPost(url);// POST方法实例
			setTimeout(post, CONNECT_TIMEOUT, SOCKET_TIMEOUT);// 设置超时时间
			customRequestHeader(post);// 定制请求头

			if (entity != null) {
				post.setEntity(entity);// 设置参数
				log.info("参数：" + EntityUtils.toString(entity));
			}

			beginTime = System.nanoTime();
			response = client.execute(post);
		} catch (Exception e) {
			log.error("通讯异常：" + e);
			log.info("用时：" + (System.nanoTime() - beginTime) / 1000000L + "毫秒");
			closeClient(client, response);
			throw e;
		}

		try {
			int code = response.getStatusLine().getStatusCode();

			log.info("状态码：" + code);
			entity = response.getEntity();
			if (entity == null) {
				log.warn("响应消息为空...");
				log.info("用时：" + (System.nanoTime() - beginTime) / 1000000L + "毫秒");
				return null;
			}

			String result = null;
			long len = entity.getContentLength();
			if (len != -1 && len < 2048) {/* 当返回值长度较小的时候，使用工具类 */
				if (charset == null) {
					result = EntityUtils.toString(entity);
				} else {
					result = EntityUtils.toString(entity, charset);
				}
			} else {/* 否则使用IO流来读取 */
				BufferedReader reader;
				if (charset == null) {
					reader = new BufferedReader(new InputStreamReader(entity.getContent()));
				} else {
					reader = new BufferedReader(new InputStreamReader(entity.getContent(), charset));
				}
				StringBuffer sb = new StringBuffer();
				String line;
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				reader.close();
				result = sb.toString();
			}
			log.info("返回：" + result);
			log.info("用时：" + (System.nanoTime() - beginTime) / 1000000L + "毫秒");
			return result.length() == 0 ? null : result;
		} catch (Exception e) {
			log.error("处理响应出现异常：" + e);
			log.info("用时：" + (System.nanoTime() - beginTime) / 1000000L + "毫秒");
			throw e;
		} finally {
			closeClient(client, response);
		}
	}

	/** 关闭HttpClient */
	private static void closeClient(CloseableHttpClient client, CloseableHttpResponse response) {
		if (response != null) {
			try {
				response.close();
			} catch (Exception e) {
				log.error("关闭HttpResponse出错：" + e.toString());
			}
		}

		if (client != null) {
			try {
				client.close();
			} catch (Exception e) {
				log.error("关闭HttpClient出错：" + e.toString());
			}
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

		if (url.toLowerCase().startsWith("https:")) {
			return createInsecureSSLClient();
		} else {
			return HttpClients.createDefault();
		}
	}

	/** 创建一个不安全的SSL客户端（未载入SSL证书） */
	public static CloseableHttpClient createInsecureSSLClient() {
		try {
			/* 未载入本地SSL证书，不安全 */
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;/* 未进行客户认证，信任所有 */
				}
			}).build();

			return HttpClients.custom().setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext)).build();
		} catch (Exception e) {
			log.error("创建SSL客户端出现异常：" + e);
			return HttpClients.createDefault();
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
	 * 定制请求头
	 * 
	 * @param request
	 *            请求（不能为空）
	 * @throws IllegalArgumentException
	 *             请求对象为空
	 */
	public static void customRequestHeader(HttpRequestBase request) {
		if (request == null) {
			throw new IllegalArgumentException("请求对象不能为空！");
		}

//		final String userAgent = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:59.0) Gecko/20100101 Firefox/59.0";// 火狐
//		request.setHeader("User-Agent", userAgent);
//		request.setHeader("Content-Type", "application/json");
		request.setHeader("Content-Type", "application/json; charset=utf-8");
//		request.setHeader("Accept", "application/json");
//		request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//		request.setHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
//		request.setHeader("Accept-Encoding", "gzip");
//		request.setHeader("Pragma", "no-cache");
//		request.setHeader("Cache-Control", "no-cache");
	}

	/**
	 * 参数处理
	 * 
	 * @param parameter
	 *            参数（允许的类型为：String, List&lt;NameValuePair&gt;, Map&lt;String, Object&gt;）
	 * @param charset
	 *            字符编码集（可为空）
	 * @return org.apache.http.HttpEntity
	 * @throws Exception
	 *             错误原因
	 */
	@SuppressWarnings("unchecked")
	private static HttpEntity parameterHandle(Object parameter, String charset) throws Exception {
		if (parameter == null)
			return null;
		if (charset != null && !Charset.isSupported(charset))
			throw new Exception("不支持的字符编码集：" + charset);

		if (parameter instanceof String) {
			return charset == null ? new StringEntity((String) parameter)
					: new StringEntity((String) parameter, charset);
		}

		List<NameValuePair> list = null;
		if (parameter instanceof List) {
			List<?> temp = (List<?>) parameter;
			if (!temp.isEmpty()) {
				Object o = temp.get(0);
				if (o instanceof NameValuePair)
					list = (List<NameValuePair>) parameter;
				else
					throw new Exception("不支持的参数类型：List<" + o.getClass().getCanonicalName() + ">");
			}
		} else if (parameter instanceof Map) {
			Map<String, Object> map = (Map<String, Object>) parameter;
			list = new ArrayList<NameValuePair>();
			for (Entry<String, Object> en : map.entrySet()) {
				list.add(new BasicNameValuePair(en.getKey(), String.valueOf(en.getValue())));
			}
		} else {
			throw new Exception("不支持的参数类型：" + parameter.getClass().getCanonicalName());
		}

		if (list == null || list.isEmpty())
			return null;

		return charset == null ? new UrlEncodedFormEntity(list) : new UrlEncodedFormEntity(list, charset);
	}

	public static void main(String[] args) {
		String url = null, result = null, charset = "UTF-8";
		Object parameter = null;
		try {
			url = "";
			parameter = "";
			result = doPost(url, parameter, charset);
//			result = doGet(url, "UTF-8");
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}