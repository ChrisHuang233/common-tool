package com.huangwei.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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

	/** 默认日期格式 */
	private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
	/** 日期格式化 */
	private DateFormat dateFormat;

	/**
	 * JavaBean日期字段 -> 日期字符串（默认格式“yyyy-MM-dd HH:mm:ss”）
	 */
	public DateJsonValueProcessor() {
		super();

		dateFormat = new SimpleDateFormat(DEFAULT_DATE_PATTERN);
	}

	/**
	 * JavaBean日期字段 -> 日期字符串
	 *
	 * @param datePattern
	 *            日期格式（为空：默认格式“yyyy-MM-dd HH:mm:ss”）
	 */
	public DateJsonValueProcessor(String datePattern) {
		super();

		if (datePattern == null || (datePattern = datePattern.trim()).isEmpty()) {
			datePattern = DEFAULT_DATE_PATTERN;
		}
		try {
			dateFormat = new SimpleDateFormat(datePattern);
		} catch (Exception e) {
			dateFormat = new SimpleDateFormat(DEFAULT_DATE_PATTERN);
		}
	}

	/** 转换 */
	private Object process(Object value) {
		return (!(value instanceof Date)) ? "" : dateFormat.format((Date) value);
	}

	@Override
	public Object processArrayValue(Object value, JsonConfig jsonConfig) {
		return process(value);
	}

	@Override
	public Object processObjectValue(String key, Object value, JsonConfig jsonConfig) {
		return process(value);
	}

}
