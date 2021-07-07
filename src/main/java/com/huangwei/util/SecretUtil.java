package com.huangwei.util;

import com.huangwei.crypto.Base64Util;
import org.apache.commons.lang.RandomStringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 加密工具类
 */
public class SecretUtil {
	/**
	 * 加解密算法/工作模式/填充方式
	 */
	private static final String ALGORITHM_MODE_PADDING = "AES/ECB/PKCS7Padding";

	/**
	 * 加解密算法/工作模式/填充方式
	 */
	private static final String ALGORITHM_MODE_PADDING_PKCS5 = "AES/ECB/PKCS5Padding";

	/**
	 * 密钥算法
	 */
	private static final String ALGORITHM = "AES";

	/**
	 * 利用key对data进行AES-256-ECB解密
	 *
	 * @param data
	 *            加密数据
	 * @param key
	 *            密钥
	 * @return 解密信息
	 * @throws Exception
	 *             异常
	 */
	public static String decryptData(String data, String key) throws Exception {

		if (data == null || "".equals(data.trim())) {
			return null;
		}
		if (key == null || "".equals(key.trim())) {
			return null;
		}

		// 1.对加密串reqInfo做base64解码，得到加密串decodeData
		data = data.replace("\n", "").replace("\r", "");
		byte[] decodeData = Base64.getDecoder().decode(data);

		// 2 对商户Key做md5，得到32位小写key
		String md5key = MD5Util.lowercase(key);

		// 3.用key对加密串decodeData做AES-256-ECB解密
		SecretKeySpec secretKeySpec = new SecretKeySpec(md5key.getBytes(), ALGORITHM);
		Security.addProvider(new BouncyCastleProvider());
		Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_PADDING, "BC");
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
		return new String(cipher.doFinal(decodeData));
	}

	/**
	 * 利用key对data进行AES-256-ECB加密
	 *
	 * @param data
	 *            加密数据
	 * @param key
	 *            密钥
	 * @return 加密信息
	 * @throws Exception
	 *             异常
	 */
	public static String encryptData(String data, String key) throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		SecretKeySpec secretKeySpec = new SecretKeySpec(MD5Util.lowercase(key).getBytes(), ALGORITHM);
		// 创建密码器
		Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_PADDING, "BC");
		// 初始化
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
		byte[] bytes = cipher.doFinal(data.getBytes());
		byte[] result = Base64.getEncoder().encode(bytes);
		return new String(result);
	}

	/**
	 * 利用key对data进行AES-256-ECB加密
	 *
	 * @param data
	 *            加密数据
	 * @param key
	 *            密钥
	 * @return 加密信息
	 * @throws Exception
	 *             异常
	 */
	public static String encryptData2(String data, String key) throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), ALGORITHM);
		// 创建密码器
		Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_PADDING, "BC");
		// 初始化
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
		byte[] bytes = cipher.doFinal(data.getBytes());
		byte[] result = Base64.getEncoder().encode(bytes);
		return new String(result);
	}

	/**
	 * 利用key对data进行128比特AES/ECB/PKCS5Padding加密
	 *
	 * @param data
	 *            数据
	 * @param key
	 *            密钥
	 * @return 加密信息（Base64字符串）
	 * @throws Exception
	 *             异常
	 */
	public static String aesEncryptToBytes(String data, String key) throws Exception {
		String encryptKey = MD5Util.half(key);
		Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_PADDING_PKCS5);
		cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), ALGORITHM));
		byte[] encrypt = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
		return Base64.getEncoder().encodeToString(encrypt);
	}

	/**
	 * 利用key对data进行128比特AES/ECB/PKCS5Padding解密
	 *
	 * @param data
	 *            加密信息（Base64字符串）
	 * @param key
	 *            密钥
	 * @return 数据
	 * @throws Exception
	 *             异常
	 */
	public static String aesDecryptByBytes(String data, String key) throws Exception {
		byte[] encryptBytes = Base64.getDecoder().decode(data.replaceAll("\r|\n", ""));
		String decryptKey = MD5Util.half(key);
		Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_PADDING_PKCS5);
		cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), ALGORITHM));
		byte[] decryptBytes = cipher.doFinal(encryptBytes);
		return new String(decryptBytes, StandardCharsets.UTF_8);
	}

	/**
	 * 对token信息进行加盐加密处理
	 *
	 * @param token
	 *            token信息
	 * @param key
	 *            密钥
	 * @return 加密信息 或者 null
	 * @throws Exception
	 *             异常
	 */
	public static String encryptToken(String token, String key) throws Exception {
		if (StringUtil.isBlank(token) || StringUtil.isBlank(key)) {
			return null;
		}

		return encryptData(token + RandomStringUtils.random(6, true, true), key);
	}

	/**
	 * 对加密的token信息进行解密去盐处理
	 *
	 * @param data
	 *            加密信息
	 * @param key
	 *            密钥
	 * @return token 或者 null
	 * @throws Exception
	 *             异常
	 */
	public static String decryptToken(String data, String key) throws Exception {
		if (StringUtil.isBlank(data) || StringUtil.isBlank(key)) {
			return null;
		}

		String decryptData = decryptData(data, key);

		if (decryptData.length() < 6) {
			return null;
		}
		return decryptData.substring(0, decryptData.length() - 6);
	}

	/**
	 * AES密钥生成器
	 *
	 * @param length 密钥长度 (单位:bit)
	 * @return 生成的密钥
	 */
	public static String keygen(Integer length) {
		length = length / 8;
		List<Character> token = new ArrayList<>();
		for (int i = 0; i < length; ++i) {
			int rand = ThreadLocalRandom.current().ints(32, 1025).limit(1).findFirst().orElse(0);
			token.add((char) rand);
		}
		Collections.shuffle(token);
		StringBuilder str = new StringBuilder();
		for (Character character : token) {
			str.append(character);
		}
		String keygen = Base64Util.encode(str.toString().getBytes());

		return keygen.substring(0, length);
	}

}
