package com.huangwei.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * JSON工具类（Jackson实现）
 */
public class JacksonUtil {
	private static Logger logger = LoggerFactory.getLogger(JacksonUtil.class);

	private static ObjectMapper objectMapper = null;

	/** 初始化 */
	static {
		objectMapper = new ObjectMapper();
//		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
//				.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
		// 去掉默认的时间戳格式
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		// 时区设置为中国
		objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		// 空值不序列化
		objectMapper.setSerializationInclusion(Include.ALWAYS);
		// 反序列化时，属性不存在的兼容处理
		objectMapper.getDeserializationConfig().withoutFeatures(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		// 序列化时，日期的统一格式
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// 单引号处理
		objectMapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

//		objectMapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
//			@Override
//			public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
//				gen.writeString("");
//			}
//		});

//		// 美化输出
//		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
//		// 允许序列化空的POJO类（否则会抛出异常）
//		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
//		// 把java.util.Date, Calendar输出为数字（时间戳）
//		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//
//		// 在遇到未知属性的时候不抛出异常
//		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
//		// 强制JSON 空字符串("")转换为null对象值:
//		objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
//
//		// 在JSON中允许C/C++ 样式的注释(非标准，默认禁用)
//		objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
//		// 允许没有引号的字段名（非标准）
//		objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
//		// 允许单引号（非标准）
//		objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
//		// 强制转义非ASCII字符
//		objectMapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
//		// 将内容包裹为一个JSON属性，属性名由@JsonRootName注解指定
//		objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
	}

	/**
	 * Java对象 -> JSON字符串（支持自定义对象、map、list等）
	 *
	 * @param bean
	 *            Java对象（不能为空）
	 * @return NULL(参数为空/转换异常) 或 JSON字符串
	 */
	public static String toString(Object bean) {
		if (bean == null) {
			logger.error("[Java对象 -> JSON字符串]参数错误！bean:" + bean);
			return null;
		}

		try {
			return objectMapper.writeValueAsString(bean);
		} catch (Exception e) {
			logger.error("[Java对象 -> JSON字符串]出错！beanClass:" + bean.getClass(), e);
			return null;
		}
	}

	/**
	 * JSON字符串 -> Java对象
	 * 
	 * @param jsonStr
	 *            JSON字符串（不能为空）
	 * @param beanClass
	 *            对象类型（不能为空）
	 * @return NULL(参数为空/转换异常) 或 Java对象
	 */
	public static <T> T toBean(String jsonStr, Class<T> beanClass) {
		if (jsonStr == null || beanClass == null) {
			logger.error("[JSON字符串 -> Java对象]参数错误！jsonStr:" + jsonStr + " beanClass:" + beanClass);
			return null;
		}

		try {
			return objectMapper.readValue(jsonStr, beanClass);
		} catch (Exception e) {
			logger.error("[JSON字符串 -> Java对象]出错！jsonStr:" + jsonStr + " beanClass:" + beanClass, e);
			return null;
		}
	}

	/**
	 * JSON字符串 -> Map&lt;String, Object&gt;
	 * 
	 * @param jsonStr
	 *            JSON字符串（不能为空）
	 * @return LinkedHashMap&lt;String, Object&gt;（不为NULL）
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> toMap(String jsonStr) {
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		if (jsonStr == null || "".equals(jsonStr = jsonStr.trim())) {
			return result;
		}

		try {
			return objectMapper.readValue(jsonStr, Map.class);
		} catch (Exception e) {
			logger.error("[JSON字符串 -> Map]出错！jsonStr:" + jsonStr, e);
			return result;
		}
	}

}
