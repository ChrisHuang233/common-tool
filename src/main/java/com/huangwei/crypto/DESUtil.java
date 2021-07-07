package com.huangwei.crypto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.util.Base64;

/**
 * DES加解密工具<br>
 * <br>
 * ①块大小: 64比特(8字节)<br>
 */
public class DESUtil {
	protected static Logger logger = LoggerFactory.getLogger(DESUtil.class);

	/** 字符编码集 - UTF-8 */
	private static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");
	/** 加密算法 - DES */
	private static final String ALGORITHM = "DES";
	/** DES密钥字节数 */
	private static final int DES_KEY_LENGTH = 8;
	/** 默认密钥 */
	private static final String DEFAULT_KEY = "Hi,Java!";

	/**
	 * DES加密<br>
	 * <br>
	 * ①使用UTF-8字符集<br>
	 * ②使用默认密钥<br>
	 * ③加密结果为Base64字符串，不含回车和换行(\r, \n)
	 * 
	 * @param data
	 *            数据（不能为空）
	 * @return 空字符串(参数为空或加密出错) 或 加密结果
	 */
	public static String encryptBase64(String data) {
		if (data == null || data.isEmpty()) {
			return "";
		}

		return encryptBase64(data, DEFAULT_KEY);
	}

	/**
	 * DES加密<br>
	 * <br>
	 * ①使用UTF-8字符集<br>
	 * ②加密结果为Base64字符串，不含回车和换行(\r, \n)
	 * 
	 * @param data
	 *            数据（不能为空）
	 * @param key
	 *            密钥（为空：默认密钥）
	 * @return 空字符串(参数为空或加密出错) 或 加密结果
	 */
	public static String encryptBase64(String data, String key) {
		if (data == null || data.isEmpty()) {
			return "";
		}

		byte[] result = encrypt(data.getBytes(CHARSET_UTF8), desKey(key));
		if (result == null || result.length < 1) {
			return "";
		} else {
//			return new sun.misc.BASE64Encoder().encode(result).replaceAll("\r|\n", "");// Java 1.7及以下
			return Base64.getEncoder().encodeToString(result);// Java 1.8
		}
	}

	/**
	 * DES加密<br>
	 * <br>
	 * ①使用UTF-8字符集<br>
	 * ②使用默认密钥<br>
	 * ③加密结果为小写十六进制字符串
	 * 
	 * @param data
	 *            数据（不能为空）
	 * @return 空字符串(参数为空或加密出错) 或 加密结果
	 */
	public static String encryptHex(String data) {
		if (data == null || data.isEmpty()) {
			return "";
		}

		return encryptHex(data, DEFAULT_KEY);
	}

	/**
	 * DES加密<br>
	 * <br>
	 * ①使用UTF-8字符集<br>
	 * ②加密结果为小写十六进制字符串
	 * 
	 * @param data
	 *            数据（不能为空）
	 * @param key
	 *            密钥（为空：默认密钥）
	 * @return 空字符串(参数为空或加密出错) 或 加密结果
	 */
	public static String encryptHex(String data, String key) {
		if (data == null || data.isEmpty()) {
			return "";
		}

		byte[] result = encrypt(data.getBytes(CHARSET_UTF8), desKey(key));
		if (result == null || result.length < 1) {
			return "";
		} else {
			return byteToHex(result);
		}
	}

	/**
	 * DES加密<br>
	 * <br>
	 * ①密钥必须为8个字节<br>
	 * ②加密结果为字节数组
	 * 
	 * @param data
	 *            数据（不能为空）
	 * @param key
	 *            密钥（不能为空，长度为8）
	 * @return NULL(加密出错) 或 加密结果
	 * @throws IllegalArgumentException
	 *             数据为空 或 密钥长度错误
	 */
	public static byte[] encrypt(byte[] data, byte[] key) {
		if (data == null || data.length < 1) {
			throw new IllegalArgumentException("数据不能为空！");
		}
		if (key == null || key.length != DES_KEY_LENGTH) {
			throw new IllegalArgumentException("密钥必须为8个字节！");
		}

		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, ALGORITHM));
			return cipher.doFinal(data);
		} catch (Exception e) {
			logger.error("DES加密出错！exception: [" + e.getClass().getName() + ": " + e.getMessage() + "]");
			return null;
		}
	}

	/**
	 * DES解密<br>
	 * <br>
	 * ①使用UTF-8字符集<br>
	 * ②数据为Base64字符串<br>
	 * ③使用默认密钥<br>
	 * ④解密结果为UTF-8编码的字符串
	 * 
	 * @param data
	 *            数据（不能为空）
	 * @return 空字符串(参数为空或解密出错) 或 解密结果
	 * @throws IllegalArgumentException
	 *             数据不是Base64字符串
	 */
	public static String decryptBase64(String data) {
		if (data == null || data.isEmpty()) {
			return "";
		}

		return decryptBase64(data, DEFAULT_KEY);
	}

	/**
	 * DES解密<br>
	 * <br>
	 * ①使用UTF-8字符集<br>
	 * ②数据为Base64字符串<br>
	 * ③解密结果为UTF-8编码的字符串
	 * 
	 * @param data
	 *            数据（不能为空）
	 * @param key
	 *            密钥（为空：默认密钥）
	 * @return 空字符串(参数为空或解密出错) 或 解密结果
	 * @throws IllegalArgumentException
	 *             数据不是Base64字符串
	 */
	public static String decryptBase64(String data, String key) {
		if (data == null || data.isEmpty()) {
			return "";
		}

//		byte[] content = new sun.misc.BASE64Decoder().decodeBuffer(data);// Java 1.7及以下
		byte[] content = Base64.getDecoder().decode(data);// Java 1.8
		byte[] result = decrypt(content, desKey(key));
		if (result == null || result.length < 1) {
			return "";
		} else {
			return new String(result, CHARSET_UTF8);
		}
	}

	/**
	 * DES解密<br>
	 * <br>
	 * ①使用UTF-8字符集<br>
	 * ②数据为十六进制字符串<br>
	 * ③使用默认密钥<br>
	 * ④解密结果为UTF-8编码的字符串
	 * 
	 * @param data
	 *            数据（不能为空）
	 * @return 空字符串(参数为空或解密出错) 或 解密结果
	 */
	public static String decryptHex(String data) {
		if (data == null || data.isEmpty()) {
			return "";
		}

		return decryptHex(data, DEFAULT_KEY);
	}

	/**
	 * DES解密<br>
	 * <br>
	 * ①使用UTF-8字符集<br>
	 * ②数据为十六进制字符串<br>
	 * ③解密结果为UTF-8编码的字符串
	 * 
	 * @param data
	 *            数据（不能为空）
	 * @param key
	 *            密钥（为空：默认密钥）
	 * @return 空字符串(参数为空或解密出错) 或 解密结果
	 */
	public static String decryptHex(String data, String key) {
		if (data == null || data.isEmpty()) {
			return "";
		}

		byte[] result = decrypt(hexToByte(data), desKey(key));
		if (result == null || result.length < 1) {
			return "";
		} else {
			return new String(result, CHARSET_UTF8);
		}
	}

	/**
	 * DES解密<br>
	 * <br>
	 * ①密钥必须为8个字节<br>
	 * ②解密结果为字节数组
	 * 
	 * @param data
	 *            数据（不能为空）
	 * @param key
	 *            密钥（不能为空，长度为8）
	 * @return NULL(解密出错) 或 解密结果
	 * @throws IllegalArgumentException
	 *             数据为空 或 密钥长度错误
	 */
	public static byte[] decrypt(byte[] data, byte[] key) {
		if (data == null || data.length < 1) {
			throw new IllegalArgumentException("数据不能为空！");
		}
		if (key == null || key.length != DES_KEY_LENGTH) {
			throw new IllegalArgumentException("密钥必须为8个字节！");
		}

		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, ALGORITHM));
			return cipher.doFinal(data);
		} catch (Exception e) {
			logger.error("DES解密出错！exception: [" + e.getClass().getName() + ": " + e.getMessage() + "]");
			return null;
		}
	}

	/**
	 * DES密钥<br>
	 * <br>
	 * ①使用UTF-8字符集<br>
	 * ②密钥为8字节数组
	 * 
	 * @param key
	 *            密钥（为空：默认密钥）
	 * @return 密钥(字节数组)
	 */
	private static byte[] desKey(String key) {
		if (key == null || key.isEmpty()) {
			key = DEFAULT_KEY;
		}

		/* DES密钥长度为8，少了报错，多了无用 */
		// 方法一：将密钥转换为字节数组并根据需要填充或截取（不推荐！！！）
//		byte[] byteKey = new byte[DES_KEY_LENGTH];
//		byte[] temp = key.getBytes(CHARSET_UTF8);
//		System.arraycopy(temp, 0, byteKey, 0, temp.length < byteKey.length ? temp.length : byteKey.length);
//		return byteKey;
		// 方法二：以密钥为种子使用KeyGenerator生成固定长度的Key
//		KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
//		keyGenerator.init(new SecureRandom(key.getBytes(CHARSET_UTF8)));
//		return keyGenerator.generateKey().getEncoded();
		// 方法三：以密钥为基础生成固定长度的Key
		return MessageDigestUtil.md5Half(key);
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

	/**
	 * 十六进制字符串 -> byte[]
	 * 
	 * @param hex
	 *            十六进制字符串（不能为空）
	 * @return NULL(参数为空) 或 字节数组
	 */
	private static byte[] hexToByte(String hex) {
		if (hex == null || hex.isEmpty()) {
			return null;
		}
		if (hex.length() % 2 != 0) {
			hex = "0" + hex;
		}

		byte[] data = new byte[hex.length() / 2];
		int j = 0, high = 0, low = 0;
		for (int i = 0; i < data.length; i++) {
			high = Character.digit(hex.charAt(j), 16) & 0xFF;
			low = Character.digit(hex.charAt(j + 1), 16) & 0xFF;
			data[i] = (byte) (high << 4 | low);
			j += 2;
		}
		return data;
	}

	public static void main(String[] args) throws Exception {
		String key = null, data = null, result = null;
		data = "ABCDEFGH12345678";
		System.out.println("------------------ Base64 ------------------");
		result = encryptBase64(data, key);
		System.out.println("加密：" + result);
		result = decryptBase64(result, key);
		System.out.println("解密：" + result);
		System.out.println("------------------ Hex ------------------");
		result = encryptHex(data, key);
		System.out.println("加密：" + result);
		result = decryptHex(result, key);
		System.out.println("解密：" + result);
	}

}
