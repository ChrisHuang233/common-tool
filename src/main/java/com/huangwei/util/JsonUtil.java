package com.huangwei.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.DefaultDefaultValueProcessor;
import net.sf.json.util.CycleDetectionStrategy;

/**
 * JSON工具类
 */
public class JsonUtil {

	/** JSON配置 - 通用 */
	public static final JsonConfig GeneralConfig;
	/** JSON配置 - 定制 */
	public static final JsonConfig CustomConfig;

	static {
		GeneralConfig = new JsonConfig();
		GeneralConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		GeneralConfig.registerJsonValueProcessor(java.util.Date.class, new DateJsonValueProcessor());
		GeneralConfig.registerJsonValueProcessor(java.sql.Date.class, new DateJsonValueProcessor());
		GeneralConfig.registerJsonValueProcessor(java.sql.Timestamp.class, new DateJsonValueProcessor());

		CustomConfig = new JsonConfig();
		CustomConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		CustomConfig.registerJsonValueProcessor(java.util.Date.class, new DateJsonValueProcessor());
		CustomConfig.registerJsonValueProcessor(java.sql.Date.class, new DateJsonValueProcessor());
		CustomConfig.registerJsonValueProcessor(java.sql.Timestamp.class, new DateJsonValueProcessor());
		CustomConfig.registerDefaultValueProcessor(Integer.class, new DefaultDefaultValueProcessor() {
			@Override
			public Object getDefaultValue(@SuppressWarnings("rawtypes") Class type) {
				return "";
			}
		});
		CustomConfig.registerDefaultValueProcessor(Long.class, new DefaultDefaultValueProcessor() {
			@Override
			public Object getDefaultValue(@SuppressWarnings("rawtypes") Class type) {
				return "";
			}
		});
	}

	/**
	 * 对象 -> JSON字符串（支持自定义对象、map、list等）（使用通用JSON配置）
	 * 
	 * @param obj
	 *            对象
	 * @return JSON字符串
	 * @throws NullPointerException
	 *             对象为空
	 */
	public static String toString(Object obj) {
		if (obj == null) {
			throw new NullPointerException("对象不能为空！");
		}

		if (obj instanceof Collection || obj.getClass().isArray()) {
			return JSONArray.fromObject(obj, GeneralConfig).toString();
		}
		return JSONObject.fromObject(obj, GeneralConfig).toString();
	}

	/**
	 * 对象 -> JSON字符串（支持自定义对象、map、list等）<br>
	 * <br>
	 * 使用定制JSON配置：Integer.null/Long.null -> ""（空字符串）
	 * 
	 * @param obj
	 *            对象
	 * @return JSON字符串
	 * @throws NullPointerException
	 *             对象为空
	 */
	public static String toStr(Object obj) {
		if (obj == null) {
			throw new NullPointerException("对象不能为空！");
		}

		if (obj instanceof Collection || obj.getClass().isArray()) {
			return JSONArray.fromObject(obj, CustomConfig).toString();
		}
		return JSONObject.fromObject(obj, CustomConfig).toString();
	}

	/**
	 * 单一值（使用通用JSON配置）
	 * 
	 * @param key
	 *            键（不能为空）
	 * @param value
	 *            值（不能为空）
	 * @return JSON字符串
	 * @throws IllegalArgumentException
	 *             参数为空
	 */
	public static String single(String key, Object value) {
		if (key == null || "".equals(key = key.trim()) || value == null) {
			throw new IllegalArgumentException("参数错误！key:" + key + " value:" + value);
		}

		JSONObject json = new JSONObject();
		json.element(key, value, GeneralConfig);
		return json.toString();
	}

	/**
	 * JSON字符串 -> HashMap&lt;String, Object&gt;
	 * 
	 * @param jsonStr
	 *            JSON字符串
	 * @return HashMap&lt;String, Object&gt;（不为NULL）
	 */
	public static Map<String, Object> toMap(String jsonStr) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (jsonStr == null || "".equals(jsonStr = jsonStr.trim())) {
			return result;
		}

		JSONObject json = JSONObject.fromObject(jsonStr);
		@SuppressWarnings("unchecked")
		Iterator<String> iterator = json.keys();
		String key;
		Object value;
		while (iterator.hasNext()) {
			key = iterator.next();
			value = json.get(key);
			if (value == null || value instanceof JSONNull) {
				result.put(key, null);
			} else {
				result.put(key, value);
			}
		}
		return result;
	}

	/**
	 * JSON字符串 -> HashMap&lt;String, String&gt;
	 * 
	 * @param jsonStr
	 *            JSON字符串
	 * @return HashMap&lt;String, String&gt;（不为NULL）
	 */
	public static Map<String, String> toStringMap(String jsonStr) {
		Map<String, String> result = new HashMap<String, String>();
		if (jsonStr == null || "".equals(jsonStr = jsonStr.trim())) {
			return result;
		}

		JSONObject json = JSONObject.fromObject(jsonStr);
		@SuppressWarnings("unchecked")
		Iterator<String> iterator = json.keys();
		String key, value;
		while (iterator.hasNext()) {
			key = iterator.next();
			value = json.getString(key);
			if (value == null || "null".equals(value)) {
				result.put(key, null);
			} else {
				result.put(key, value);
			}
		}
		return result;
	}

	/**
	 * 类型映射<br>
	 * <br>
	 * 实体/Bean含有复杂类型属性时使用：JSONObject.toBean(jsonObject, class, classMap)
	 * 
	 * @param key
	 *            键/属性名（不能为空）
	 * @param clazz
	 *            属性类型（不能为空）
	 * @return HashMap&lt;String, Class&lt;?&gt;&gt;
	 * @throws IllegalArgumentException
	 *             参数为空
	 */
	public static Map<String, Class<?>> classMap(String key, Class<?> clazz) {
		if (key == null || "".equals(key = key.trim()) || clazz == null) {
			throw new IllegalArgumentException("参数错误！key:" + key + " clazz:" + clazz);
		}

		Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();
		classMap.put(key, clazz);
		return classMap;
	}

}
