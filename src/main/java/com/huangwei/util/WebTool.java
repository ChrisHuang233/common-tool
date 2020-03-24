package com.huangwei.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;


/**
 * Web页面相关工具
 */
@Slf4j
public class WebTool {

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
			log.error("[单一查询条件]参数错误！key:" + key + " value:" + value);
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
			log.error("[简单结果]参数错误！key:" + key + " value:" + value);
			return null;
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put(key, value);
		return result;
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
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", true);
		result.put("iDisplayStart", offset == null ? 0 : offset);// 偏移量（从0开始）
		result.put("iTotalRecords", total);// 总记录数
		result.put("iTotalDisplayRecords", total);// (过滤后)显示的记录数
		result.put("aaData", list == null ? Collections.EMPTY_LIST : list);// 结果集
		return JsonUtil.toStr(result);
	}

	/**
	 * 分页失败 -> DataTables数据（JSON字符串）
	 * 
	 * @param message
	 *            信息
	 * @return 结果（JSON字符串）
	 */
	public static String pageFault(String message) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", false);
		result.put("message", message == null ? "查询失败！" : message);
		result.put("iDisplayStart", 0);// 偏移量（从0开始）
		result.put("iTotalRecords", 0);// 总记录数
		result.put("iTotalDisplayRecords", 0);// (过滤后)显示的记录数
		result.put("aaData", Collections.EMPTY_LIST);// 结果集
		return JsonUtil.toStr(result);
	}

	/**
	 * 查询结果
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
				message = "查询成功！";
			} else {
				message = "查询失败！";
			}
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("result", result);
		map.put("message", message);
		map.put("data", data);
		return JsonUtil.toStr(map);
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
				message = "操作成功！";
			} else {
				message = "操作失败！";
			}
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("result", result);
		map.put("message", message);
		return JsonUtil.toStr(map);
	}

}
