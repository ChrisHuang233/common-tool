package com.huangwei.crypto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Base64;

/**
 * AES加解密工具<br>
 * <br>
 * ①块大小: 128比特(16字节)<br>
 * ②密钥长度: 128比特(16字节)，192比特(24字节)，256比特(32字节)<br>
 * ③工作模式<br>
 * -- ECB模式: 电码本(Electronic Code
 * Book)模式。这是最简单的块密码加密模式，加密前根据加密块大小（如AES为128位）分成若干块，之后将每块使用相同的密钥单独加密，解密同理。<br>
 * -- CBC模式: 密文分组链接(Cipher Block
 * Chaining)模式。在这种加密模式中，每个密文块都依赖于它前面的所有明文块。同时，为了保证每条消息的唯一性，在第一个块中需要使用初始化向量IV。<br>
 * -- CTR模式: 计数器(Counter)模式。<br>
 * -- CFB模式: 密文反馈(Cipher Feedback)模式。<br>
 * -- OFB模式: 输出反馈(Output Feedback)模式。<br>
 * ④填充方式<br>
 * -- ZeroPadding: 数据未对齐时使用0填充，否则不填充。缺点：当原始数据尾部存在0时无法区分真实数据与填充数据。<br>
 * -- PKCS7Padding:
 * 未对齐，需要补充的字节个数为N，则填充一个长度为N且每个字节均为N的数据；已对齐，填充一个长度为块大小且每个字节均为块大小的数据。<br>
 * -- PKCS5Padding: PKCS7Padding的子集，块大小固定为8字节。<br>
 * -- 注意: Java标准库仅支持PKCS5Padding，如果需要PKCS7Padding，请使用第三方组件BouncyCastle。<br>
 */
public class AESUtil {
	protected static Logger logger = LoggerFactory.getLogger(AESUtil.class);

	/** 字符编码集 - UTF-8 */
	private static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");
	/** 加密算法 - AES */
	private static final String ALGORITHM = "AES";
	/** 算法/工作模式/填充方式 */
	private static final String AES_ECB_PKCS5 = "AES/ECB/PKCS5Padding";
	/** 算法/工作模式/填充方式 */
	private static final String AES_ECB_NOPADDING = "AES/ECB/NoPadding";
	/** 算法/工作模式/填充方式 */
	private static final String AES_CBC_PKCS5 = "AES/CBC/PKCS5Padding";
	/** (AES算法)允许的密钥大小(比特位数) */
	private static final int[] POSSIBLE_KEY_SIZE = { 128, 192, 256 };
	/** (AES算法)允许的密钥长度(字节数) */
	private static final int[] POSSIBLE_KEY_LENGTH = { 16, 24, 32 };
	/** AES初始向量字节数 */
	private static final int IV_LENGTH = 16;
	/** 默认密钥 */
	private static final String DEFAULT_KEY = "Hello, 世界！";
	/** 默认密钥大小(比特位数) */
	private static final int DEFAULT_KEY_SIZE = 256;
	/** 默认初始向量 */
	private static final String DEFAULT_IV = "你好, World！";

	/**
	 * AES加密<br>
	 * <br>
	 * ①算法/工作模式/填充方式：AES/ECB/PKCS5Padding<br>
	 * ②密钥大小：256比特(32字节)<br>
	 * ③使用UTF-8字符集<br>
	 * ④使用默认密钥<br>
	 * ⑤加密结果为Base64字符串，不含回车和换行(\r, \n)
	 * 
	 * @param data 数据（不能为空）
	 * @return 空字符串(参数为空或加密出错) 或 加密结果
	 */
	public static String ecbEncryptBase64(String data) {
		if (data == null || data.isEmpty()) {
			return "";
		}

		return ecbEncryptBase64(data, DEFAULT_KEY);
	}

	/**
	 * AES加密<br>
	 * <br>
	 * ①算法/工作模式/填充方式：AES/ECB/PKCS5Padding<br>
	 * ②密钥大小：256比特(32字节)<br>
	 * ③使用UTF-8字符集<br>
	 * ④加密结果为Base64字符串，不含回车和换行(\r, \n)
	 * 
	 * @param data 数据（不能为空）
	 * @param key  密钥（为空：默认密钥）
	 * @return 空字符串(参数为空或加密出错) 或 加密结果
	 */
	public static String ecbEncryptBase64(String data, String key) {
		if (data == null || data.isEmpty()) {
			return "";
		}

		byte[] result = ecbEncrypt(data.getBytes(CHARSET_UTF8), aesKey(key));
		if (result == null || result.length < 1) {
			return "";
		} else {
//			return new sun.misc.BASE64Encoder().encode(result).replaceAll("\r|\n", "");// Java 1.7及以下
			return Base64.getEncoder().encodeToString(result);// Java 1.8
		}
	}

	/**
	 * AES加密<br>
	 * <br>
	 * ①算法/工作模式/填充方式：AES/ECB/PKCS5Padding<br>
	 * ②密钥大小：256比特(32字节)<br>
	 * ③使用UTF-8字符集<br>
	 * ④使用默认密钥<br>
	 * ⑤加密结果为小写十六进制字符串
	 * 
	 * @param data 数据（不能为空）
	 * @return 空字符串(参数为空或加密出错) 或 加密结果
	 */
	public static String ecbEncryptHex(String data) {
		if (data == null || data.isEmpty()) {
			return "";
		}

		return ecbEncryptHex(data, DEFAULT_KEY);
	}

	/**
	 * AES加密<br>
	 * <br>
	 * ①算法/工作模式/填充方式：AES/ECB/PKCS5Padding<br>
	 * ②密钥大小：256比特(32字节)<br>
	 * ③使用UTF-8字符集<br>
	 * ④加密结果为小写十六进制字符串
	 * 
	 * @param data 数据（不能为空）
	 * @param key  密钥（为空：默认密钥）
	 * @return 空字符串(参数为空或加密出错) 或 加密结果
	 */
	public static String ecbEncryptHex(String data, String key) {
		if (data == null || data.isEmpty()) {
			return "";
		}

		byte[] result = ecbEncrypt(data.getBytes(CHARSET_UTF8), aesKey(key));
		if (result == null || result.length < 1) {
			return "";
		} else {
			return byteToHex(result);
		}
	}

	/**
	 * AES加密<br>
	 * <br>
	 * ①算法/工作模式/填充方式：AES/ECB/PKCS5Padding<br>
	 * ②允许的密钥长度(字节数)：16, 24, 32<br>
	 * ③加密结果为字节数组
	 * 
	 * @param data 数据（不能为空）
	 * @param key  密钥（不能为空，长度为16/24/32）
	 * @return NULL(加密出错) 或 加密结果
	 * @throws IllegalArgumentException 数据为空 或 密钥长度错误
	 */
	public static byte[] ecbEncrypt(byte[] data, byte[] key) {
		if (data == null || data.length < 1) {
			throw new IllegalArgumentException("数据不能为空！");
		}
		if (key == null || Arrays.binarySearch(POSSIBLE_KEY_LENGTH, key.length) < 0) {
			throw new IllegalArgumentException("密钥长度不符合要求！允许的值：" + Arrays.toString(POSSIBLE_KEY_LENGTH));
		}

		try {
			/* Cipher.getInstance("AES") 与 Cipher.getInstance("AES/ECB/PKCS5Padding") 等效 */
			Cipher cipher = Cipher.getInstance(AES_ECB_PKCS5);
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, ALGORITHM));
			return cipher.doFinal(data);
		} catch (Exception e) {
			logger.error("AES加密出错！exception: [" + e.getClass().getName() + ": " + e.getMessage() + "]");
			return null;
		}
	}

	/**
	 * AES解密<br>
	 * <br>
	 * ①算法/工作模式/填充方式：AES/ECB/PKCS5Padding<br>
	 * ②密钥大小：256比特(32字节)<br>
	 * ③使用UTF-8字符集<br>
	 * ④数据为Base64字符串<br>
	 * ⑤使用默认密钥<br>
	 * ⑥解密结果为UTF-8编码的字符串
	 * 
	 * @param data 数据（不能为空）
	 * @return 空字符串(参数为空或解密出错) 或 解密结果
	 * @throws IllegalArgumentException 数据不是Base64字符串
	 */
	public static String ecbDecryptBase64(String data) {
		if (data == null || data.isEmpty()) {
			return "";
		}

		return ecbDecryptBase64(data, DEFAULT_KEY);
	}

	/**
	 * AES解密<br>
	 * <br>
	 * ①算法/工作模式/填充方式：AES/ECB/PKCS5Padding<br>
	 * ②密钥大小：256比特(32字节)<br>
	 * ③使用UTF-8字符集<br>
	 * ④数据为Base64字符串<br>
	 * ⑤解密结果为UTF-8编码的字符串
	 * 
	 * @param data 数据（不能为空）
	 * @param key  密钥（为空：默认密钥）
	 * @return 空字符串(参数为空或解密出错) 或 解密结果
	 * @throws IllegalArgumentException 数据不是Base64字符串
	 */
	public static String ecbDecryptBase64(String data, String key) {
		if (data == null || data.isEmpty()) {
			return "";
		}

//		byte[] content = new sun.misc.BASE64Decoder().decodeBuffer(data);// Java 1.7及以下
		byte[] content = Base64.getDecoder().decode(data);// Java 1.8
		byte[] result = ecbDecrypt(content, aesKey(key));
		if (result == null || result.length < 1) {
			return "";
		} else {
			return new String(result, CHARSET_UTF8);
		}
	}

	/**
	 * AES解密<br>
	 * <br>
	 * ①算法/工作模式/填充方式：AES/ECB/PKCS5Padding<br>
	 * ②密钥大小：256比特(32字节)<br>
	 * ③使用UTF-8字符集<br>
	 * ④数据为十六进制字符串<br>
	 * ⑤使用默认密钥<br>
	 * ⑥解密结果为UTF-8编码的字符串
	 * 
	 * @param data 数据（不能为空）
	 * @return 空字符串(参数为空或解密出错) 或 解密结果
	 */
	public static String ecbDecryptHex(String data) {
		if (data == null || data.isEmpty()) {
			return "";
		}

		return ecbDecryptHex(data, DEFAULT_KEY);
	}

	/**
	 * AES解密<br>
	 * <br>
	 * ①算法/工作模式/填充方式：AES/ECB/PKCS5Padding<br>
	 * ②密钥大小：256比特(32字节)<br>
	 * ③使用UTF-8字符集<br>
	 * ④数据为十六进制字符串<br>
	 * ⑤解密结果为UTF-8编码的字符串<br>
	 * 
	 * @param data 数据（不能为空）
	 * @param key  密钥（为空：默认密钥）
	 * @return 空字符串(参数为空或解密出错) 或 解密结果
	 */
	public static String ecbDecryptHex(String data, String key) {
		if (data == null || data.isEmpty()) {
			return "";
		}

		byte[] result = ecbDecrypt(hexToByte(data), aesKey(key));
		if (result == null || result.length < 1) {
			return "";
		} else {
			return new String(result, CHARSET_UTF8);
		}
	}

	/**
	 * AES解密<br>
	 * <br>
	 * ①算法/工作模式/填充方式：AES/ECB/PKCS5Padding<br>
	 * ②允许的密钥长度(字节数)：16, 24, 32<br>
	 * ③解密结果为字节数组
	 * 
	 * @param data 数据（不能为空）
	 * @param key  密钥（不能为空，长度为16/24/32）
	 * @return NULL(解密出错) 或 解密结果
	 * @throws IllegalArgumentException 数据为空 或 密钥长度错误
	 */
	public static byte[] ecbDecrypt(byte[] data, byte[] key) {
		if (data == null || data.length < 1) {
			throw new IllegalArgumentException("数据不能为空！");
		}
		if (key == null || Arrays.binarySearch(POSSIBLE_KEY_LENGTH, key.length) < 0) {
			throw new IllegalArgumentException("密钥长度不符合要求！允许的值：" + Arrays.toString(POSSIBLE_KEY_LENGTH));
		}

		try {
			/* Cipher.getInstance("AES") 与 Cipher.getInstance("AES/ECB/PKCS5Padding") 等效 */
			Cipher cipher = Cipher.getInstance(AES_ECB_PKCS5);
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, ALGORITHM));
			return cipher.doFinal(data);
		} catch (Exception e) {
			logger.error("AES解密出错！exception: [" + e.getClass().getName() + ": " + e.getMessage() + "]");
			return null;
		}
	}

	/**
	 * AES解密<br>
	 * <br>
	 * ①算法/工作模式/填充方式：AES/ECB/NoPadding<br>
	 * ②允许的密钥长度(字节数)：16, 24, 32<br>
	 * ③解密结果为字节数组
	 * 
	 * @param data 数据（不能为空）
	 * @param key  密钥（不能为空，长度为16/24/32）
	 * @return NULL(解密出错) 或 解密结果
	 * @throws IllegalArgumentException 数据为空 或 密钥长度错误
	 */
	public static byte[] ecbNoPaddingDecrypt(byte[] data, byte[] key) {
		if (data == null || data.length < 1) {
			throw new IllegalArgumentException("数据不能为空！");
		}
		if (key == null || Arrays.binarySearch(POSSIBLE_KEY_LENGTH, key.length) < 0) {
			throw new IllegalArgumentException("密钥长度不符合要求！允许的值：" + Arrays.toString(POSSIBLE_KEY_LENGTH));
		}
		int blockSize = 16;
		int n = ((data.length + blockSize - 1) / blockSize) * blockSize;
		byte[] newArr = new byte[n];
		System.arraycopy(data, 0, newArr, 0, data.length);
		try {
			/* Cipher.getInstance("AES") 与 Cipher.getInstance("AES/ECB/NoPadding") 等效 */
			Cipher cipher = Cipher.getInstance(AES_ECB_NOPADDING);
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, ALGORITHM));
			return cipher.doFinal(newArr);
		} catch (Exception e) {
			logger.error("AES解密出错！exception: [" + e.getClass().getName() + ": " + e.getMessage() + "]");
			return null;
		}
	}

	/**
	 * AES加密<br>
	 * <br>
	 * ①算法/工作模式/填充方式：AES/CBC/PKCS5Padding<br>
	 * ②允许的密钥长度(字节数)：16, 24, 32<br>
	 * ③初始向量必须为16字节<br>
	 * ④加密结果为字节数组
	 * 
	 * @param data 数据（不能为空）
	 * @param key  密钥（不能为空，长度为16/24/32）
	 * @param iv   初始向量（不能为空，长度为16）
	 * @return NULL(加密出错) 或 加密结果
	 * @throws IllegalArgumentException 数据为空 或 密钥长度错误 或 初始向量长度错误
	 */
	public static byte[] cbcEncrypt(byte[] data, byte[] key, byte[] iv) {
		if (data == null || data.length < 1) {
			throw new IllegalArgumentException("数据不能为空！");
		}
		if (key == null || Arrays.binarySearch(POSSIBLE_KEY_LENGTH, key.length) < 0) {
			throw new IllegalArgumentException("密钥长度不符合要求！允许的值：" + Arrays.toString(POSSIBLE_KEY_LENGTH));
		}
		if (iv == null || iv.length != IV_LENGTH) {
			throw new IllegalArgumentException("初始向量必须为16个字节！");
		}

		try {
			Cipher cipher = Cipher.getInstance(AES_CBC_PKCS5);
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, ALGORITHM), new IvParameterSpec(iv));
			return cipher.doFinal(data);
		} catch (Exception e) {
			logger.error("AES加密出错！exception: [" + e.getClass().getName() + ": " + e.getMessage() + "]");
			return null;
		}
	}

	/**
	 * AES解密<br>
	 * <br>
	 * ①算法/工作模式/填充方式：AES/CBC/PKCS5Padding<br>
	 * ②允许的密钥长度(字节数)：16, 24, 32<br>
	 * ③初始向量必须为16字节<br>
	 * ④解密结果为字节数组
	 * 
	 * @param data 数据（不能为空）
	 * @param key  密钥（不能为空，长度为16/24/32）
	 * @param iv   初始向量（不能为空，长度为16）
	 * @return NULL(解密出错) 或 解密结果
	 * @throws IllegalArgumentException 数据为空 或 密钥长度错误
	 */
	public static byte[] cbcDecrypt(byte[] data, byte[] key, byte[] iv) {
		if (data == null || data.length < 1) {
			throw new IllegalArgumentException("数据不能为空！");
		}
		if (key == null || Arrays.binarySearch(POSSIBLE_KEY_LENGTH, key.length) < 0) {
			throw new IllegalArgumentException("密钥长度不符合要求！允许的值：" + Arrays.toString(POSSIBLE_KEY_LENGTH));
		}
		if (iv == null || iv.length != IV_LENGTH) {
			throw new IllegalArgumentException("初始向量必须为16个字节！");
		}

		try {
			Cipher cipher = Cipher.getInstance(AES_CBC_PKCS5);
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, ALGORITHM), new IvParameterSpec(iv));
			return cipher.doFinal(data);
		} catch (Exception e) {
			logger.error("AES解密出错！exception: [" + e.getClass().getName() + ": " + e.getMessage() + "]");
			return null;
		}
	}

	/**
	 * AES密钥<br>
	 * <br>
	 * ①使用UTF-8字符集<br>
	 * ②密钥大小：256比特(32字节)
	 * 
	 * @param key 密钥（为空：默认密钥）
	 * @return 密钥(字节数组)
	 */
	private static byte[] aesKey(String key) {
		return aesKey(key, DEFAULT_KEY_SIZE);
	}

	/**
	 * AES密钥<br>
	 * <br>
	 * ①使用UTF-8字符集<br>
	 * ②返回指定大小的字节密钥
	 * 
	 * @param key     密钥（为空：默认密钥）
	 * @param keySize 密钥大小（128比特(16字节)/192比特(24字节)/256比特(32字节)）
	 * @return 密钥(字节数组)
	 */
	public static byte[] aesKey(String key, int keySize) {
		if (key == null || key.isEmpty()) {
			key = DEFAULT_KEY;
		}
		if (Arrays.binarySearch(POSSIBLE_KEY_SIZE, keySize) < 0) {
			keySize = DEFAULT_KEY_SIZE;
		}
		// 方法一：将密钥转换为字节数组并根据需要填充或截取（不推荐！！！）
//		byte[] byteKey = new byte[keySize / 8];
//		byte[] temp = key.getBytes(CHARSET_UTF8);
//		System.arraycopy(temp, 0, byteKey, 0, temp.length < byteKey.length ? temp.length : byteKey.length);
//		return byteKey;
		// 方法二：以密钥为种子使用KeyGenerator生成固定长度的Key
//		KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
//		keyGenerator.init(keySize, new SecureRandom(key.getBytes(CHARSET_UTF8)));
//		return keyGenerator.generateKey().getEncoded();
		// 方法三：以密钥为基础生成固定长度的Key
		if (keySize == 128) {// 密钥长度：128比特(16字节)
			return MessageDigestUtil.md5(key);
		} else if (keySize == 192) {// 密钥长度：192比特(24字节)
			return MessageDigestUtil.sha384Half(key);
		} else {// 密钥长度：256比特(32字节)
			return MessageDigestUtil.sha256(key);
		}
	}

	/**
	 * AES初始向量<br>
	 * <br>
	 * ①使用UTF-8字符集<br>
	 * ②初始向量为16字节
	 * 
	 * @param iv 初始向量（为空：默认向量）
	 * @return 初始向量(字节数组)
	 */
	private static byte[] aesIv(String iv) {
		if (iv == null || iv.isEmpty()) {
			iv = DEFAULT_IV;
		}
		// 方法一：将初始向量转换为字节数组并根据需要填充或截取（不推荐！！！）
//		byte[] byteIv = new byte[IV_LENGTH];
//		byte[] temp = iv.getBytes(CHARSET_UTF8);
//		System.arraycopy(temp, 0, byteIv, 0, temp.length < byteIv.length ? temp.length : byteIv.length);
//		return byteIv;
		// 方法二：以初始向量为种子使用KeyGenerator生成128比特(16字节)的密钥当作IV
//		KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
//		keyGenerator.init(128, new SecureRandom(iv.getBytes(CHARSET_UTF8)));
//		return keyGenerator.generateKey().getEncoded();
		// 方法三：以初始向量为基础生成固定长度的IV
		return MessageDigestUtil.md5(iv);
	}

	/**
	 * byte[] -> 小写十六进制字符串
	 * 
	 * @param data 数据（不能为空）
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
	 * @param hex 十六进制字符串（不能为空）
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

	public static void main(String[] args) {
		String key = null, iv = null, data = null, result = null;
		byte[] byteKey = null, byteIv = null, byteData = null, byteResult = null;
		iv = "5678ABCD";
		data = "ABCDEFGH12345678";
		byteKey = aesKey(key);
		byteIv = aesIv(iv);
		byteData = data.getBytes(CHARSET_UTF8);
		System.out.println("------------------ AES/ECB/PKCS5 - Base64 ------------------");
		result = ecbEncryptBase64(data);
		System.out.println("加密：" + result);
		result = ecbDecryptBase64(result, key);
		System.out.println("解密：" + result);
		System.out.println("------------------ AES/ECB/PKCS5 - Hex ------------------");
		result = ecbEncryptHex(data, key);
		System.out.println("加密：" + result);
		result = ecbDecryptHex(result, key);
		System.out.println("解密：" + result);
		System.out.println("------------------ AES/CBC/PKCS5 - Hex ------------------");
		byteResult = cbcEncrypt(byteData, byteKey, byteIv);
		System.out.println("加密：" + byteToHex(byteResult));
		byteResult = cbcDecrypt(byteResult, byteKey, byteIv);
		System.out.println("解密：" + new String(byteResult, CHARSET_UTF8));
	}

}
