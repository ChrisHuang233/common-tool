package com.huangwei.util;

import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.DefaultDefaultValueProcessor;
import net.sf.json.util.CycleDetectionStrategy;
import net.sf.json.util.JSONUtils;

import java.util.*;

/**
 * JSON工具类（Json-lib实现）
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
		// 反序列化 - 日期处理
		final String[] dateFormats = { "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd",
				"yyyy年MM月dd日 HH时mm分ss秒", "yyyy年MM月dd日 HH时mm分", "yyyy年MM月dd日", "yyyyMMddHHmmss", "yyyyMMddHHmm",
				"yyyyMMdd", "HH:mm:ss", "HH:mm" };
		JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpher(dateFormats));
	}

	/**
	 * Java对象 -> JSON字符串（支持自定义对象、map、list等）（使用通用JSON配置）
	 *
	 * @param bean
	 *            Java对象（不能为空）
	 * @return JSON字符串
	 * @throws NullPointerException
	 *             参数为空
	 */
	public static String toString(Object bean) {
		if (bean == null) {
			throw new NullPointerException("参数不能为空！");
		}

		if (bean instanceof Collection || bean.getClass().isArray()) {
			return JSONArray.fromObject(bean, GeneralConfig).toString();
		}
		return JSONObject.fromObject(bean, GeneralConfig).toString();
	}

	/**
	 * Java对象 -> JSON字符串（支持自定义对象、map、list等）<br>
	 * <br>
	 * 使用定制JSON配置：Integer.null/Long.null -> ""（空字符串）
	 *
	 * @param bean
	 *            Java对象（不能为空）
	 * @return JSON字符串
	 * @throws NullPointerException
	 *             参数为空
	 */
	public static String toStr(Object bean) {
		if (bean == null) {
			throw new NullPointerException("参数不能为空！");
		}

		if (bean instanceof Collection || bean.getClass().isArray()) {
			return JSONArray.fromObject(bean, CustomConfig).toString();
		}
		return JSONObject.fromObject(bean, CustomConfig).toString();
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
			throw new IllegalArgumentException("参数不能为空！key:" + key + " value:" + value);
		}

		JSONObject json = new JSONObject();
		json.element(key, value, GeneralConfig);
		return json.toString();
	}

	/**
	 * JSON字符串 -> Map&lt;String, Object&gt;
	 *
	 * @param jsonStr
	 *            JSON字符串
	 * @return LinkedHashMap&lt;String, Object&gt;（不为NULL）
	 */
	public static Map<String, Object> toMap(String jsonStr) {
		Map<String, Object> result = new LinkedHashMap<String, Object>();
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
	 * JSON字符串 -> Map&lt;String, String&gt;
	 *
	 * @param jsonStr
	 *            JSON字符串
	 * @return LinkedHashMap&lt;String, String&gt;（不为NULL）
	 */
	public static Map<String, String> toStringMap(String jsonStr) {
		Map<String, String> result = new LinkedHashMap<String, String>();
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
	 * 属性类型映射<br>
	 * <br>
	 * 注意：转换含有复杂类型属性的Java对象时请使用：JSONObject.toBean(jsonObject, class, classMap)
	 *
	 * @param key
	 *            键/属性名（不能为空）
	 * @param beanClass
	 *            属性类型（不能为空）
	 * @return HashMap&lt;String, Class&lt;?&gt;&gt;
	 * @throws IllegalArgumentException
	 *             参数为空
	 */
	public static Map<String, Class<?>> classMap(String key, Class<?> beanClass) {
		if (key == null || "".equals(key = key.trim()) || beanClass == null) {
			throw new IllegalArgumentException("参数不能为空！key:" + key + " beanClass:" + beanClass);
		}

		Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();
		classMap.put(key, beanClass);
		return classMap;
	}

	/**
	 * JSON字符串 -> Java对象<br>
	 * <br>
	 * 注意：仅适用于属性全部为简单类型的Java对象。
	 *
	 * @param jsonStr
	 *            JSON字符串（不能为空）
	 * @param beanClass
	 *            对象类型（不能为空）
	 * @return Java对象（不为NULL）
	 * @throws IllegalArgumentException
	 *             参数为空
	 */
	@SuppressWarnings("unchecked")
	public static <T> T toBean(String jsonStr, Class<T> beanClass) {
		if (jsonStr == null || "".equals(jsonStr = jsonStr.trim()) || beanClass == null) {
			throw new IllegalArgumentException("参数不能为空！jsonStr:" + jsonStr + " beanClass:" + beanClass);
		}

		return (T) JSONObject.toBean(JSONObject.fromObject(jsonStr), beanClass);
	}

	/**
	 * JSON字符串 -> Java对象<br>
	 * <br>
	 * 注意：请为Java对象的复杂类型属性设置正确的属性类型映射。
	 *
	 * @param jsonStr
	 *            JSON字符串（不能为空）
	 * @param beanClass
	 *            对象类型（不能为空）
	 * @param classMap
	 *            属性类型映射（不能为空）
	 * @return Java对象（不为NULL）
	 * @throws IllegalArgumentException
	 *             参数为空
	 */
	@SuppressWarnings("unchecked")
	public static <T> T toBean(String jsonStr, Class<T> beanClass, Map<String, Class<?>> classMap) {
		if (jsonStr == null || "".equals(jsonStr = jsonStr.trim()) || beanClass == null) {
			throw new IllegalArgumentException("参数不能为空！jsonStr:" + jsonStr + " beanClass:" + beanClass);
		}

		return (T) JSONObject.toBean(JSONObject.fromObject(jsonStr), beanClass, classMap);
	}

}
