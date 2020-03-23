package com.huangwei.util;

import java.nio.charset.Charset;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base64工具类
 */
public class Base64Util {
	protected static Logger logger = LoggerFactory.getLogger(Base64Util.class);

	/** 默认字符集（UTF-8） */
	private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

	/**
	 * 编码
	 * 
	 * @param src
	 *            源数据（不能为空）
	 * @return 空字符串（参数错误） 或 编码结果
	 */
	public static String encode(byte[] src) {
		if (src == null || src.length < 1) {
			return "";
		}

//		return new sun.misc.BASE64Encoder().encode(b);// Java 1.7及以下
		return Base64.getEncoder().encodeToString(src);// Java 1.8
	}

	/**
	 * 编码（UTF-8编码）
	 * 
	 * @param src
	 *            源数据（不能为空）
	 * @return 空字符串（参数错误） 或 编码结果
	 */
	public static String encode(String src) {
		if (src == null || "".equals(src)) {
			return "";
		}

		return encode(src.getBytes(DEFAULT_CHARSET));
	}

	/**
	 * 解码
	 * 
	 * @param src
	 *            源数据（不能为空）
	 * @return 空数组（参数错误/解码出错） 或 解码结果
	 */
	public static byte[] decode(String src) {
		if (src == null || "".equals(src)) {
			return new byte[0];
		}

		try {
//			return new sun.misc.BASE64Decoder().decodeBuffer(src);// Java 1.7及以下
			return Base64.getDecoder().decode(src);// Java 1.8
		} catch (Exception e) {
			logger.error("Base64解码出错！", e);
			return new byte[0];
		}
	}

	/**
	 * 解码（UTF-8编码）
	 * 
	 * @param src
	 *            源数据（不能为空）
	 * @return 空字符串（参数错误/解码出错） 或 解码结果
	 */
	public static String decodeToString(String src) {
		if (src == null || "".equals(src)) {
			return "";
		}

		return new String(decode(src), DEFAULT_CHARSET);
	}

}
