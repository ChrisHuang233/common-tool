package com.huangwei.util;

import javax.servlet.http.HttpServletRequest;

/**
 * HTTP工具类
 */
public class HttpUtil {

	/**
	 * 是否是JSON请求
	 *
	 * @param request
	 *            HTTP请求（不能为空）
	 * @return true:是 false:否
	 */
	public static boolean isJsonRequest(HttpServletRequest request) {
		if (request == null) {
			return false;
		}

		String accept = request.getHeader("accept");
		return accept != null && accept.toLowerCase().contains("application/json");
	}

	/**
	 * 是否是AJAX请求
	 *
	 * @param request
	 *            HTTP请求（不能为空）
	 * @return true:是 false:否
	 */
	public static boolean isAjaxRequest(HttpServletRequest request) {
		if (request == null) {
			return false;
		}

		return "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
	}

	/**
	 * 获取真实IP
	 *
	 * @param request
	 *            HTTP请求（不能为空）
	 * @return 真实IP（非NULL）
	 */
	public static String getRealIp(HttpServletRequest request) {
		if (request == null) {
			return "";
		}

		String ip = request.getHeader("X-Real-IP");// Nginx反代参数
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Forwarded-For");
		}
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");// Apache HTTP Server
		}
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");// WebLogic
		}
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		// 多次反向代理后会有多个IP值，第一个IP才是真实IP
		if (ip != null && ip.contains(",")) {
			ip = ip.split(",")[0];
		}
		return ip;
	}

}
