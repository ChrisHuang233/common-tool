package com.huangwei.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huangwei.util.HttpUtil;
import com.huangwei.util.JacksonUtil;
import com.huangwei.util.StringUtil;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 安全请求包装器 - 防SQL注入及XSS
 */
public class SecurityRequestWrapper extends HttpServletRequestWrapper {
	protected static final Logger logger = LoggerFactory.getLogger(SecurityRequestWrapper.class);

	/** 最小缓冲区大小 */
	private static final int MIN_BUFFER_SIZE = 32;
	/** 默认缓冲区大小 */
//	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String APPLICATION_JSON = "application/json";
	/** 空字符串 */
	private static final String EMPTY_STRING = "";
	/** HTML过滤 */
	private static final HtmlFilter HTML_FILTER = new HtmlFilter();

	/** 原始HttpRequest（未被包装的HttpRequest，适用于需要自行处理的特殊场景） */
	private HttpServletRequest originalRequest;
	/** 请求使用的字符集 */
	private String requestCharset;
	/** (当前请求)是否为POST方法提交的JSON请求 */
	private boolean isPostJsonRequest = false;
	/** (POST方法)请求内容 */
	private final byte[] body;

	/**
	 * 安全请求包装器 - 防SQL注入及XSS
	 * 
	 * @param request
	 *            请求（不能为空）
	 * @throws IllegalArgumentException
	 *             参数错误
	 */
	public SecurityRequestWrapper(HttpServletRequest request) {
		super(request);
		this.requestCharset = StringUtil.ifEmpty(request.getCharacterEncoding(), StandardCharsets.UTF_8.name());
		this.isPostJsonRequest = isPostJsonRequest(request);
		if (isPostJsonRequest) {
			this.body = readBody(request);
		} else {
			this.body = new byte[0];
		}
		this.originalRequest = request;
	}

	/**
	 * 判断请求是否为POST方法提交的JSON请求
	 * 
	 * @param request
	 *            请求（不能为空）
	 * @return true:是 false:否
	 */
	private boolean isPostJsonRequest(HttpServletRequest request) {
		if (request == null) {
			return false;
		}

		String method = request.getMethod();
		String contentType = request.getHeader(CONTENT_TYPE);
		if (!"POST".equalsIgnoreCase(method)) {
			return false;
		}
		if (contentType == null || !contentType.toLowerCase().contains(APPLICATION_JSON)) {
			return false;
		}
		return true;
	}

	/**
	 * 读取请求内容
	 * 
	 * @param request
	 *            请求（不能为空）
	 * @return 内容（非NULL）
	 */
	private byte[] readBody(HttpServletRequest request) {
		if (request == null) {
			return new byte[0];
		}

		final int contentLength = request.getContentLength();
		String value = null;
		try {
			value = toString(request.getInputStream(), requestCharset, contentLength);
			final String filtered = filterJson(value);
			logger.debug("[读取请求内容]uri:" + request.getRequestURI() + " requestCharset:" + requestCharset
					+ " contentLength:" + contentLength + " value:" + value + " filtered:" + filtered);
			return filtered.getBytes(requestCharset);
		} catch (Exception e) {
			logger.error("[读取请求内容]出错！uri:" + request.getRequestURI() + " requestCharset:" + requestCharset
					+ " contentLength:" + contentLength + " remoteIp:" + getRemoteAddr() + " realIp:"
					+ HttpUtil.getRealIp(originalRequest) + " value:" + value + " error:" + e.getMessage(), e);
			return new byte[0];
		}
	}

	@Override
	public String getParameter(String name) {
		final String value = super.getParameter(name);
		String filtered = value;
		if (StringUtil.isNotBlank(filtered)) {
			filtered = filter(filtered);
		}
		logger.debug("[请求过滤-Parameter]uri:" + getRequestURI() + " name:" + name + " value:" + value + " filtered:"
				+ filtered);
		return filtered;
	}

	@Override
	public String[] getParameterValues(String name) {
		final String[] values = super.getParameterValues(name);
		String[] filtered = values;
		if (filtered != null && filtered.length > 0) {
			for (int i = 0; i < filtered.length; i++) {
				filtered[i] = filter(filtered[i]);
			}
		}
		logger.debug("[请求过滤-ParameterValues]uri:" + getRequestURI() + " name:" + name + " values:"
				+ JacksonUtil.toString(values) + " filtered:" + JacksonUtil.toString(filtered));
		return filtered;
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		final Map<String, String[]> parameterMap = super.getParameterMap();
		Map<String, String[]> filtered = null;
		if (parameterMap != null && parameterMap.size() > 0) {
			filtered = new LinkedHashMap<String, String[]>(parameterMap.size());
			for (String key : parameterMap.keySet()) {
				String[] values = parameterMap.get(key);
				if (values != null && values.length > 0) {
					for (int i = 0; i < values.length; i++) {
						values[i] = filter(values[i]);
					}
				}
				filtered.put(filter(key), values);
			}
		}
		logger.debug("[请求过滤-ParameterMap]uri:" + getRequestURI() + " parameterMap:" + JacksonUtil.toString(parameterMap)
				+ " filtered:" + JacksonUtil.toString(filtered));
		return filtered;
	}

//	@Override
//	public String getHeader(String name) {
//		final String value = super.getHeader(name);
//		String filtered = value;
//		if (StringUtil.isNotBlank(filtered)) {
//			filtered = filter(filtered);
//		}
//		logger.debug("[请求过滤-Header]uri:" + getRequestURI() + " name:" + name + " value:" + value + " filtered:"
//				+ filtered);
//		return filtered;
//	}

	@Override
	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new InputStreamReader(getInputStream()));
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		if (!isPostJsonRequest) {
			return super.getInputStream();
		}

		final ByteArrayInputStream bais = new ByteArrayInputStream(body);
		return new ServletInputStream() {
			@Override
			public boolean isReady() {
				return true;
			}

			@Override
			public boolean isFinished() {
				return bais.available() < 1;
			}

			@Override
			public int available() throws IOException {
				return bais.available();
			}

			@Override
			public void setReadListener(ReadListener readListener) {
				throw new UnsupportedOperationException();
			}

			@Override
			public int read() throws IOException {
				return bais.read();
			}

			@Override
			public int read(byte[] b, int off, int len) throws IOException {
				return bais.read(b, off, len);
			}
		};
	}

	// ------ 请重写以下两个方法以避免出现“Content-length different from byte array length!”异常！ ------

	@Override
	public int getContentLength() {
		if (!isPostJsonRequest) {
			return super.getContentLength();
		} else {
			return body.length;
		}
	}

	@Override
	public long getContentLengthLong() {
		if (!isPostJsonRequest) {
			return super.getContentLengthLong();
		} else {
			return body.length;
		}
	}

	// ------ 请重写以上两个方法以避免出现“Content-length different from byte array length!”异常！ ------

	/**
	 * 过滤
	 * 
	 * @param input
	 *            待过滤的字符串
	 * @return NULL 或 过滤后的字符串
	 */
	private String filter(String input) {
		if (input == null) {
			return null;
		}
		if ("".equals(input = input.trim())) {
			return EMPTY_STRING;
		}

		String value = input;
		value = specialCharacterFilter(value);
		value = xssFilter(value);
		value = reflectFilter(value);
		value = sqlInjectFilter(value);
		return value;
	}

	/**
	 * 特殊字符过滤
	 * 
	 * @param input
	 *            待过滤的字符串
	 * @return NULL 或 过滤后的字符串
	 */
	private String specialCharacterFilter(final String input) {
		if (input == null) {
			return null;
		}
		if (StringUtil.isBlank(input)) {
			return EMPTY_STRING;
		}

		String value = input;
		value = value.replace("'", "");// 单引号
		value = value.replace("\"", "");// 双引号
		// value = value.replace(";", "");// 分号（业务中用到了英文分号，不过滤）
		value = value.replace("%", "");// 百分号
		value = value.replace("^", "");// '^'字符
		value = value.replace("~", "");// '~'字符
		value = value.replaceAll("(?i)\\\\u[0-9a-z]{4}", "");// Unicode字符
		value = value.replaceAll("\\/\\.{1,}|\\.{1,}/", "");// 路径
		value = value.replaceAll("-{2,}", "");// "--"注释
		value = value.replaceAll("\\/\\*{1,}|\\*{1,}\\/", "");// "/* */"注释
		// value = value.replaceAll("\\.{2,}", "");// 多个'.'（影响英文省略号的使用，不过滤）
		value = value.replace("\\", "");// '\'字符
		// value = value.replace("/", "");// '/'字符（影响类似“URL地址”的参数的使用，不过滤）
		if (value.contains("${")) {// "${}"标签
			value = value.replace("${", "").replace("}", "");
		}
		if (logger.isWarnEnabled() && !input.equals(value)) {
			logger.warn("[请求过滤]请求参数含有特殊字符！uri:" + getRequestURI() + " remoteIp:" + getRemoteAddr() + " realIp:"
					+ HttpUtil.getRealIp(originalRequest) + " input:" + input + " filtered:" + value);
		}
		return value;
	}

	/**
	 * XSS攻击过滤
	 * 
	 * @param input
	 *            待过滤的字符串
	 * @return NULL 或 过滤后的字符串
	 */
	private String xssFilter(final String input) {
		if (input == null) {
			return null;
		}
		if (StringUtil.isBlank(input)) {
			return EMPTY_STRING;
		}

		String value = HTML_FILTER.filter(input);
		if (logger.isWarnEnabled() && !input.equals(value)) {
			logger.warn("[请求过滤]疑似XSS攻击！uri:" + getRequestURI() + " remoteIp:" + getRemoteAddr() + " realIp:"
					+ HttpUtil.getRealIp(originalRequest) + " input:" + input + " filtered:" + value);
		}
		return value;
	}

	/**
	 * 反射攻击过滤
	 * 
	 * @param input
	 *            待过滤的字符串
	 * @return NULL 或 过滤后的字符串
	 */
	private String reflectFilter(final String input) {
		if (input == null) {
			return null;
		}
		if (StringUtil.isBlank(input)) {
			return EMPTY_STRING;
		}

		String value = input;
		value = value.replaceAll("(?i)jndi:.*//", "");// JNDI
		value = value.replaceAll("(?i)jdbc:.*//", "");// JDBC
		value = value.replaceAll("(?i)java\\.(.*\\.)*", "");
		value = value.replaceAll("(?i)javax\\.(.*\\.)*", "");
		value = value.replaceAll("(?i)javassist\\.(.*\\.)*", "");
		value = value.replaceAll("(?i)com\\.sun\\.(.*\\.)*", "");
		value = value.replaceAll("(?i)com\\.mysql\\.(.*\\.)*", "");
		value = value.replaceAll("(?i)com\\.github\\.(.*\\.)*", "");
		value = value.replaceAll("(?i)com\\.google\\.(.*\\.)*", "");
		value = value.replaceAll("(?i)com\\.oracle\\.(.*\\.)*", "");
		value = value.replaceAll("(?i)com\\.alibaba\\.(.*\\.)*", "");
		value = value.replaceAll("(?i)com\\.netflix\\.(.*\\.)*", "");
		value = value.replaceAll("(?i)com\\.facebook\\.(.*\\.)*", "");
		value = value.replaceAll("(?i)com\\.fasterxml\\.(.*\\.)*", "");
		value = value.replaceAll("(?i)com\\.microsoft\\.(.*\\.)*", "");
		value = value.replaceAll("(?i)dm\\.jdbc\\.(.*\\.)*", "");
		value = value.replaceAll("(?i)net\\.sf\\.(.*\\.)*", "");
		value = value.replaceAll("(?i)net\\.bytebuddy\\.(.*\\.)*", "");
		value = value.replaceAll("(?i)org\\.w3c\\.(.*\\.)*", "");
		value = value.replaceAll("(?i)org\\.jdom\\.(.*\\.)*", "");
		value = value.replaceAll("(?i)org\\.json\\.(.*\\.)*", "");
		value = value.replaceAll("(?i)org\\.dom4j\\.(.*\\.)*", "");
		value = value.replaceAll("(?i)org\\.jboss\\.(.*\\.)*", "");
		value = value.replaceAll("(?i)org\\.junit\\.(.*\\.)*", "");
		value = value.replaceAll("(?i)org\\.slf4j\\.(.*\\.)*", "");
		value = value.replaceAll("(?i)org\\.apache\\.(.*\\.)*", "");
		value = value.replaceAll("(?i)org\\.sqlite\\.(.*\\.)*", "");
		value = value.replaceAll("(?i)org\\.mariadb\\.(.*\\.)*", "");
		value = value.replaceAll("(?i)org\\.hibernate\\.(.*\\.)*", "");
		value = value.replaceAll("(?i)org\\.objectweb\\.(.*\\.)*", "");
		value = value.replaceAll("(?i)org\\.postgresql\\.(.*\\.)*", "");
		value = value.replaceAll("(?i)org\\.bouncycastle\\.(.*\\.)*", "");
		value = value.replaceAll("(?i)org\\.springframework\\.(.*\\.)*", "");
		value = value.replaceAll("(?i)oracle\\.jdbc\\.(.*\\.)*", "");
		if (logger.isWarnEnabled() && !input.equals(value)) {
			logger.warn("[请求过滤]疑似反射攻击！uri:" + getRequestURI() + " remoteIp:" + getRemoteAddr() + " realIp:"
					+ HttpUtil.getRealIp(originalRequest) + " input:" + input + " filtered:" + value);
		}
		return value;
	}

	/**
	 * SQL注入过滤
	 * 
	 * @param input
	 *            待过滤的字符串
	 * @return NULL 或 过滤后的字符串
	 */
	public String sqlInjectFilter(final String input) {
		if (input == null) {
			return null;
		}
		if (StringUtil.isBlank(input)) {
			return EMPTY_STRING;
		}

		String value = input;
		value = value.replaceAll("(?i)master", "FORBIDDEN_MASTER");
		value = value.replaceAll("(?i)declare", "FORBIDDEN_DECLARE");
		value = value.replaceAll("(?i)create", "FORBIDDEN_CREATE");
		value = value.replaceAll("(?i)alter", "FORBIDDEN_ALTER");
		value = value.replaceAll("(?i)drop", "FORBIDDEN_DROP");
		value = value.replaceAll("(?i)truncate", "FORBIDDEN_TRUNCATE");
		value = value.replaceAll("(?i)select", "FORBIDDEN_SELECT");
		value = value.replaceAll("(?i)insert", "FORBIDDEN_INSERT");
		value = value.replaceAll("(?i)update", "FORBIDDEN_UPDATE");
		value = value.replaceAll("(?i)delete", "FORBIDDEN_DELETE");
//		value = value.replaceAll("(?i)and", "FORBIDDEN_AND");
//		value = value.replaceAll("(?i)or", "FORBIDDEN_OR");
//		value = value.replaceAll("(?i)union", "FORBIDDEN_UNION");
		value = value.replaceAll("(?i)union all", "FORBIDDEN_UNION_ALL");
		if (logger.isWarnEnabled() && !input.equals(value)) {
			logger.warn("[请求过滤]疑似SQL注入攻击！uri:" + getRequestURI() + " remoteIp:" + getRemoteAddr() + " realIp:"
					+ HttpUtil.getRealIp(originalRequest) + " input:" + input + " filtered:" + value);
		}
		return value;
	}

	/**
	 * 过滤JSON字符串
	 * 
	 * @param input
	 *            待过滤的JSON字符串
	 * @return 过滤后的JSON字符串（非NULL）
	 * @throws IOException
	 *             IO异常
	 */
	private String filterJson(String input) throws IOException {
		if (input == null || "".equals(input = input.trim())) {
			return EMPTY_STRING;
		}
		if (input.startsWith("{") || input.startsWith("[")) {
			JsonNode node = new ObjectMapper().readTree(input);
			JsonFactory factory = new JsonFactory();
			factory.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
			ByteArrayOutputStream output = new ByteArrayOutputStream(1024 * 4);
			try (JsonGenerator generator = factory.createGenerator(output)) {
				loopJson(node, generator);
			}
			String filtered = output.toString(requestCharset);
			output.close();
			return filtered;
		} else {
			return filter(input);
		}
	}

	/**
	 * 遍历并过滤JSON节点
	 * 
	 * @param node
	 *            JSON节点（不能为空）
	 * @param generator
	 *            JSON生成器（不能为空）
	 * @throws IOException
	 *             IO异常
	 */
	private void loopJson(JsonNode node, JsonGenerator generator) throws IOException {
		if (node == null || generator == null) {
			throw new IllegalArgumentException("参数不能为空！");
		}
		if (node.isValueNode()) {
			if (node.isNull()) {
				generator.writeNull();
			} else if (node.isNumber()) {
				generator.writeNumber(node.decimalValue());
			} else if (node.isBoolean()) {
				generator.writeBoolean(node.booleanValue());
			} else if (node.isBinary()) {
				generator.writeBinary(node.binaryValue());
			} else if (node.isTextual()) {
				generator.writeString(filter(node.textValue()));
			} else {
				generator.writeString(filter(node.toString()));
			}
		} else if (node.isObject()) {
			generator.writeStartObject();
			Iterator<Map.Entry<String, JsonNode>> it = node.fields();
			while (it.hasNext()) {
				Map.Entry<String, JsonNode> e = it.next();
				generator.writeFieldName(e.getKey());
				loopJson(e.getValue(), generator);
			}
			generator.writeEndObject();
		} else if (node.isArray()) {
			generator.writeStartArray();
			Iterator<JsonNode> it = node.iterator();
			while (it.hasNext()) {
				loopJson(it.next(), generator);
			}
			generator.writeEndArray();
		}
	}

	/**
	 * 获取原始请求（未进行参数过滤）
	 * 
	 * @return 原始请求
	 */
	public HttpServletRequest getOriginalRequest() {
		return originalRequest;
	}

	/**
	 * 获取原始请求（未进行参数过滤）
	 * 
	 * @param request
	 *            请求
	 * @return 原始请求
	 */
	public static HttpServletRequest getOriginalRequest(HttpServletRequest request) {
		if (request == null) {
			return null;
		}
		if (request instanceof SecurityRequestWrapper) {
			return ((SecurityRequestWrapper) request).getOriginalRequest();
		}
		return request;
	}

	/**
	 * 输入流 -> 字符串
	 * 
	 * @param input
	 *            输入流（不能为空）
	 * @param charsetName
	 *            字符集名称
	 * @param contentLength
	 *            内容长度
	 * @return NULL 或 内容
	 * @throws IOException
	 *             IO异常
	 */
	private static String toString(InputStream input, String charsetName, int contentLength) throws IOException {
		if (input == null) {
			return null;
		}
		if (charsetName == null || "".equals(charsetName = charsetName.trim()) || !Charset.isSupported(charsetName)) {
			charsetName = StandardCharsets.UTF_8.name();
		}
		if (contentLength < MIN_BUFFER_SIZE) {
			contentLength = MIN_BUFFER_SIZE;
		}

		// 方案一
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, charsetName))) {
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();
		}
//		// 方案二
//		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
//		try (ByteArrayOutputStream output = new ByteArrayOutputStream(Math.min(buffer.length, contentLength))) {
//			int n = 0;
//			while ((n = input.read(buffer)) > 0) {
//				output.write(buffer, 0, n);
//			}
//			return output.toString(charsetName);
//		}
	}

}
