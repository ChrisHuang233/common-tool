package com.huangwei.crypto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.Base64;

/**
 * Base64编解码工具
 */
public class Base64Util {
	protected static Logger logger = LoggerFactory.getLogger(Base64Util.class);

	/** 字符编码集 - UTF-8 */
	private static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

	/**
	 * 编码<br>
	 * <br>
	 * ①使用UTF-8字符集<br>
	 * ②结果不含回车和换行(\r, \n)
	 * 
	 * @param data
	 *            数据（不能为空）
	 * @return 空字符串(参数为空) 或 结果
	 */
	public static String encode(final String data) {
		if (data == null || "".equals(data)) {
			return "";
		}

		return encode(data.getBytes(CHARSET_UTF8));
	}

	/**
	 * 编码<br>
	 * <br>
	 * ①结果不含回车和换行(\r, \n)
	 * 
	 * @param data
	 *            数据（不能为空）
	 * @return 空字符串(参数为空) 或 结果
	 */
	public static String encode(final byte[] data) {
		if (data == null || data.length < 1) {
			return "";
		}

//		return new sun.misc.BASE64Encoder().encode(data).replaceAll("\r|\n", "");// Java 1.7及以下
		return Base64.getEncoder().encodeToString(data);// Java 1.8
	}

	/**
	 * 解码<br>
	 * <br>
	 * ①使用UTF-8字符集<br>
	 * ②结果为UTF-8编码的字符串
	 * 
	 * @param data
	 *            数据（不能为空）
	 * @return 空字符串(参数为空或解码出错) 或 结果
	 */
	public static String decodeToString(final String data) {
		if (data == null || "".equals(data)) {
			return "";
		}

		return new String(decode(data), CHARSET_UTF8);
	}

	/**
	 * 解码
	 * 
	 * @param data
	 *            数据（不能为空）
	 * @return 空数组(参数为空或解码出错) 或 结果
	 */
	public static byte[] decode(final String data) {
		if (data == null || "".equals(data)) {
			return new byte[0];
		}

		try {
//			return new sun.misc.BASE64Decoder().decodeBuffer(data);// Java 1.7及以下
			return Base64.getDecoder().decode(data);// Java 1.8
		} catch (Exception e) {
			logger.error("Base64解码出错！exception: [" + e.getClass().getName() + ": " + e.getMessage() + "]");
			return new byte[0];
		}
	}

}
