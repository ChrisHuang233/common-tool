package com.huangwei.filter;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 安全响应包装器
 */
public class SecurityResponseWrapper extends HttpServletResponseWrapper {
	protected static final Logger logger = LoggerFactory.getLogger(SecurityResponseWrapper.class);

	/** 安全响应头 */
	private static final Map<String, String> SECURITY_HEADERS = new LinkedHashMap<String, String>(16);
	/** 原始HttpResponse（未被包装的HttpResponse，适用于需要自行处理的特殊场景） */
	private HttpServletResponse originalResponse;
	/** 是否使用安全协议(HTTPS/SSL)（true:使用 false:不使用） */
	private boolean useSecureProtocol = false;

	static {
		SECURITY_HEADERS.put("X-Frame-Options", "SAMEORIGIN");
		SECURITY_HEADERS.put("X-XSS-Protection", "1; mode=block");
		SECURITY_HEADERS.put("X-Content-Type-Options", "nosniff");
	}

	/**
	 * 响应包装器
	 * 
	 * @param response
	 *            HTTP响应（不能为空）
	 * @param useSecureProtocol
	 *            是否使用安全协议(HTTPS/SSL)（true:使用 false:不使用）
	 */
	public SecurityResponseWrapper(HttpServletResponse response, boolean useSecureProtocol) {
		super(response);
		this.originalResponse = response;
		this.useSecureProtocol = useSecureProtocol;
		// 设置安全响应头
		setSecurityHeaders();
	}

	/** 设置安全响应头 */
	private void setSecurityHeaders() {
		if (SECURITY_HEADERS != null && SECURITY_HEADERS.size() > 0) {
			for (Map.Entry<String, String> e : SECURITY_HEADERS.entrySet()) {
				if (!this.containsHeader(e.getKey())) {
					this.setHeader(e.getKey(), e.getValue());
				}
			}
		}
	}

	@Override
	public void addCookie(Cookie cookie) {
		// 设置Cookie安全属性（HttpOnly、Secure）
		if (cookie != null) {
			cookie.setHttpOnly(true);
			if (useSecureProtocol) {
				cookie.setSecure(true);
			}
		}
		super.addCookie(cookie);
	}

//	@Override
//	public void setStatus(int sc) {
//		if (sc == HttpServletResponse.SC_FORBIDDEN) {
//			logger.warn("[响应包装器]状态码改写：403->404。");
//			sc = HttpServletResponse.SC_NOT_FOUND;
//		}
//		super.setStatus(sc);
//	}

	/**
	 * 获取原始响应（未被包装）
	 * 
	 * @return 原始响应
	 */
	public HttpServletResponse getOriginalResponse() {
		return originalResponse;
	}

	/**
	 * 获取原始响应（未被包装）
	 * 
	 * @param response
	 *            响应
	 * @return 原始响应
	 */
	public static HttpServletResponse getOriginalResponse(HttpServletResponse response) {
		if (response == null) {
			return null;
		}
		if (response instanceof SecurityResponseWrapper) {
			return ((SecurityResponseWrapper) response).getOriginalResponse();
		}
		return response;
	}

}
