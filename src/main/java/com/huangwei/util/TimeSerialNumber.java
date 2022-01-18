package com.huangwei.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 时基序列号<br>
 * <br>
 * 格式：前缀 + (指定格式)当前时间 + (左侧补零)N位递增序号
 */
public class TimeSerialNumber {

	/** 日期格式 - 年月日（yyyyMMdd） */
	public static final String P_YMD = "yyyyMMdd";
	/** 日期格式 - 年月日时（yyyyMMddHH） */
	public static final String P_YMDH = "yyyyMMddHH";
	/** 日期格式 - 年月日时分（yyyyMMddHHmm） */
	public static final String P_YMDHM = "yyyyMMddHHmm";
	/** 日期格式 - 年月日时分秒（yyyyMMddHHmmss） */
	public static final String P_YMDHMS = "yyyyMMddHHmmss";
	/** 日期格式 - 年月日时分秒毫秒（yyyyMMddHHmmssSSS） */
	public static final String P_YMDHMSS = "yyyyMMddHHmmssSSS";

	/** 前缀 */
	private String prefix;
	/** 日期格式 */
	private String pattern;
	/** 递增最大值 */
	private long max;
	/** 填补格式 */
	private String format;
	/** 种子 */
	private AtomicLong seed;

	/**
	 * 时基序列号<br>
	 * <br>
	 * 说明：递增部分从1开始。
	 * 
	 * @param pattern
	 *            日期格式（不能为空）
	 * @param length
	 *            (递增部分)长度（>=1）
	 */
	private TimeSerialNumber(String pattern, int length) {
		this(null, pattern, length, 1L);
	}

	/**
	 * 时基序列号
	 * 
	 * @param prefix
	 *            前缀
	 * @param pattern
	 *            日期格式（不能为空）
	 * @param length
	 *            (递增部分)长度（>=1）
	 * @param initial
	 *            (递增部分)初始值（>=1）
	 */
	private TimeSerialNumber(String prefix, String pattern, int length, long initial) {
		super();
		if (pattern == null || "".equals(pattern = pattern.trim())) {
			throw new IllegalArgumentException("The parameter 'pattern' cannot be null or blank!");
		}
		final int l = length < 1 ? 1 : length;
		final long i = initial < 1L ? 1L : initial;

		this.prefix = prefix == null ? null : prefix.trim();
		this.pattern = pattern;
		this.max = ((long) Math.pow(10, l)) - 1;
		this.format = "%0" + l + "d";
		this.seed = new AtomicLong(i);
	}

	/**
	 * 时基序列号样例<br>
	 * <br>
	 * 格式：年月日时分秒(yyyyMMddHHmmss) + 6位递增序号，共20位。<br>
	 * 说明：递增部分从1开始。
	 * 
	 * @return 时基序列号对象（非NULL）
	 */
	public static TimeSerialNumber sample() {
		return new TimeSerialNumber(P_YMDHMS, 6);
	}

	/**
	 * 时基序列号样例<br>
	 * <br>
	 * 格式：年月日时分秒(yyyyMMddHHmmss) + 6位递增序号，共20位。
	 * 
	 * @param initial
	 *            (递增部分)初始值（>=1）
	 * @return 时基序列号对象（非NULL）
	 */
	public static TimeSerialNumber sample(long initial) {
		return new TimeSerialNumber(null, P_YMDHMS, 6, initial);
	}

	/**
	 * 时基序列号样例<br>
	 * <br>
	 * 格式：M位前缀 + 年月日时分秒(yyyyMMddHHmmss) + 6位递增序号，共M+20位。<br>
	 * 说明：递增部分从1开始。
	 * 
	 * @param prefix
	 *            前缀
	 * @return 时基序列号对象（非NULL）
	 */
	public static TimeSerialNumber sample(String prefix) {
		return new TimeSerialNumber(prefix, P_YMDHMS, 6, 1L);
	}

	/**
	 * 时基序列号样例<br>
	 * <br>
	 * 格式：M位前缀 + 年月日时分秒(yyyyMMddHHmmss) + 6位递增序号，共M+20位。
	 * 
	 * @param prefix
	 *            前缀
	 * @param initial
	 *            (递增部分)初始值（>=1）
	 * @return 时基序列号对象（非NULL）
	 */
	public static TimeSerialNumber sample(String prefix, long initial) {
		return new TimeSerialNumber(prefix, P_YMDHMS, 6, initial);
	}

	/**
	 * 时基序列号实例<br>
	 * <br>
	 * 说明：递增部分从1开始。
	 * 
	 * @param prefix
	 *            前缀
	 * @param pattern
	 *            日期格式（不能为空）
	 * @param length
	 *            (递增部分)长度（>=1）
	 * @return 时基序列号对象（非NULL）
	 */
	public static TimeSerialNumber instance(String prefix, String pattern, int length) {
		return new TimeSerialNumber(prefix, pattern, length, 1L);
	}

	/**
	 * 时基序列号实例
	 * 
	 * @param prefix
	 *            前缀
	 * @param pattern
	 *            日期格式（不能为空）
	 * @param length
	 *            (递增部分)长度（>=1）
	 * @param initial
	 *            (递增部分)初始值（>=1）
	 * @return 时基序列号对象（非NULL）
	 */
	public static TimeSerialNumber instance(String prefix, String pattern, int length, long initial) {
		return new TimeSerialNumber(prefix, pattern, length, initial);
	}

	/**
	 * 生成序列号
	 * 
	 * @return 序列号
	 */
	public String sn() {
		long current, next;
		for (;;) {
			current = seed.get();
			next = (current >= max ? 0 : current) + 1;
			if (seed.compareAndSet(current, next))
				break;
		}

		StringBuilder sb = new StringBuilder();
		if (prefix != null && !prefix.isEmpty()) {
			sb.append(prefix);
		}
		sb.append(new SimpleDateFormat(pattern).format(new Date()));
		sb.append(String.format(format, current));
		return sb.toString();
	}

}
