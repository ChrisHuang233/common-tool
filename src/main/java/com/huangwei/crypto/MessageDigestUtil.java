package com.huangwei.crypto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.security.MessageDigest;

/**
 * 消息摘要算法工具<br>
 * <br>
 * 包含：MD5、SHA1、SHA256
 */
public class MessageDigestUtil {
	protected static Logger logger = LoggerFactory.getLogger(MessageDigestUtil.class);

	/** 字符编码集 - UTF-8 */
	protected static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

	/**
	 * MD5摘要<br>
	 * <br>
	 * ①使用UTF-8字符集<br>
	 * ②摘要为32位小写十六进制字符串
	 * 
	 * @param data
	 *            数据（不能为NULL）
	 * @return NULL(参数为NULL或未知异常) 或 摘要
	 */
	public static String md5Hex(final String data) {
		if (data == null) {
			return null;
		}

		return md5Hex(data.getBytes(CHARSET_UTF8));
	}

	/**
	 * MD5摘要<br>
	 * <br>
	 * ①摘要为32位小写十六进制字符串
	 * 
	 * @param data
	 *            数据（不能为NULL）
	 * @return NULL(参数为NULL或未知异常) 或 摘要
	 */
	public static String md5Hex(final byte[] data) {
		if (data == null) {
			return null;
		}

		byte[] digest = md5(data);
		if (digest == null || digest.length < 1) {
			return null;
		} else {
			return byteToHex(digest);
		}
	}

	/**
	 * MD5摘要<br>
	 * <br>
	 * ①使用UTF-8字符集<br>
	 * ②摘要为字节数组，其长度为16
	 * 
	 * @param data
	 *            数据（不能为NULL）
	 * @return NULL(参数为NULL或未知异常) 或 摘要
	 */
	public static byte[] md5(final String data) {
		if (data == null) {
			return null;
		}

		return md5(data.getBytes(CHARSET_UTF8));
	}

	/**
	 * MD5摘要<br>
	 * <br>
	 * ①摘要为字节数组，其长度为16
	 * 
	 * @param data
	 *            数据（不能为NULL）
	 * @return NULL(参数为NULL或未知异常) 或 摘要
	 */
	public static byte[] md5(final byte[] data) {
		if (data == null) {
			return null;
		}

		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(data);
			return md5.digest();
		} catch (Exception e) {
			logger.error("MD5摘要计算出错！exception: [" + e.getClass().getName() + ": " + e.getMessage() + "]");
			return null;
		}
	}

	/**
	 * MD5摘要<br>
	 * <br>
	 * ①使用UTF-8字符集<br>
	 * ②摘要为16位小写十六进制字符串
	 * 
	 * @param data
	 *            数据（不能为NULL）
	 * @return NULL(参数为NULL或未知异常) 或 摘要
	 */
	public static String md5HalfHex(final String data) {
		if (data == null) {
			return null;
		}

		return md5HalfHex(data.getBytes(CHARSET_UTF8));
	}

	/**
	 * MD5摘要<br>
	 * <br>
	 * ①摘要为16位小写十六进制字符串
	 * 
	 * @param data
	 *            数据（不能为NULL）
	 * @return NULL(参数为NULL或未知异常) 或 摘要
	 */
	public static String md5HalfHex(final byte[] data) {
		if (data == null) {
			return null;
		}

		byte[] digest = md5Half(data);
		if (digest == null || digest.length < 1) {
			return null;
		} else {
			return byteToHex(digest);
		}
	}

	/**
	 * MD5摘要<br>
	 * <br>
	 * ①使用UTF-8字符集<br>
	 * ②摘要为字节数组，其长度为8
	 * 
	 * @param data
	 *            数据（不能为NULL）
	 * @return NULL(参数为NULL或未知异常) 或 摘要
	 */
	public static byte[] md5Half(final String data) {
		if (data == null) {
			return null;
		}

		return md5Half(data.getBytes(CHARSET_UTF8));
	}

	/**
	 * MD5摘要<br>
	 * <br>
	 * ①摘要为字节数组，其长度为8
	 * 
	 * @param data
	 *            数据（不能为NULL）
	 * @return NULL(参数为NULL或未知异常) 或 摘要
	 */
	public static byte[] md5Half(final byte[] data) {
		if (data == null) {
			return null;
		}

		byte[] digest = md5(data);
		if (digest == null || digest.length < 1) {
			return null;
		} else {
			byte[] result = new byte[8];
			System.arraycopy(digest, 4, result, 0, result.length);
			return result;
		}
	}

	/**
	 * SHA1摘要<br>
	 * <br>
	 * ①使用UTF-8字符集<br>
	 * ②摘要为40位小写十六进制字符串
	 * 
	 * @param data
	 *            数据（不能为NULL）
	 * @return NULL(参数为NULL或未知异常) 或 摘要
	 */
	public static String sha1Hex(final String data) {
		if (data == null) {
			return null;
		}

		return sha1Hex(data.getBytes(CHARSET_UTF8));
	}

	/**
	 * SHA1摘要<br>
	 * <br>
	 * ①摘要为40位小写十六进制字符串
	 * 
	 * @param data
	 *            数据（不能为NULL）
	 * @return NULL(参数为NULL或未知异常) 或 摘要
	 */
	public static String sha1Hex(final byte[] data) {
		if (data == null) {
			return null;
		}

		byte[] digest = sha1(data);
		if (digest == null || digest.length < 1) {
			return null;
		} else {
			return byteToHex(digest);
		}
	}

	/**
	 * SHA1摘要<br>
	 * <br>
	 * ①使用UTF-8字符集<br>
	 * ②摘要为字节数组，其长度为20
	 * 
	 * @param data
	 *            数据（不能为NULL）
	 * @return NULL(参数为NULL或未知异常) 或 摘要
	 */
	public static byte[] sha1(final String data) {
		if (data == null) {
			return null;
		}

		return sha1(data.getBytes(CHARSET_UTF8));
	}

	/**
	 * SHA1摘要<br>
	 * <br>
	 * ①摘要为字节数组，其长度为20
	 * 
	 * @param data
	 *            数据（不能为NULL）
	 * @return NULL(参数为NULL或未知异常) 或 摘要
	 */
	public static byte[] sha1(final byte[] data) {
		if (data == null) {
			return null;
		}

		try {
			MessageDigest sha1 = MessageDigest.getInstance("SHA1");
			sha1.update(data);
			return sha1.digest();
		} catch (Exception e) {
			logger.error("SHA1摘要计算出错！exception: [" + e.getClass().getName() + ": " + e.getMessage() + "]");
			return null;
		}
	}

	/**
	 * SHA256摘要<br>
	 * <br>
	 * ①使用UTF-8字符集<br>
	 * ②摘要为64位小写十六进制字符串
	 * 
	 * @param data
	 *            数据（不能为NULL）
	 * @return NULL(参数为NULL或未知异常) 或 摘要
	 */
	public static String sha256Hex(final String data) {
		if (data == null) {
			return null;
		}

		return sha256Hex(data.getBytes(CHARSET_UTF8));
	}

	/**
	 * SHA256摘要<br>
	 * <br>
	 * ①摘要为64位小写十六进制字符串
	 * 
	 * @param data
	 *            数据（不能为NULL）
	 * @return NULL(参数为NULL或未知异常) 或 摘要
	 */
	public static String sha256Hex(final byte[] data) {
		if (data == null) {
			return null;
		}

		byte[] digest = sha256(data);
		if (digest == null || digest.length < 1) {
			return null;
		} else {
			return byteToHex(digest);
		}
	}

	/**
	 * SHA256摘要<br>
	 * <br>
	 * ①使用UTF-8字符集<br>
	 * ②摘要为字节数组，其长度为32
	 * 
	 * @param data
	 *            数据（不能为NULL）
	 * @return NULL(参数为NULL或未知异常) 或 摘要
	 */
	public static byte[] sha256(final String data) {
		if (data == null) {
			return null;
		}

		return sha256(data.getBytes(CHARSET_UTF8));
	}

	/**
	 * SHA256摘要<br>
	 * <br>
	 * ①摘要为字节数组，其长度为32
	 * 
	 * @param data
	 *            数据（不能为NULL）
	 * @return NULL(参数为NULL或未知异常) 或 摘要
	 */
	public static byte[] sha256(final byte[] data) {
		if (data == null) {
			return null;
		}

		try {
			MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
			sha256.update(data);
			return sha256.digest();
		} catch (Exception e) {
			logger.error("SHA256摘要计算出错！exception: [" + e.getClass().getName() + ": " + e.getMessage() + "]");
			return null;
		}
	}

	/**
	 * SHA384摘要<br>
	 * <br>
	 * ①使用UTF-8字符集<br>
	 * ②摘要为96位小写十六进制字符串
	 * 
	 * @param data
	 *            数据（不能为NULL）
	 * @return NULL(参数为NULL或未知异常) 或 摘要
	 */
	public static String sha384Hex(final String data) {
		if (data == null) {
			return null;
		}

		return sha384Hex(data.getBytes(CHARSET_UTF8));
	}

	/**
	 * SHA384摘要<br>
	 * <br>
	 * ①摘要为96位小写十六进制字符串
	 * 
	 * @param data
	 *            数据（不能为NULL）
	 * @return NULL(参数为NULL或未知异常) 或 摘要
	 */
	public static String sha384Hex(final byte[] data) {
		if (data == null) {
			return null;
		}

		byte[] digest = sha384(data);
		if (digest == null || digest.length < 1) {
			return null;
		} else {
			return byteToHex(digest);
		}
	}

	/**
	 * SHA384摘要<br>
	 * <br>
	 * ①使用UTF-8字符集<br>
	 * ②摘要为字节数组，其长度为48
	 * 
	 * @param data
	 *            数据（不能为NULL）
	 * @return NULL(参数为NULL或未知异常) 或 摘要
	 */
	public static byte[] sha384(final String data) {
		if (data == null) {
			return null;
		}

		return sha384(data.getBytes(CHARSET_UTF8));
	}

	/**
	 * SHA384摘要<br>
	 * <br>
	 * ①摘要为字节数组，其长度为48
	 * 
	 * @param data
	 *            数据（不能为NULL）
	 * @return NULL(参数为NULL或未知异常) 或 摘要
	 */
	public static byte[] sha384(final byte[] data) {
		if (data == null) {
			return null;
		}

		try {
			MessageDigest sha384 = MessageDigest.getInstance("SHA-384");
			sha384.update(data);
			return sha384.digest();
		} catch (Exception e) {
			logger.error("SHA384摘要计算出错！exception: [" + e.getClass().getName() + ": " + e.getMessage() + "]");
			return null;
		}
	}

	/**
	 * SHA384摘要<br>
	 * <br>
	 * ①使用UTF-8字符集<br>
	 * ②摘要为48位小写十六进制字符串
	 * 
	 * @param data
	 *            数据（不能为NULL）
	 * @return NULL(参数为NULL或未知异常) 或 摘要
	 */
	public static String sha384HalfHex(final String data) {
		if (data == null) {
			return null;
		}

		return sha384HalfHex(data.getBytes(CHARSET_UTF8));
	}

	/**
	 * SHA384摘要<br>
	 * <br>
	 * ①摘要为48位小写十六进制字符串
	 * 
	 * @param data
	 *            数据（不能为NULL）
	 * @return NULL(参数为NULL或未知异常) 或 摘要
	 */
	public static String sha384HalfHex(final byte[] data) {
		if (data == null) {
			return null;
		}

		byte[] digest = sha384Half(data);
		if (digest == null || digest.length < 1) {
			return null;
		} else {
			return byteToHex(digest);
		}
	}

	/**
	 * SHA384摘要<br>
	 * <br>
	 * ①使用UTF-8字符集<br>
	 * ②摘要为字节数组，其长度为24
	 * 
	 * @param data
	 *            数据（不能为NULL）
	 * @return NULL(参数为NULL或未知异常) 或 摘要
	 */
	public static byte[] sha384Half(final String data) {
		if (data == null) {
			return null;
		}

		return sha384Half(data.getBytes(CHARSET_UTF8));
	}

	/**
	 * SHA384摘要<br>
	 * <br>
	 * ①摘要为字节数组，其长度为24
	 * 
	 * @param data
	 *            数据（不能为NULL）
	 * @return NULL(参数为NULL或未知异常) 或 摘要
	 */
	public static byte[] sha384Half(final byte[] data) {
		if (data == null) {
			return null;
		}

		byte[] digest = sha384(data);
		if (digest == null || digest.length < 1) {
			return null;
		} else {
			byte[] result = new byte[24];
			System.arraycopy(digest, 12, result, 0, result.length);
			return result;
		}
	}

	/**
	 * byte[] -> 小写十六进制字符串
	 * 
	 * @param data
	 *            数据（不能为空）
	 * @return NULL(参数为空) 或 小写十六进制字符串
	 */
	private static String byteToHex(final byte[] data) {
		if (data == null || data.length < 1) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		for (byte b : data) {
			sb.append(Integer.toHexString(b >>> 4 & 0xF));
			sb.append(Integer.toHexString(b & 0xF));
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		String data = "0123456789";
		System.out.println("------------------ MD5 ------------------");
		System.out.println(md5Hex(data));
		System.out.println(md5Hex(data.getBytes(CHARSET_UTF8)));
		System.out.println(byteToHex(md5(data)));
		System.out.println(byteToHex(md5(data.getBytes(CHARSET_UTF8))));
		System.out.println(md5HalfHex(data));
		System.out.println(md5HalfHex(data.getBytes(CHARSET_UTF8)));
		System.out.println(byteToHex(md5Half(data)));
		System.out.println(byteToHex(md5Half(data.getBytes(CHARSET_UTF8))));
		System.out.println("------------------ SHA1 ------------------");
		System.out.println(sha1Hex(data));
		System.out.println(sha1Hex(data.getBytes(CHARSET_UTF8)));
		System.out.println(byteToHex(sha1(data)));
		System.out.println(byteToHex(sha1(data.getBytes(CHARSET_UTF8))));
		System.out.println("------------------ SHA256 ------------------");
		System.out.println(sha256Hex(data));
		System.out.println(sha256Hex(data.getBytes(CHARSET_UTF8)));
		System.out.println(byteToHex(sha256(data)));
		System.out.println(byteToHex(sha256(data.getBytes(CHARSET_UTF8))));
		System.out.println("------------------ SHA384 ------------------");
		System.out.println(sha384Hex(data));
		System.out.println(sha384Hex(data.getBytes(CHARSET_UTF8)));
		System.out.println(byteToHex(sha384(data)));
		System.out.println(byteToHex(sha384(data.getBytes(CHARSET_UTF8))));
		System.out.println(sha384HalfHex(data));
		System.out.println(sha384HalfHex(data.getBytes(CHARSET_UTF8)));
		System.out.println(byteToHex(sha384Half(data)));
		System.out.println(byteToHex(sha384Half(data.getBytes(CHARSET_UTF8))));
	}

}
