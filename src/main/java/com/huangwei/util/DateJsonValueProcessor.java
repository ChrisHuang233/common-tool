package com.huangwei.util;

import java.text.SimpleDateFormat;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

/**
 * JSON工具 - JavaBean日期字段 -> 日期字符串<br>
 * <br>
 * 支持：java.sql.Date, java.sql.Timestamp, java.util.Date<br>
 * <br>
 * 默认格式：java.sql.Date -> 年-月-日(yyyy-MM-dd)<br>
 * java.sql.Timestamp, java.util.Date -> 年-月-日 时:分:秒(yyyy-MM-dd HH:mm:ss)
 */
public class DateJsonValueProcessor implements JsonValueProcessor {

	/* ----------------------- Fields ----------------------- */

	/** 日期格式 - 全（年-月-日 时:分:秒(yyyy-MM-dd HH:mm:ss)） */
	private static final String PTN_FULL = "yyyy-MM-dd HH:mm:ss";
	/** 日期格式 - 半（年-月-日(yyyy-MM-dd)） */
	private static final String PTN_HALF = "yyyy-MM-dd";
	/** 日期格式 - 指定值 */
	private String datePattern;

	/* -------------------- Constructors -------------------- */

	/**
	 * JavaBean日期字段 -> 日期字符串<br>
	 * <br>
	 * 默认格式：java.sql.Date -> 年-月-日(yyyy-MM-dd)<br>
	 * java.sql.Timestamp, java.util.Date -> 年-月-日 时:分:秒(yyyy-MM-dd HH:mm:ss)
	 */
	public DateJsonValueProcessor() {
		super();
	}

	/**
	 * JavaBean日期字段 -> 日期字符串
	 * 
	 * @param datePattern
	 *            日期格式（为空：默认格式）
	 */
	public DateJsonValueProcessor(String datePattern) {
		super();

		this.datePattern = (datePattern == null || "".equals(datePattern.trim())) ? null : datePattern;
	}

	/* ----------------------- Methods ----------------------- */

	@Override
	public Object processArrayValue(Object value, JsonConfig jsonConfig) {
		return process(value);
	}

	@Override
	public Object processObjectValue(String key, Object value, JsonConfig jsonConfig) {
		return process(value);
	}

	/** 转换 */
	private Object process(Object value) {
		if (value == null) {
			return "";
		}

		if (datePattern != null) {
			return new SimpleDateFormat(datePattern).format((java.util.Date) value);
		}

		// 注意：在判断父子级类型时要先判断子类型再判断父类型
		if (value instanceof java.sql.Date) {
			return new SimpleDateFormat(PTN_HALF).format((java.util.Date) value);
		}
		if (value instanceof java.sql.Timestamp || value instanceof java.util.Date) {
			return new SimpleDateFormat(PTN_FULL).format((java.util.Date) value);
		}
		return value.toString();
	}

}
