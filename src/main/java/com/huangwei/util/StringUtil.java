package com.huangwei.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 */
public class StringUtil {

	/** 空字符串 */
	public static final String Empty = "";

	/**
	 * 是否为空字符串<br>
	 * <br>
	 * 注意：此方法只判断字符串是否为NULL或长度为零，如果要判断是否全部由空白字符组成，请使用 isBlank 方法
	 * 
	 * @param str
	 *            字符串
	 * @return true:空（NULL 或 长度为零） false:非空
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	/**
	 * 是否为非空字符串<br>
	 * <br>
	 * 注意：此方法只判断字符串是否为NULL或长度为零，如果要判断是否全部由空白字符组成，请使用 isNotBlank 方法
	 * 
	 * @param str
	 *            字符串
	 * @return true:非空 false:空（NULL 或 长度为零）
	 */
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	/**
	 * 是否全部由空白字符组成
	 * 
	 * @param str
	 *            字符串
	 * @return true:空白（NULL 或 长度为零 或 全部由空白字符组成） false:非空白
	 */
	public static boolean isBlank(String str) {
		return str == null || str.length() == 0 || str.trim().length() == 0;
	}

	/**
	 * 是否为非空白字符串
	 * 
	 * @param str
	 *            字符串
	 * @return true:非空白 false:空白（NULL 或 长度为零 或 全部由空白字符组成）
	 */
	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}

	/**
	 * 如果字符串为NULL，返回给定的默认值
	 * 
	 * @param str
	 *            字符串
	 * @param defaultValue
	 *            默认值
	 * @return 原始值 或 默认值
	 */
	public static String ifNull(String str, String defaultValue) {
		if (str == null) {
			return defaultValue;
		}

		return str;
	}

	/**
	 * 如果字符串为空（NULL 或 长度为零），返回给定的默认值
	 * 
	 * @param str
	 *            字符串
	 * @param defaultValue
	 *            默认值
	 * @return 原始值 或 默认值
	 */
	public static String ifEmpty(String str, String defaultValue) {
		if (str == null || str.length() == 0) {
			return defaultValue;
		}

		return str;
	}

	/**
	 * 如果字符串为空白（NULL 或 长度为零 或 全部由空白字符组成），返回给定的默认值
	 * 
	 * @param str
	 *            字符串
	 * @param defaultValue
	 *            默认值
	 * @return 原始值 或 默认值
	 */
	public static String ifBlank(String str, String defaultValue) {
		if (str == null || str.length() == 0 || (str = str.trim()).length() == 0) {
			return defaultValue == null ? null : defaultValue.trim();
		}

		return str;
	}

	/**
	 * 字符串过滤（防SQL注入及XSS）<br>
	 * <br>
	 * 过滤内容：<>'"();%&~^及两端的空白字符<br>
	 * 
	 * @param str
	 *            要过滤的字符串
	 * @return 过滤后的字符串（不为NULL）
	 */
	public static String filter(String str) {
		if (str == null || "".equals(str = str.trim())) {
			return Empty;
		}

		return str.replaceAll("<|>|'|\"|;|/|%|~|\\^", "");
	}

	/**
	 * 去除首尾空格并限定长度（丢弃超出部分）
	 * 
	 * @param str
	 *            字符串
	 * @param maxLength
	 *            最大长度（>0）
	 * @return 空字符串 或 处理后的字符串
	 * @throws IllegalArgumentException
	 *             长度小于1
	 */
	public static String trimAndLimit(String str, int maxLength) {
		if (maxLength < 1) {
			throw new IllegalArgumentException("最大长度不能小于1！");
		}
		if (str == null || str.length() == 0 || (str = str.trim()).length() == 0) {
			return Empty;
		}

		return str.length() > maxLength ? str.substring(0, maxLength) : str;
	}

	/**
	 * 从对象中移除指定的前导匹配项<br>
	 * <br>
	 * 匹配规则：使用 String.startsWith 进行整体匹配
	 * 
	 * @param str
	 *            字符串
	 * @param prefix
	 *            前导匹配项
	 * @return 处理后的字符串（不为NULL）
	 */
	public static String trimStart(String str, String prefix) {
		if (isEmpty(str)) {
			return Empty;
		}
		if (isEmpty(prefix)) {
			return str;
		}

		if (str.startsWith(prefix)) {
			str = str.substring(prefix.length(), str.length());
		}
		return str;
	}

	/**
	 * 从对象中移除指定的尾部匹配项<br>
	 * <br>
	 * 匹配规则：使用 String.endsWith 进行整体匹配
	 * 
	 * @param str
	 *            字符串
	 * @param suffix
	 *            尾部匹配项
	 * @return 处理后的字符串（不为NULL）
	 */
	public static String trimEnd(String str, String suffix) {
		if (isEmpty(str)) {
			return Empty;
		}
		if (isEmpty(suffix)) {
			return str;
		}

		if (str.endsWith(suffix)) {
			str = str.substring(0, str.length() - suffix.length());
		}
		return str;
	}

	/**
	 * 截取 start（第一次出现） 和 end（最后一次出现） 之间的字符串
	 * 
	 * @param str
	 *            字符串
	 * @param start
	 *            起始字符串（为空：不作为条件）
	 * @param end
	 *            结束字符串（为空：不作为条件）
	 * @return 处理后的字符串（不为NULL）
	 */
	public static String subStr(String str, String start, String end) {
		if (isEmpty(str)) {
			return Empty;
		}

		int first = 0, last = str.length(), total = last;
		if (isNotEmpty(start)) {
			int i = str.indexOf(start);
			if (i != -1) {
				first = i + start.length();
			}
		}
		if (isNotEmpty(end)) {
			int i = str.lastIndexOf(end);
			if (i != -1) {
				last = i;
			}
		}
		if (first - last > 0) {
			last = first;
		}
		if (first == 0 && last - total == 0) {
			return str;
		} else {
			return str.substring(first, last);
		}
	}

	/**
	 * 转义正则特殊字符
	 * 
	 * @param str
	 *            目标字符串
	 * @return 转义后的字符串
	 */
	public static String escapeSpecialWord(String str) {
		if (isEmpty(str)) {
			return str;
		}

		String[] array = { "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|" };
		for (String key : array) {
			if (str.contains("\\" + key)) {
				str = str.replace("\\" + key, key);/* 将已转义过的字符还原 */
			}
		}
		if (str.contains("\\")) {
			str = str.replace("\\", "\\\\");/* 转义\ */
		}
		for (String key : array) {
			if (str.contains(key)) {
				str = str.replace(key, "\\" + key);/* 逐个进行转义替换 */
			}
		}
		return str;
	}

	/**
	 * 截取 start 和 end 之间的字符串（使用正则匹配）<br>
	 * <br>
	 * 注意：如果 start 和 end 的组合出现多次，只取第一次出现时的结果；如果需要收集所有的匹配结果，请使用 subSequence 方法
	 * 
	 * @param str
	 *            字符串（不能为空）
	 * @param start
	 *            起始字符串（不能为空）
	 * @param end
	 *            结束字符串（不能为空）
	 * @return null 或 匹配结果
	 */
	public static String substring(String str, String start, String end) {
		if (isEmpty(str) || isEmpty(start) || isEmpty(end)) {
			return null;
		}

		start = escapeSpecialWord(start);
		end = escapeSpecialWord(end);

		Matcher m = Pattern.compile("(?<=" + start + ").*?(?=" + end + ")").matcher(str);
		if (m.find()) {
			return m.group();
		} else {
			return null;
		}
	}

	/**
	 * 截取 start 和 end 之间的字符串（使用正则匹配）<br>
	 * <br>
	 * 注意：收集所有的匹配结果，放入数组
	 * 
	 * @param str
	 *            字符串（不能为空）
	 * @param start
	 *            起始字符串（不能为空）
	 * @param end
	 *            结束字符串（不能为空）
	 * @return null 或 匹配结果（数组）
	 */
	public static String[] subSequence(String str, String start, String end) {
		if (isEmpty(str) || isEmpty(start) || isEmpty(end)) {
			return null;
		}

		start = escapeSpecialWord(start);
		end = escapeSpecialWord(end);

		List<String> list = new ArrayList<String>();
		Matcher m = Pattern.compile("(?<=" + start + ").*?(?=" + end + ")").matcher(str);
		while (m.find()) {
			list.add(m.group());
		}
		return list.isEmpty() ? null : list.toArray(new String[] {});
	}

	/**
	 * 将字符串用指定分隔符拆分成数组<br>
	 * 
	 * 简单拆分："a,b,c"用分隔符","拆分为[a, b, c]<br>
	 * 包围拆分："[a][b][c]"用分隔符"[]"拆分为[a, b, c]<br>
	 * 
	 * @param str
	 *            字符串
	 * @param separator
	 *            分隔符
	 * @return 空数组 或 数组（拆分结果）
	 */
	public static String[] toArray(String str, String separator) {
		if (str == null) {
			return new String[0];
		}
		if (str.isEmpty() || separator == null || "".equals(separator)) {
			return new String[] { str };
		}

		/* 特殊间隔符（需要转义）：.*?+^$|\()[]{} */
		if (".".equals(separator) || "*".equals(separator) || "?".equals(separator) || "+".equals(separator)
				|| "^".equals(separator) || "$".equals(separator) || "|".equals(separator) || "\\".equals(separator)
				|| "(".equals(separator) || ")".equals(separator) || "[".equals(separator) || "]".equals(separator)
				|| "{".equals(separator) || "}".equals(separator)) {
			return str.split("\\\\" + separator);
		}

		/* 包围形式的字符串拆分，如：(a)(b)(c)、[a][b][c]、{a}{b}{c} */
		if ("()".equals(separator) || "[]".equals(separator) || "{}".equals(separator) || "<>".equals(separator)) {
			String a = separator.substring(0, 1);
			String b = separator.substring(1);
			if (str.startsWith(a)) {
				str = str.substring(1);
			}
			if (str.endsWith(b)) {
				str = str.substring(0, str.length() - 1);
			}
			return str.split("\\\\" + b + "\\\\" + a);
		}

		return str.split(separator);
	}

	/**
	 * 字符串断句（按固定长度拆分）
	 * 
	 * @param str
	 *            字符串（不能为空）
	 * @param separator
	 *            分隔符（不能为空）
	 * @param unit
	 *            单元长度（>=1）
	 * @return 原字符串 或 断句结果
	 */
	public static String wordBreak(String str, String separator, int unit) {
		if (isEmpty(str) || isEmpty(separator) || unit < 1) {
			return str;
		}

		StringBuilder sb = new StringBuilder();
		int length = str.length();
		for (int i = 0; i < length; i += unit) {
			if (i + unit >= length) {
				sb.append(str.substring(i));
				break;
			}
			sb.append(str.substring(i, i + unit)).append(separator);
		}
		return sb.toString();
	}

	/**
	 * (十进制)数字字符串 -> integer
	 * 
	 * @param str
	 *            (十进制)数字字符串
	 * @param defaultValue
	 *            默认值（字符串为空/格式错误时返回默认值）
	 * @return integer
	 */
	public static int toInt(String str, int defaultValue) {
		if (str == null || "".equals(str = str.trim())) {
			return defaultValue;
		}

		try {
			return new BigDecimal(str).intValue();
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * (十进制)数字字符串 -> long
	 * 
	 * @param str
	 *            (十进制)数字字符串
	 * @param defaultValue
	 *            默认值（字符串为空/格式错误时返回默认值）
	 * @return long
	 */
	public static long toLong(String str, long defaultValue) {
		if (str == null || "".equals(str = str.trim())) {
			return defaultValue;
		}

		try {
			return new BigDecimal(str).longValue();
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * (十进制)数字字符串 -> double
	 * 
	 * @param str
	 *            (十进制)数字字符串
	 * @param defaultValue
	 *            默认值（字符串为空/格式错误时返回默认值）
	 * @return double
	 */
	public static double toDouble(String str, double defaultValue) {
		if (str == null || "".equals(str = str.trim())) {
			return defaultValue;
		}

		try {
			return new BigDecimal(str).doubleValue();
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 布尔值字符串 -> boolean
	 * 
	 * @param str
	 *            布尔值字符串
	 * @param defaultValue
	 *            默认值（字符串为空/格式错误时返回默认值）
	 * @return boolean
	 */
	public static boolean toBoolean(String str, boolean defaultValue) {
		if (str == null || "".equals(str = str.trim())) {
			return defaultValue;
		}

		try {
			return Boolean.parseBoolean(str);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 校验数字合法性
	 * 
	 * @param o
	 *            数字对象（不能为空）
	 * @throws NullPointerException
	 *             对象为空
	 * @throws IllegalArgumentException
	 *             非数数 或 字不合法
	 */
	public static void numberValidity(Object o) {
		if (o == null) {
			throw new NullPointerException("对象不能为空！");
		}
		if (!(o instanceof Number)) {
			throw new IllegalArgumentException("非数字对象！");
		}

		if (o instanceof Double) {
			if (Double.isNaN((Double) o) || Double.isInfinite((Double) o)) {
				throw new IllegalArgumentException("数字不合法：无限大或非数字！");
			}
		} else if (o instanceof Float) {
			if (Float.isNaN((Float) o) || Float.isInfinite((Float) o)) {
				throw new IllegalArgumentException("数字不合法：无限大或非数字！");
			}
		} else if (o instanceof BigDecimal || o instanceof BigInteger) {
			//
		}
	}

	/**
	 * 数字 -> 字符串（去除小数点后无意义的零）
	 * 
	 * @param n
	 *            数字对象（不能为空）
	 * @return 字符串
	 * @throws IllegalArgumentException
	 *             数字为空 或 不合法
	 */
	private static String _numberToString(Number n) {
		// 校验合法性
		numberValidity(n);

		String s = n.toString();
		if (n instanceof Float || n instanceof Double) {
			s = new BigDecimal(s).toPlainString();
		}
		// 去除小数点后无意义的零
		if (s.indexOf('.') > 0 && s.indexOf('e') < 0 && s.indexOf('E') < 0) {
			while (s.endsWith("0")) {
				s = s.substring(0, s.length() - 1);
			}
			if (s.endsWith(".")) {
				s = s.substring(0, s.length() - 1);
			}
		}
		return s;
	}

	/**
	 * 数字 -> 字符串<br>
	 * <br>
	 * ①null -> ""(空字符串)<br>
	 * ②去除小数点后无意义的零
	 * 
	 * @param n
	 *            数字对象
	 * @return "" 或 字符串
	 */
	public static String numberToStr(Number n) {
		if (n == null) {
			return Empty;
		}

		return _numberToString(n);
	}

	/**
	 * 数字 -> 字符串<br>
	 * <br>
	 * ①null -> 0<br>
	 * ②去除小数点后无意义的零
	 * 
	 * @param n
	 *            数字对象
	 * @return 字符串（非空）
	 */
	public static String numberToString(Number n) {
		if (n == null) {
			return "0";
		}

		return _numberToString(n);
	}

	/**
	 * Object -> 字符串<br>
	 * <br>
	 * ①null -> ""(空字符串)<br>
	 * ②去除小数点后无意义的零
	 * 
	 * @param o
	 *            对象
	 * @return "" 或 字符串
	 */
	public static String toStr(Object o) {
		if (o == null) {
			return Empty;
		}

		if (o instanceof Number) {
			return numberToStr((Number) o);
		}
		if (o.getClass().isArray()) {
			return Arrays.toString((Object[]) o);
		}

		return o.toString();
	}

}
