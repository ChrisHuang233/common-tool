package com.huangwei.util;

import java.security.MessageDigest;

/**
 * MD5摘要工具
 */
public class MD5Util {

	/**
	 * MD5摘要 - 32位小写
	 * 
	 * @param data
	 *            数据（不能为NULL）
	 * @return NULL(参数为NULL或未知异常) 或 32位小写十六进制字符串
	 */
	public static String lowercase(String data) {
		if (data == null) {
			return null;
		}

		try {
			StringBuilder sb = new StringBuilder();
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(data.getBytes());
			for (byte b : md5.digest()) {
				sb.append(Integer.toHexString(b >>> 4 & 0xF));
				sb.append(Integer.toHexString(b & 0xF));
			}
			return sb.length() < 1 ? null : sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * MD5摘要 - 32位大写
	 * 
	 * @param data
	 *            数据（不能为NULL）
	 * @return NULL(参数为NULL或未知异常) 或 32位大写十六进制字符串
	 */
	public static String uppercase(String data) {
		if (data == null) {
			return null;
		}

		data = lowercase(data);
		return data == null ? null : data.toUpperCase();
	}

	/**
	 * MD5摘要 - 16位小写
	 * 
	 * @param data
	 *            数据（不能为NULL）
	 * @return NULL(参数为NULL或未知异常) 或 16位小写十六进制字符串
	 */
	public static String half(String data) {
		if (data == null) {
			return null;
		}

		data = lowercase(data);
		return data == null ? null : data.substring(8, 24);
	}

}
