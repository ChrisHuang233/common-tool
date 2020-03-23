package com.huangwei.util;

import java.security.MessageDigest;

public class MD5Util {

	/**
	 * 32位小写MD5加密
	 * 
	 * @param str
	 *            要加密的字符串（不能为NULL）
	 * @return null（参数为NULL或加密异常） 或 加密后的32位小写字符串
	 */
	public static String lowercase(String str) {
		if (str == null)
			return null;

		try {
			StringBuilder sb = new StringBuilder();
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(str.getBytes());
			for (byte b : md5.digest()) {
				sb.append(Integer.toHexString(b >>> 4 & 0xf));
				sb.append(Integer.toHexString(b & 0xf));
			}
			return sb.length() < 1 ? null : sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 32位大写MD5加密
	 * 
	 * @param str
	 *            要加密的字符串（不能为NULL）
	 * @return null（参数为NULL或加密异常） 或 加密后的32位小写字符串
	 */
	public static String uppercase(String str) {
		if (str == null)
			return null;

		str = lowercase(str);
		return str == null ? null : str.toUpperCase();
	}

	/**
	 * 16位小写MD5加密
	 * 
	 * @param str
	 *            要加密的字符串（不能为NULL）
	 * @return null（参数为NULL或加密异常） 或 加密后的32位小写字符串
	 */
	public static String half(String str) {
		if (str == null)
			return null;

		str = lowercase(str);
		return str == null ? null : str.substring(8, 24);
	}

}
