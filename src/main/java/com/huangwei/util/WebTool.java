package com.huangwei.util;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Web页面相关工具
 */
public class WebTool {
	private static Logger logger = LoggerFactory.getLogger(WebTool.class);

	/**
	 * 单一查询条件
	 * 
	 * @param key
	 *            键（不能为空）
	 * @param value
	 *            值（不能为空）
	 * @return null（参数错误） 或 条件
	 */
	public static Map<String, Object> condition(String key, Object value) {
		if (key == null || "".equals(key = key.trim()) || value == null) {
			logger.error("[单一查询条件]参数错误！key:" + key + " value:" + value);
			return null;
		}

		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put(key, value);
		return condition;
	}

	/**
	 * 简单结果
	 * 
	 * @param key
	 *            键（不能为空）
	 * @param value
	 *            值（不能为空）
	 * @return null（参数错误） 或 结果
	 */
	public static Map<String, Object> simpleResult(String key, Object value) {
		if (key == null || "".equals(key = key.trim()) || value == null) {
			logger.error("[简单结果]参数错误！key:" + key + " value:" + value);
			return null;
		}

		Map<String, Object> result = new HashMap<String, Object>(1);
		result.put(key, value);
		return result;
	}

	/**
	 * 未登录
	 * 
	 * @return 结果（JSON字符串）
	 */
	public static String unauthorized() {
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		response.put("result", false);
		response.put("message", "请登陆后重试！");
		return JsonUtil.toString(response);
	}

	/**
	 * 权限不足
	 * 
	 * @return 结果（JSON字符串）
	 */
	public static String noPower() {
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		response.put("result", false);
		response.put("message", "权限不足！");
		return JsonUtil.toString(response);
	}

	/**
	 * 分页结果 -> DataTables数据
	 * 
	 * @param offset
	 *            偏移量（可以为空；从0开始）
	 * @param total
	 *            记录总数
	 * @param list
	 *            查询结果（可能为空）
	 * @return DataTables数据（JSON字符串）
	 */
	public static String pageResult(Integer offset, int total, List<?> list) {
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		response.put("result", true);
		response.put("iDisplayStart", offset == null ? 0 : offset);// 偏移量（从0开始）
		response.put("iTotalRecords", total);// 总记录数
		response.put("iTotalDisplayRecords", total);// (过滤后)显示的记录数
		response.put("aaData", list == null ? Collections.EMPTY_LIST : list);// 结果集
		return JsonUtil.toStr(response);
	}

	/**
	 * 分页结果 -> DataTables数据（附带额外数据）
	 * 
	 * @param offset
	 *            偏移量（可以为空；从0开始）
	 * @param total
	 *            记录总数
	 * @param list
	 *            查询结果（可能为空）
	 * @param data
	 *            额外数据（可能为空）
	 * @return DataTables数据（JSON字符串）
	 */
	public static String pageResult(Integer offset, int total, List<?> list, Object data) {
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		response.put("result", true);
		response.put("iDisplayStart", offset == null ? 0 : offset);// 偏移量（从0开始）
		response.put("iTotalRecords", total);// 总记录数
		response.put("iTotalDisplayRecords", total);// (过滤后)显示的记录数
		response.put("aaData", list == null ? Collections.EMPTY_LIST : list);// 结果集
		response.put("data", data);// 额外数据
		return JsonUtil.toStr(response);
	}

	/**
	 * 分页失败 -> DataTables数据（JSON字符串）
	 * 
	 * @param message
	 *            信息
	 * @return 结果（JSON字符串）
	 */
	public static String pageFault(String message) {
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		response.put("result", false);
		response.put("message", message == null ? "查询失败！" : message);
		response.put("iDisplayStart", 0);// 偏移量（从0开始）
		response.put("iTotalRecords", 0);// 总记录数
		response.put("iTotalDisplayRecords", 0);// (过滤后)显示的记录数
		response.put("aaData", Collections.EMPTY_LIST);// 结果集
		return JsonUtil.toStr(response);
	}

	/**
	 * 结果
	 * 
	 * @param result
	 *            结果（true:成功 false:失败）
	 * @param message
	 *            信息（为空：默认信息）
	 * @return 结果（JSON字符串）
	 */
	public static String result(boolean result, String message) {
		if (message == null) {
			if (result) {
				message = "成功！";
			} else {
				message = "失败！";
			}
		}

		Map<String, Object> response = new LinkedHashMap<String, Object>();
		response.put("result", result);
		response.put("message", message);
		return JsonUtil.toStr(response);
	}

	/**
	 * 结果
	 * 
	 * @param result
	 *            结果（true:成功 false:失败）
	 * @param message
	 *            信息（为空：默认信息）
	 * @param data
	 *            数据
	 * @return 结果（JSON字符串）
	 */
	public static String result(boolean result, String message, Object data) {
		if (message == null) {
			if (result) {
				message = "成功！";
			} else {
				message = "失败！";
			}
		}

		Map<String, Object> response = new LinkedHashMap<String, Object>();
		response.put("result", result);
		response.put("message", message);
		response.put("data", data);
		return JsonUtil.toStr(response);
	}

	/**
	 * 操作结果
	 * 
	 * @param result
	 *            结果（true:成功 false:失败）
	 * @param message
	 *            信息（为空：默认信息）
	 * @return 结果（JSON字符串）
	 */
	public static String operateResult(boolean result, String message) {
		if (message == null) {
			if (result) {
				message = "成功！";
			} else {
				message = "失败！";
			}
		}

		Map<String, Object> response = new LinkedHashMap<String, Object>();
		response.put("result", result);
		response.put("message", message);
		return JsonUtil.toStr(response);
	}

}
