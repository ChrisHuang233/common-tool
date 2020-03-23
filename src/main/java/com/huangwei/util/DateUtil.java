package com.huangwei.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 */
public class DateUtil {

	/* ------------------------ 日期格式 ------------------------ */

	/** 日期格式 - 年-月-日（yyyy-MM-dd） */
	public static final String PTN_Y_M_D = "yyyy-MM-dd";
	/** 日期格式 - 年月日（yyyyMMdd） */
	public static final String PTN_YMD = "yyyyMMdd";
	/** 日期格式 - 年-月（yyyy-MM） */
	public static final String PTN_Y_M = "yyyy-MM";
	/** 日期格式 - 年月（yyyyMM） */
	public static final String PTN_YM = "yyyyMM";
	/** 日期格式 - 月-日（MM-dd） */
	public static final String PTN_M_D = "MM-dd";
	/** 日期格式 - 月日（MMdd） */
	public static final String PTN_MD = "MMdd";
	/** 日期格式 - 时:分:秒（HH:mm:ss） */
	public static final String PTN_H_M_S = "HH:mm:ss";
	/** 日期格式 - 时分秒（HHmmss） */
	public static final String PTN_HMS = "HHmmss";
	/** 日期格式 - 时:分（HH:mm） */
	public static final String PTN_H_M = "HH:mm";
	/** 日期格式 - 时分（HHmm） */
	public static final String PTN_HM = "HHmm";
	/** 日期格式 - 时:分（kk:mm） */
	public static final String PTN_K_M = "kk:mm";
	/** 日期格式 - 上午/下午 时:分（aaa HH:mm） */
	public static final String PTN_AAA_H_M = "aaa HH:mm";
	/** 日期格式 - 年-月-日 时:分:秒（yyyy-MM-dd HH:mm:ss） */
	public static final String PTN_Y_M_D_H_M_S = "yyyy-MM-dd HH:mm:ss";
	/** 日期格式 - 年-月-日 时:分:秒（yyyy-MM-dd kk:mm:ss） */
	public static final String PTN_Y_M_D_K_M_S = "yyyy-MM-dd kk:mm:ss";
	/** 日期格式 - 年-月-日 时:分:秒.毫秒（yyyy-MM-dd HH:mm:ss.SSS） */
	public static final String PTN_Y_M_D_H_M_S_S = "yyyy-MM-dd HH:mm:ss.SSS";
	/** 日期格式 - 年月日时分秒（yyyyMMddHHmmss） */
	public static final String PTN_YMDHMS = "yyyyMMddHHmmss";
	/** 日期格式 - 年月日时分秒毫秒（yyyyMMddHHmmssSSS） */
	public static final String PTN_YMDHMSS = "yyyyMMddHHmmssSSS";
	/** 日期格式 - 年-月-日 时:分（yyyy-MM-dd HH:mm） */
	public static final String PTN_Y_M_D_H_M = "yyyy-MM-dd HH:mm";
	/** 日期格式 - 年月日时分（yyyyMMddHHmm） */
	public static final String PTN_YMDHM = "yyyy-MM-dd HH:mm";

	/* ------------------------ 毫秒值 ------------------------ */

	/** 毫秒值 - 一秒 */
	public static final long ONE_SECOND = 1000L;
	/** 毫秒值 - 三秒 */
	public static final long THREE_SECOND = ONE_SECOND * 3L;
	/** 毫秒值 - 五秒 */
	public static final long FIVE_SECOND = ONE_SECOND * 5L;
	/** 毫秒值 - 十秒 */
	public static final long TEN_SECOND = ONE_SECOND * 10L;
	/** 毫秒值 - 十五秒 */
	public static final long FIFTEEN_SECOND = ONE_SECOND * 15L;
	/** 毫秒值 - 半分钟 */
	public static final long HALF_MINUTE = ONE_SECOND * 30L;
	/** 毫秒值 - 一分钟 */
	public static final long ONE_MINUTE = ONE_SECOND * 60L;
	/** 毫秒值 - 三分钟 */
	public static final long THREE_MINUTE = ONE_MINUTE * 3L;
	/** 毫秒值 - 五分钟 */
	public static final long FIVE_MINUTE = ONE_MINUTE * 5L;
	/** 毫秒值 - 十分钟 */
	public static final long TEN_MINUTE = ONE_MINUTE * 10L;
	/** 毫秒值 - 十五分钟 */
	public static final long FIFTEEN_MINUTE = ONE_MINUTE * 15L;
	/** 毫秒值 - 半小时（30分钟） */
	public static final long HALF_HOUR = ONE_MINUTE * 30L;
	/** 毫秒值 - 一小时（60分钟） */
	public static final long ONE_HOUR = ONE_MINUTE * 60L;
	/** 毫秒值 - 半天（12小时） */
	public static final long HALF_DAY = ONE_HOUR * 12L;
	/** 毫秒值 - 一天（24小时） */
	public static final long ONE_DAY = ONE_HOUR * 24L;
	/** 毫秒值 - 一周（7天） */
	public static final long ONE_WEEK = ONE_DAY * 7L;
	/** 毫秒值 - 一年（365天） */
	public static final long ONE_YEAR = ONE_DAY * 365L;

	/* ----------------------- Methods ----------------------- */

	/**
	 * 当前时间（精度：毫秒）
	 * 
	 * @return 当前时间
	 */
	public static Date time() {
		return new Date();
	}

	/**
	 * 当前时间（精度：秒）/舍去毫秒
	 * 
	 * @return 时间
	 */
	public static Date timeOnSecond() {
		return timeOnSecond(new Date());
	}

	/**
	 * 时间取整（精度：秒）/舍去毫秒
	 * 
	 * @param date
	 *            日期（不能为空）
	 * @return 新日期
	 * @throws IllegalArgumentException
	 *             日期为空
	 */
	public static Date timeOnSecond(Date date) {
		if (date == null) {
			throw new IllegalArgumentException("日期不能为空！");
		}

		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

	/**
	 * 当前时间（精度：分）/当前分钟零秒
	 * 
	 * @return 时间
	 */
	public static Date timeOnMinute() {
		return timeOnMinute(new Date());
	}

	/**
	 * 时间取整（精度：分）/当前分钟零秒
	 * 
	 * @param date
	 *            日期（不能为空）
	 * @return 新日期
	 * @throws IllegalArgumentException
	 *             日期为空
	 */
	public static Date timeOnMinute(Date date) {
		if (date == null) {
			throw new IllegalArgumentException("日期不能为空！");
		}

		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

	/**
	 * 当前时间（精度：小时）/当前小时零分
	 * 
	 * @return 时间
	 */
	public static Date timeOnHour() {
		return timeOnHour(new Date());
	}

	/**
	 * 时间取整（精度：小时）/当前小时零分
	 * 
	 * @param date
	 *            日期（不能为空）
	 * @return 新日期
	 * @throws IllegalArgumentException
	 *             日期为空
	 */
	public static Date timeOnHour(Date date) {
		if (date == null) {
			throw new IllegalArgumentException("日期不能为空！");
		}

		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

	/**
	 * 当前时间（精度：天）/当天零点
	 * 
	 * @return 时间
	 */
	public static Date timeOnDay() {
		return timeOnDay(new Date());
	}

	/**
	 * 时间取整（精度：天）/当天零点
	 * 
	 * @param date
	 *            日期（不能为空）
	 * @return 新日期
	 * @throws IllegalArgumentException
	 *             日期为空
	 */
	public static Date timeOnDay(Date date) {
		if (date == null) {
			throw new IllegalArgumentException("日期不能为空！");
		}

		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

	/**
	 * （当前时间）月初
	 * 
	 * @return 时间
	 */
	public static Date monthBegin() {
		return monthBegin(new Date());
	}

	/**
	 * 月初
	 * 
	 * @param date
	 *            日期（不能为空）
	 * @return 新日期
	 * @throws IllegalArgumentException
	 *             日期为空
	 */
	public static Date monthBegin(Date date) {
		if (date == null) {
			throw new IllegalArgumentException("日期不能为空！");
		}

		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DATE, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

	/**
	 * （当前时间）月末
	 * 
	 * @return 时间
	 */
	public static Date monthEnd() {
		return monthEnd(new Date());
	}

	/**
	 * 月末
	 * 
	 * @param date
	 *            日期（不能为空）
	 * @return 新日期
	 * @throws IllegalArgumentException
	 *             日期为空
	 */
	public static Date monthEnd(Date date) {
		if (date == null) {
			throw new IllegalArgumentException("日期不能为空！");
		}

		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DATE, 1);// 当月第一天
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		c.add(Calendar.MONTH, 1);// 下个月
		c.add(Calendar.DATE, -1);// 前一天
		return c.getTime();
	}

	/**
	 * （当前时间）一个月后
	 * 
	 * @return 时间
	 */
	public static Date nextMonth() {
		return nextMonth(new Date());
	}

	/**
	 * 一个月后
	 * 
	 * @param date
	 *            日期（不能为空）
	 * @return 新日期
	 * @throws IllegalArgumentException
	 *             日期为空
	 */
	public static Date nextMonth(Date date) {
		if (date == null) {
			throw new IllegalArgumentException("日期不能为空！");
		}

		return calculateTime(date, Calendar.MONTH, 1);
	}

	/**
	 * （当前时间）下月初
	 * 
	 * @return 时间
	 */
	public static Date nextMonthBegin() {
		return nextMonthBegin(new Date());
	}

	/**
	 * 下月初
	 * 
	 * @param date
	 *            日期（不能为空）
	 * @return 新日期
	 * @throws IllegalArgumentException
	 *             日期为空
	 */
	public static Date nextMonthBegin(Date date) {
		if (date == null) {
			throw new IllegalArgumentException("日期不能为空！");
		}

		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DATE, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		c.add(Calendar.MONTH, 1);// 下个月
		return c.getTime();
	}

	/**
	 * 当前时间（年-月-日 时:分:秒(yyyy-MM-dd HH:mm:ss)）
	 * 
	 * @return 当前时间
	 */
	public static String now() {
		return new SimpleDateFormat(PTN_Y_M_D_H_M_S).format(new Date());
	}

	/**
	 * 当前时间 - 指定格式
	 * 
	 * @param pattern
	 *            日期格式（为空：年-月-日 时:分:秒(yyyy-MM-dd HH:mm:ss)）
	 * @return 当前时间
	 */
	public static String now(final String pattern) {
		if (pattern == null || "".equals(pattern.trim())) {
			return new SimpleDateFormat(PTN_Y_M_D_H_M_S).format(new Date());
		}

		return new SimpleDateFormat(pattern).format(new Date());
	}

	/**
	 * 日期格式化（年-月-日 时:分:秒(yyyy-MM-dd HH:mm:ss)）
	 * 
	 * @param date
	 *            日期（Date对象 或 基准毫秒值(自1970年1月1日0点0分0秒以来的毫秒数)）
	 * @return 空字符串（日期为空） 或 日期字符串
	 */
	public static String format(Object date) {
		if (date == null) {
			return "";
		}

		return new SimpleDateFormat(PTN_Y_M_D_H_M_S).format(date);
	}

	/**
	 * 日期格式化 - 指定格式
	 * 
	 * @param date
	 *            日期（Date对象 或 基准毫秒值(自1970年1月1日0点0分0秒以来的毫秒数)）
	 * @param pattern
	 *            日期格式（为空：年-月-日 时:分:秒(yyyy-MM-dd HH:mm:ss)）
	 * @return 空字符串（日期为空） 或 日期字符串
	 */
	public static String format(Object date, final String pattern) {
		if (date == null) {
			return "";
		}

		if (pattern == null || "".equals(pattern.trim())) {
			return new SimpleDateFormat(PTN_Y_M_D_H_M_S).format(date);
		}

		return new SimpleDateFormat(pattern).format(date);
	}

	/**
	 * (固定格式)日期字符串 -> 日期
	 * 
	 * @param dateStr
	 *            日期字符串（不能为空）（格式：年-月-日 时:分:秒(yyyy-MM-dd HH:mm:ss)）
	 * @return 日期
	 * @throws IllegalArgumentException
	 *             日期字符串为空
	 * @throws ParseException
	 *             格式不匹配
	 */
	public static Date parse(final String dateStr) throws ParseException {
		if (dateStr == null || "".equals(dateStr.trim())) {
			throw new IllegalArgumentException("日期字符串不能为空！");
		}

		return new SimpleDateFormat(PTN_Y_M_D_H_M_S).parse(dateStr);
	}

	/**
	 * (指定格式)日期字符串 -> 日期
	 * 
	 * @param dateStr
	 *            日期字符串（不能为空）
	 * @param pattern
	 *            日期格式（为空：年-月-日 时:分:秒(yyyy-MM-dd HH:mm:ss)）
	 * @return 日期
	 * @throws IllegalArgumentException
	 *             日期字符串为空
	 * @throws ParseException
	 *             格式不匹配
	 */
	public static Date parse(final String dateStr, final String pattern) throws ParseException {
		if (dateStr == null || "".equals(dateStr.trim())) {
			throw new IllegalArgumentException("日期字符串不能为空！");
		}

		if (pattern == null || "".equals(pattern.trim())) {
			return new SimpleDateFormat(PTN_Y_M_D_H_M_S).parse(dateStr);
		}

		return new SimpleDateFormat(pattern).parse(dateStr);
	}

	/**
	 * 时间计算
	 * 
	 * @param date
	 *            日期（为空：当前时间）
	 * @param field
	 *            时间字段（借鉴 java.util.Calendar 的时间字段）
	 * @param amount
	 *            增加（正数）/减小（负数）的数量（为零：返回传入的日期或当前时间）
	 * @return 新日期
	 * @throws IllegalArgumentException
	 *             时间字段超出范围
	 */
	public static Date calculateTime(Date date, int field, int amount) {
		if (field < 0 || field >= Calendar.FIELD_COUNT) {
			throw new IllegalArgumentException("时间字段超出范围（0 - " + (Calendar.FIELD_COUNT - 1) + "）！");
		}
		if (amount == 0) {
			return date == null ? new Date() : date;
		}

		Calendar c = Calendar.getInstance();
		if (date != null) {
			c.setTime(date);
		}
		c.add(field, amount);
		return c.getTime();
	}

	/**
	 * 时间计算 - 年<br>
	 * 
	 * 以当前时间为基础
	 * 
	 * @param amount
	 *            增加（正数）/减小（负数）的数量
	 * @return 新日期
	 */
	public static Date addYear(int amount) {
		if (amount == 0) {
			return new Date();
		}

		return calculateTime(null, Calendar.YEAR, amount);
	}

	/**
	 * 时间计算 - 年
	 * 
	 * @param date
	 *            日期（不能为空）
	 * @param amount
	 *            增加（正数）/减小（负数）的数量
	 * @return 新日期
	 * @throws IllegalArgumentException
	 *             日期为空
	 */
	public static Date addYear(Date date, int amount) {
		if (date == null) {
			throw new IllegalArgumentException("日期不能为空！");
		}
		if (amount == 0) {
			return date;
		}

		return calculateTime(date, Calendar.YEAR, amount);
	}

	/**
	 * 时间计算 - 月<br>
	 * 
	 * 以当前时间为基础
	 * 
	 * @param amount
	 *            增加（正数）/减小（负数）的数量
	 * @return 新日期
	 */
	public static Date addMonth(int amount) {
		if (amount == 0) {
			return new Date();
		}

		return calculateTime(null, Calendar.MONTH, amount);
	}

	/**
	 * 时间计算 - 月
	 * 
	 * @param date
	 *            日期（不能为空）
	 * @param amount
	 *            增加（正数）/减小（负数）的数量
	 * @return 新日期
	 * @throws IllegalArgumentException
	 *             日期为空
	 */
	public static Date addMonth(Date date, int amount) {
		if (date == null) {
			throw new IllegalArgumentException("日期不能为空！");
		}
		if (amount == 0) {
			return date;
		}

		return calculateTime(date, Calendar.MONTH, amount);
	}

	/**
	 * 时间计算 - 日<br>
	 * 
	 * 以当前时间为基础
	 * 
	 * @param amount
	 *            增加（正数）/减小（负数）的数量
	 * @return 新日期
	 */
	public static Date addDay(int amount) {
		if (amount == 0) {
			return new Date();
		}

		return calculateTime(null, Calendar.DATE, amount);
	}

	/**
	 * 时间计算 - 日
	 * 
	 * @param date
	 *            日期（不能为空）
	 * @param amount
	 *            增加（正数）/减小（负数）的数量
	 * @return 新日期
	 * @throws IllegalArgumentException
	 *             日期为空
	 */
	public static Date addDay(Date date, int amount) {
		if (date == null) {
			throw new IllegalArgumentException("日期不能为空！");
		}
		if (amount == 0) {
			return date;
		}

		return calculateTime(date, Calendar.DATE, amount);
	}

	/**
	 * 时间计算 - 小时<br>
	 * 
	 * 以当前时间为基础
	 * 
	 * @param amount
	 *            增加（正数）/减小（负数）的数量
	 * @return 新日期
	 */
	public static Date addHour(int amount) {
		if (amount == 0) {
			return new Date();
		}

		return calculateTime(null, Calendar.HOUR, amount);
	}

	/**
	 * 时间计算 - 小时
	 * 
	 * @param date
	 *            日期（不能为空）
	 * @param amount
	 *            增加（正数）/减小（负数）的数量
	 * @return 新日期
	 * @throws IllegalArgumentException
	 *             日期为空
	 */
	public static Date addHour(Date date, int amount) {
		if (date == null) {
			throw new IllegalArgumentException("日期不能为空！");
		}
		if (amount == 0) {
			return date;
		}

		return calculateTime(date, Calendar.HOUR, amount);
	}

	/**
	 * 时间计算 - 分钟<br>
	 * 
	 * 以当前时间为基础
	 * 
	 * @param amount
	 *            增加（正数）/减小（负数）的数量
	 * @return 新日期
	 */
	public static Date addMinute(int amount) {
		if (amount == 0) {
			return new Date();
		}

		return calculateTime(null, Calendar.MINUTE, amount);
	}

	/**
	 * 时间计算 - 分钟
	 * 
	 * @param date
	 *            日期（不能为空）
	 * @param amount
	 *            增加（正数）/减小（负数）的数量
	 * @return 新日期
	 * @throws IllegalArgumentException
	 *             日期为空
	 */
	public static Date addMinute(Date date, int amount) {
		if (date == null) {
			throw new IllegalArgumentException("日期不能为空！");
		}
		if (amount == 0) {
			return date;
		}

		return calculateTime(date, Calendar.MINUTE, amount);
	}

	/**
	 * 时间计算 - 秒<br>
	 * 
	 * 以当前时间为基础
	 * 
	 * @param amount
	 *            增加（正数）/减小（负数）的数量
	 * @return 新日期
	 */
	public static Date addSecond(int amount) {
		if (amount == 0) {
			return new Date();
		}

		return calculateTime(null, Calendar.SECOND, amount);
	}

	/**
	 * 时间计算 - 秒
	 * 
	 * @param date
	 *            日期（不能为空）
	 * @param amount
	 *            增加（正数）/减小（负数）的数量
	 * @return 新日期
	 * @throws IllegalArgumentException
	 *             日期为空
	 */
	public static Date addSecond(Date date, int amount) {
		if (date == null) {
			throw new IllegalArgumentException("日期不能为空！");
		}
		if (amount == 0) {
			return date;
		}

		return calculateTime(date, Calendar.SECOND, amount);
	}

	/**
	 * 时间比较
	 * 
	 * @param a
	 *            日期A（不能为空）
	 * @param b
	 *            日期B（不能为空）
	 * @return 小于零：A小于B（A在B之前） 等于零：A等于B 大于零：A大于B（A在B之后）
	 * @throws IllegalArgumentException
	 *             日期为空
	 */
	public static int compare(Date a, Date b) {
		if (a == null && b == null) {
			return 0;
		}
		if (a == null || b == null) {
			throw new IllegalArgumentException("日期" + (a == null ? "A" : "B") + "不能为空！");
		}

		return a.compareTo(b);
	}

	/**
	 * 时间差 - 毫秒<br>
	 * 
	 * 与当前时间的时间差
	 * 
	 * @param date
	 *            日期（不能为空）
	 * @return 毫秒数
	 * @throws IllegalArgumentException
	 *             日期为空
	 */
	public static long millisecondDifference(Date date) {
		if (date == null) {
			throw new IllegalArgumentException("日期不能为空！");
		}

		return millisecondDifference(new Date(), date);
	}

	/**
	 * 时间差 - 毫秒
	 * 
	 * @param a
	 *            日期A（不能为空）
	 * @param b
	 *            日期B（不能为空）
	 * @return 毫秒数
	 * @throws IllegalArgumentException
	 *             日期为空
	 */
	public static long millisecondDifference(Date a, Date b) {
		if (a == null && b == null) {
			return 0;
		}
		if (a == null || b == null) {
			throw new IllegalArgumentException("日期" + (a == null ? "A" : "B") + "不能为空！");
		}

		return Math.abs(a.getTime() - b.getTime());
	}

	/**
	 * 时间差 - 秒<br>
	 * 
	 * 与当前时间的时间差
	 * 
	 * @param date
	 *            日期（不能为空）
	 * @return 秒数（不足1秒返回0）
	 * @throws IllegalArgumentException
	 *             日期为空
	 */
	public static long secondDifference(Date date) {
		if (date == null) {
			throw new IllegalArgumentException("日期不能为空！");
		}

		return secondDifference(new Date(), date);
	}

	/**
	 * 时间差 - 秒
	 * 
	 * @param a
	 *            日期A（不能为空）
	 * @param b
	 *            日期B（不能为空）
	 * @return 秒数（不足1秒返回0）
	 * @throws IllegalArgumentException
	 *             日期为空
	 */
	public static long secondDifference(Date a, Date b) {
		if (a == null && b == null) {
			return 0;
		}
		if (a == null || b == null) {
			throw new IllegalArgumentException("日期" + (a == null ? "A" : "B") + "不能为空！");
		}

		return Math.abs(a.getTime() - b.getTime()) / 1000L;
	}

	/**
	 * 时间差 - 分钟<br>
	 * 
	 * 与当前时间的时间差
	 * 
	 * @param date
	 *            日期（不能为空）
	 * @return 分钟数（不足1分钟返回0）
	 * @throws IllegalArgumentException
	 *             日期为空
	 */
	public static long minuteDifference(Date date) {
		if (date == null) {
			throw new IllegalArgumentException("日期不能为空！");
		}

		return minuteDifference(new Date(), date);
	}

	/**
	 * 时间差 - 分钟
	 * 
	 * @param a
	 *            日期A（不能为空）
	 * @param b
	 *            日期B（不能为空）
	 * @return 分钟数（不足1分钟返回0）
	 * @throws IllegalArgumentException
	 *             日期为空
	 */
	public static long minuteDifference(Date a, Date b) {
		if (a == null && b == null) {
			return 0;
		}
		if (a == null || b == null) {
			throw new IllegalArgumentException("日期" + (a == null ? "A" : "B") + "不能为空！");
		}

		return Math.abs(a.getTime() - b.getTime()) / (1000L * 60);
	}

	/**
	 * 时间差 - 小时
	 * 
	 * @param a
	 *            日期A（不能为空）
	 * @param b
	 *            日期B（不能为空）
	 * @return 小时数（不足1小时返回0）
	 * @throws IllegalArgumentException
	 *             日期为空
	 */
	public static long hourDifference(Date a, Date b) {
		if (a == null && b == null) {
			return 0;
		}
		if (a == null || b == null) {
			throw new IllegalArgumentException("日期" + (a == null ? "A" : "B") + "不能为空！");
		}

		return Math.abs(a.getTime() - b.getTime()) / (1000L * 60 * 60);
	}

	/**
	 * 时间差 - 天
	 * 
	 * @param a
	 *            日期A（不能为空）
	 * @param b
	 *            日期B（不能为空）
	 * @return 天数（不足1天返回0）
	 * @throws IllegalArgumentException
	 *             日期为空
	 */
	public static long dayDifference(Date a, Date b) {
		if (a == null && b == null) {
			return 0;
		}
		if (a == null || b == null) {
			throw new IllegalArgumentException("日期" + (a == null ? "A" : "B") + "不能为空！");
		}

		return Math.abs(a.getTime() - b.getTime()) / (1000L * 60 * 60 * 24);
	}

	/**
	 * 时间差 - 口语化（假设一个月有30天）（最高单位：年；最低单位：秒）
	 * 
	 * @param a
	 *            日期A（不能为空）
	 * @param b
	 *            日期B（不能为空）
	 * @return x年x月x天x小时x分钟x秒
	 * @throws IllegalArgumentException
	 *             日期为空
	 */
	public static String informalDifference(Date a, Date b) {
		if (a == null && b == null) {
			return "0秒";
		}
		if (a == null || b == null) {
			throw new IllegalArgumentException("日期" + (a == null ? "A" : "B") + "不能为空！");
		}

		long n = Math.abs(a.getTime() - b.getTime()) / 1000L;// 秒
		long days = 30;// 每月多少天
		long second = 0, minute = 0, hour = 0, day = 0, month = 0, year = 0;
		if (n > 0) {// 秒
			second = n % 60L;
			n = n / 60L;
			if (n > 0) {// 分钟
				minute = n % 60L;
				n = n / 60L;
				if (n > 0) {// 小时
					hour = n % 24L;
					n = n / 24L;
					if (n > 0) {// 天
						day = n % days;
						n = n / days;
						if (n > 0) {// 月
							month = n % 12;
							n = n / 12;
							if (n > 0) {// 年
								year = n;
							}
						}
					}
				}
			}
		}

		StringBuilder sb = new StringBuilder();
		if (year > 0) {
			sb.append(year).append("年");
		}
		if (month > 0) {
			sb.append(month).append("月");
		}
		if (day > 0) {
			sb.append(day).append("天");
		}
		if (hour > 0) {
			sb.append(hour).append("小时");
		}
		if (minute > 0) {
			sb.append(minute).append("分钟");
		}
		if (second > 0 || sb.length() == 0) {
			sb.append(second).append("秒");
		}
		return sb.toString();
	}

	/**
	 * 时间差 - 口语化（最高单位：天；最低单位：秒）
	 * 
	 * @param a
	 *            日期A（不能为空）
	 * @param b
	 *            日期B（不能为空）
	 * @return x天x小时x分钟x秒
	 * @throws IllegalArgumentException
	 *             日期为空
	 */
	public static String oralDifference(Date a, Date b) {
		if (a == null && b == null) {
			return "0秒";
		}
		if (a == null || b == null) {
			throw new IllegalArgumentException("日期" + (a == null ? "A" : "B") + "不能为空！");
		}

		long n = Math.abs(a.getTime() - b.getTime()) / 1000L;// 秒
		long second = 0, minute = 0, hour = 0, day = 0;
		if (n > 0) {// 秒
			second = n % 60L;
			n = n / 60L;
			if (n > 0) {// 分钟
				minute = n % 60L;
				n = n / 60L;
				if (n > 0) {// 小时
					hour = n % 24L;
					n = n / 24L;
					if (n > 0) {// 天
						day = n;
					}
				}
			}
		}
		StringBuilder sb = new StringBuilder();
		if (day > 0) {
			sb.append(day).append("天");
		}
		if (hour > 0) {
			sb.append(hour).append("小时");
		}
		if (minute > 0) {
			sb.append(minute).append("分钟");
		}
		if (second > 0 || sb.length() == 0) {
			sb.append(second).append("秒");
		}
		return sb.toString();
	}

	/**
	 * 时间差 - 口语化 - 分钟（最高单位：天；最低单位：分钟）
	 * 
	 * @param a
	 *            日期A（不能为空）
	 * @param b
	 *            日期B（不能为空）
	 * @return x天x小时x分钟
	 * @throws IllegalArgumentException
	 *             日期为空
	 */
	public static String oralDifferenceByMinute(Date a, Date b) {
		if (a == null && b == null) {
			return "0分钟";
		}
		if (a == null || b == null) {
			throw new IllegalArgumentException("日期" + (a == null ? "A" : "B") + "不能为空！");
		}

		long n = Math.abs(a.getTime() - b.getTime()) / 1000L;// 秒
		long minute = 0, hour = 0, day = 0;
		if (n > 0) {// 秒
//			minute = 1;// 不足1分钟记为1分钟
			n = n / 60L;
			if (n > 0) {// 分钟
				minute = n % 60L;
				n = n / 60L;
				if (n > 0) {// 小时
					hour = n % 24L;
					n = n / 24L;
					if (n > 0) {// 天
						day = n;
					}
				}
			}
		}
		StringBuilder sb = new StringBuilder();
		if (day > 0) {
			sb.append(day).append("天");
		}
		if (hour > 0) {
			sb.append(hour).append("小时");
		}
		if (minute > 0 || sb.length() == 0) {
			sb.append(minute).append("分钟");
		}
		return sb.toString();
	}

	/**
	 * 口语化 - 分钟（最高单位：天；最低单位：分钟）
	 * 
	 * @param n
	 *            分钟数（>0）
	 * @return x天x小时x分钟
	 */
	public static String oral4Minute(long n) {
		if (n < 1) {
			return "0分钟";
		}

		long minute = 0, hour = 0, day = 0;
		if (n > 0) {// 分钟
			minute = n % 60L;
			n = n / 60L;
			if (n > 0) {// 小时
				hour = n % 24L;
				n = n / 24L;
				if (n > 0) {// 天
					day = n;
				}
			}
		}
		StringBuilder sb = new StringBuilder();
		if (day > 0) {
			sb.append(day).append("天");
		}
		if (hour > 0) {
			sb.append(hour).append("小时");
		}
		if (minute > 0 || sb.length() == 0) {
			sb.append(minute).append("分钟");
		}
		return sb.toString();
	}

}
