package com.huangwei.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 字节数组工具
 */
public class ByteUtil {

	/**
	 * byte[] -> integer<br>
	 * <br>
	 * byte[]: 高位在前，低位在后
	 * 
	 * @param b
	 *            byte数组（长度：1-4）
	 * @return 整数
	 * @throws IllegalArgumentException
	 *             数组为空
	 */
	public static int byteToInt(byte[] b) {
		if (b == null || b.length < 1) {
			throw new IllegalArgumentException("数组不能为空！");
		}

		int n = 0;
		for (int i = 0; i < b.length; i++) {
			n += (b[i] & 0xFF) << (8 * (b.length - 1 - i));
		}
		return n;
	}

	/**
	 * byte[] -> integer<br>
	 * <br>
	 * byte[]: 低位在前，高位在后
	 * 
	 * @param b
	 *            byte数组（长度：1-4）
	 * @return 整数
	 * @throws IllegalArgumentException
	 *             数组为空
	 */
	public static int byte2Int(byte[] b) {
		if (b == null || b.length < 1) {
			throw new IllegalArgumentException("数组不能为空！");
		}

		int n = 0;
		for (int i = 0; i < b.length; i++) {
			n += (b[i] & 0xFF) << (8 * i);
		}
		return n;
	}

	/**
	 * byte[] -> integer<br>
	 * <br>
	 * byte[]: 低位在前，高位在后；读取4个字节<br>
	 * 
	 * @param b
	 *            byte数组
	 * @param offset
	 *            偏移量（从0开始）
	 * @return 整数
	 * @throws IllegalArgumentException
	 *             数组为空
	 * @throws ArrayIndexOutOfBoundsException
	 *             偏移量为负 或 偏移量越界
	 */
	public static int byte2Int(byte[] b, int offset) {
		return byte2Int(b, offset, 4);
	}

	/**
	 * byte[] -> integer<br>
	 * <br>
	 * byte[]: 低位在前，高位在后
	 * 
	 * @param b
	 *            byte数组
	 * @param offset
	 *            偏移量（从0开始）
	 * @param length
	 *            长度（大于零）
	 * @return 整数
	 * @throws IllegalArgumentException
	 *             数组为空 或 长度无效
	 * @throws ArrayIndexOutOfBoundsException
	 *             偏移量为负 或 偏移量越界
	 */
	public static int byte2Int(byte[] b, int offset, int length) {
		if (b == null || b.length < 1) {
			throw new IllegalArgumentException("数组不能为空！");
		}
		if (length < 1) {
			throw new IllegalArgumentException("长度必须大于零！");
		}
		if (offset < 0) {
			throw new ArrayIndexOutOfBoundsException("偏移量不能为负！");
		}
		if (offset + 1 > b.length) {
			throw new ArrayIndexOutOfBoundsException("偏移量越界！最大值：" + (b.length - 1) + " 当前值：" + offset);
		}
		if (offset + length > b.length) {// 修正长度
			length = b.length - offset;
		}
		if (length > 4) {// integer最多4个字节
			length = 4;
		}

		int n = 0;
		for (int i = 0; i < length; i++) {
			n += (b[offset + i] & 0xFF) << (8 * i);
		}
		return n;
	}

	/**
	 * integer -> byte[4]<br>
	 * <br>
	 * byte[]: 高位在前，低位在后
	 * 
	 * @param n
	 *            整数
	 * @return byte[4]
	 */
	public static byte[] intToByte(int n) {
		byte[] b = new byte[4];
		for (int i = 0; i < b.length; i++) {
			b[i] = (byte) ((n >> ((3 - i) * 8)) & 0xFF);
		}
		return b;
	}

	/**
	 * integer -> byte[4]<br>
	 * <br>
	 * byte[]: 低位在前，高位在后
	 * 
	 * @param n
	 *            integer
	 * @return byte[4]
	 */
	public static byte[] int2Byte(int n) {
		byte[] b = new byte[4];
		for (int i = 0; i < b.length; i++) {
			b[i] = (byte) ((n >> (i * 8)) & 0xFF);
		}
		return b;
	}

	/**
	 * byte[] -> long<br>
	 * <br>
	 * byte[]: 高位在前，低位在后
	 * 
	 * @param b
	 *            byte数组（长度：1-8）
	 * @return 整数
	 * @throws IllegalArgumentException
	 *             数组为空
	 */
	public static long byteToLong(byte[] b) {
		if (b == null || b.length < 1) {
			throw new IllegalArgumentException("数组不能为空！");
		}

		int length = Math.min(b.length, 8);// long最多8个字节
		long n = 0;
		for (int i = 0; i < length; i++) {
			n += ((long) (b[i] & 0xFF)) << (8 * (length - 1 - i));
		}
		return n;
	}

	/**
	 * byte[] -> long<br>
	 * <br>
	 * byte[]: 低位在前，高位在后
	 * 
	 * @param b
	 *            byte数组（长度：1-8）
	 * @return 整数
	 * @throws IllegalArgumentException
	 *             数组为空
	 */
	public static long byte2Long(byte[] b) {
		if (b == null || b.length < 1) {
			throw new IllegalArgumentException("数组不能为空！");
		}

		int length = Math.min(b.length, 8);// long最多8个字节
		long n = 0;
		for (int i = 0; i < length; i++) {
			n += ((long) (b[i] & 0xFF)) << (8 * i);
		}
		return n;
	}

	/**
	 * byte[] -> long<br>
	 * <br>
	 * byte[]: 低位在前，高位在后；读取8个字节<br>
	 * 
	 * @param b
	 *            byte数组
	 * @param offset
	 *            偏移量（从0开始）
	 * @return 整数
	 * @throws IllegalArgumentException
	 *             数组为空
	 * @throws ArrayIndexOutOfBoundsException
	 *             偏移量为负 或 偏移量越界
	 */
	public static long byte2Long(byte[] b, int offset) {
		return byte2Long(b, offset, 8);
	}

	/**
	 * byte[] -> long<br>
	 * <br>
	 * byte[]: 低位在前，高位在后
	 * 
	 * @param b
	 *            byte数组
	 * @param offset
	 *            偏移量（从0开始）
	 * @param length
	 *            长度（大于零）
	 * @return 整数
	 * @throws IllegalArgumentException
	 *             数组为空 或 长度无效
	 * @throws ArrayIndexOutOfBoundsException
	 *             偏移量为负 或 偏移量越界
	 */
	public static long byte2Long(byte[] b, int offset, int length) {
		if (b == null || b.length < 1) {
			throw new IllegalArgumentException("数组不能为空！");
		}
		if (length < 1) {
			throw new IllegalArgumentException("长度必须大于零！");
		}
		if (offset < 0) {
			throw new ArrayIndexOutOfBoundsException("偏移量不能为负！");
		}
		if (offset + 1 > b.length) {
			throw new ArrayIndexOutOfBoundsException("偏移量越界！最大值：" + (b.length - 1) + " 当前值：" + offset);
		}
		if (offset + length > b.length) {// 修正长度
			length = b.length - offset;
		}
		if (length > 8) {// long最多8个字节
			length = 8;
		}

		long n = 0;
		for (int i = 0; i < length; i++) {
			n += ((long) (b[offset + i] & 0xFF)) << (8 * i);
		}
		return n;
	}

	/**
	 * long -> byte[8]<br>
	 * <br>
	 * byte[]: 高位在前，低位在后
	 * 
	 * @param n
	 *            整数
	 * @return byte[8]
	 */
	public static byte[] longToByte(long n) {
		byte[] b = new byte[8];
		for (int i = 0; i < b.length; i++) {
			b[i] = (byte) ((n >> ((7 - i) * 8)) & 0xFF);
		}
		return b;
	}

	/**
	 * long -> byte[8]<br>
	 * <br>
	 * byte[]: 低位在前，高位在后
	 * 
	 * @param n
	 *            整数
	 * @return byte[8]
	 */
	public static byte[] long2Byte(long n) {
		byte[] b = new byte[8];
		for (int i = 0; i < b.length; i++) {
			b[i] = (byte) ((n >> (i * 8)) & 0xFF);
		}
		return b;
	}

	/**
	 * long -> byte[length]<br>
	 * <br>
	 * byte[]: 高位在前，低位在后
	 * 
	 * @param n
	 *            整数（byte/char/short/int/long）
	 * @param length
	 *            长度（大于零）
	 * @return byte[length]
	 */
	public static byte[] longToByte(long n, int length) {
		if (length < 1) {
			length = 8;
		}

		byte[] b = new byte[length];
		int i = length <= 8 ? 0 : (length - 8);
		for (; i < length; i++) {
			b[i] = (byte) ((n >> ((length - i - 1) * 8)) & 0xFF);
		}
		return b;
	}

	/**
	 * long -> byte[length]<br>
	 * <br>
	 * byte[]: 低位在前，高位在后
	 * 
	 * @param n
	 *            整数（byte/char/short/int/long）
	 * @param length
	 *            长度（大于零）
	 * @return byte[length]
	 */
	public static byte[] long2Byte(long n, int length) {
		if (length < 1) {
			length = 8;
		}

		byte[] b = new byte[length];
		if (length > 8) {// long最多8个字节
			length = 8;
		}
		for (int i = 0; i < length; i++) {
			b[i] = (byte) ((n >> (i * 8)) & 0xFF);
		}
		return b;
	}

	/**
	 * 异或运算
	 * 
	 * @param a
	 *            byte数组A（不能为空）
	 * @param b
	 *            byte数组B（不能为空）
	 * @return byte[MAX(a.length, b.length)]
	 * @throws NullPointerException
	 *             数组为空
	 */
	public static byte[] xor(byte[] a, byte[] b) {
		if (a == null || a.length < 1) {
			throw new NullPointerException("数组A不能为空！");
		}
		if (b == null || b.length < 1) {
			throw new NullPointerException("数组B不能为空！");
		}

		// 区分长短
		byte[] longer, shorter;
		if (a.length >= b.length) {
			longer = a;
			shorter = b;
		} else {
			longer = b;
			shorter = a;
		}

		byte[] xor = new byte[longer.length];
		int i = 0;
		for (; i < shorter.length; i++) {
			xor[i] = (byte) (shorter[i] ^ longer[i]);
		}
		for (; i < longer.length; i++) {
			xor[i] = longer[i];
		}
		return xor;
	}

	/**
	 * byte[] -> 16进制字符串（无分隔符；长度2N；大写；不带0x）
	 * 
	 * @param b
	 *            byte数组
	 * @return 空字符串（参数错误） 或 16进制字符串
	 */
	public static String toHexStr(byte[] b) {
		if (b == null || b.length < 1) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		String hexStr;
		for (int i = 0; i < b.length; i++) {
			hexStr = Integer.toHexString(b[i] & 0xFF);
			if (hexStr.length() == 1) {
				sb.append("0").append(hexStr);
			} else {
				sb.append(hexStr);
			}
		}
		return sb.toString().toUpperCase();
	}

	/**
	 * byte[] -> 16进制字符串（指定分隔符；大写；不带0x）
	 * 
	 * @param b
	 *            byte数组
	 * @param separator
	 *            分隔符（为空：不使用分隔符）
	 * @return 空字符串（参数错误） 或 16进制字符串
	 */
	public static String toHexStr(byte[] b, String separator) {
		if (b == null || b.length < 1) {
			return "";
		}
		if (separator == null || "".equals(separator)) {
			return toHexStr(b);
		}

		StringBuilder sb = new StringBuilder();
		String hexStr;
		for (int i = 0; i < b.length; i++) {
			if (i > 0) {
				sb.append(separator);
			}
			hexStr = Integer.toHexString(b[i] & 0xFF);
			if (hexStr.length() == 1) {
				sb.append("0").append(hexStr);
			} else {
				sb.append(hexStr);
			}
		}
		return sb.toString().toUpperCase();
	}

	/**
	 * byte[] -> 16进制字符串（无分隔符；长度2N；大写；不带0x）
	 * 
	 * @param b
	 *            byte数组
	 * @param offset
	 *            偏移量（从0开始）
	 * @param length
	 *            长度（大于零）
	 * @return 空字符串（参数错误） 或 16进制字符串
	 * @throws ArrayIndexOutOfBoundsException
	 *             偏移量为负 或 偏移量越界
	 */
	public static String toHexStr(byte[] b, int offset, int length) {
		if (b == null || b.length < 1 || length < 1) {
			return "";
		}
		if (offset < 0) {
			throw new ArrayIndexOutOfBoundsException("偏移量不能为负！");
		}
		if (offset + 1 > b.length) {
			throw new ArrayIndexOutOfBoundsException("偏移量越界！最大值：" + (b.length - 1) + " 当前值：" + offset);
		}
		if (offset + length > b.length) {// 修正长度
			length = b.length - offset;
		}

		byte[] temp = new byte[length];
		System.arraycopy(b, offset, temp, 0, length);
		return toHexStr(temp);
	}

	/**
	 * byte[] -> 16进制字符串（空格分隔；大写；不带0x）
	 * 
	 * @param b
	 *            byte数组
	 * @return 空字符串（参数错误） 或 16进制字符串
	 */
	public static String toHexString(byte[] b) {
		return toHexStr(b, " ");
	}

	/**
	 * 整数（byte/char/short/int/long） -> 16进制字符串（长度2；大写；不带0x）
	 * 
	 * @param n
	 *            整数（byte/char/short/int/long）
	 * @return 16进制字符串
	 */
	public static String toHexStr(long n) {
		String hexStr = Long.toHexString(n & 0xFF);
		if (hexStr.length() == 1) {
			hexStr = "0" + hexStr;
		}
		return hexStr.toUpperCase();
	}

	/**
	 * 整数（byte/char/short/int/long） -> 16进制字符串（指定长度；大写；不带0x）
	 * 
	 * @param n
	 *            整数（byte/char/short/int/long）
	 * @param length
	 *            输出长度（一般为：2, 4, 6, 8）
	 * @return 空字符串（参数错误） 或 16进制字符串
	 */
	public static String toHexStr(long n, int length) {
		if (length < 1) {
			return "";
		}
		if (n < 0 && n >= Integer.MIN_VALUE) {// Integer范围内的负数
			if (n >= Byte.MIN_VALUE) {
				n &= 0xFF;
			} else if (n >= Short.MIN_VALUE) {
				n &= 0xFFFF;
			} else {
				n &= 0xFFFFFFFFL;
			}
		}

		String hexStr = Long.toHexString(n);
		if (hexStr.length() < length) {
			hexStr = String.format("%" + length + "s", hexStr).replace(" ", "0");
		} else if (hexStr.length() > length) {
			hexStr = hexStr.substring(hexStr.length() - length);// 从后往前截取
		}
		return hexStr.toUpperCase();
	}

	/**
	 * 16进制字符串 -> byte[]
	 * 
	 * @param hex
	 *            16进制字符串
	 * @return byte[]
	 * @throws IllegalArgumentException
	 *             参数错误
	 */
	public static byte[] hexToByte(String hex) {
		if (hex == null || "".equals(hex = hex.trim()) || hex.matches("(?i)^0x$")) {
			throw new IllegalArgumentException("参数不能为空！");
		}
		if (hex.matches("(?i)^0x.*")) {
			hex = hex.substring(2);
		}
		if (hex.length() < 2) {
			hex = "0" + hex;
		} else if (hex.length() % 2 != 0) {// 长度为奇数
			if (hex.startsWith("0")) {// 以0开头
				if ((hex.length() / 2) % 2 != 0) {
					hex = "0" + hex;
				} else {
					hex = hex.substring(1);
				}
			} else {
				hex = "0" + hex;
			}
		}

		byte[] temp = new byte[hex.length() / 2];
		for (int i = 0; i < hex.length() / 2; i++) {
			temp[i] = (byte) (Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16) & 0xFF);
		}
		return temp;
	}

	/**
	 * byte[] -> String
	 * 
	 * @param b
	 *            byte数组（不能为空）
	 * @param charset
	 *            字符集（为空：使用默认字符集）
	 * @param terminator
	 *            结束符
	 * @return String（不为NULL）
	 * @throws IllegalArgumentException
	 *             数组为空
	 */
	public static String toStr(byte[] b, Charset charset, char terminator) {
		if (b == null || b.length < 1) {
			throw new IllegalArgumentException("数组不能为空！");
		}

		int length = b.length;
		for (int i = 0; i < b.length; i++) {
			if ((b[i] & 0xFF) == terminator) {
				length = i;
				break;
			}
		}
		if (length == 0) {
			return "";
		}

		byte[] temp;
		if (length == b.length) {
			temp = b;
		} else {
			temp = new byte[length];
			System.arraycopy(b, 0, temp, 0, temp.length);
		}

		if (charset == null) {
			return new String(temp);
		}

		return new String(temp, charset);
	}

	/**
	 * byte[] -> String
	 * 
	 * @param b
	 *            byte数组（不能为空）
	 * @param charset
	 *            字符集（为空：使用默认字符集）
	 * @param offset
	 *            偏移量（从0开始）
	 * @param length
	 *            长度（大于零）
	 * @param terminator
	 *            结束符
	 * @return String（不为NULL）
	 * @throws IllegalArgumentException
	 *             数组为空 或 长度错误
	 * @throws ArrayIndexOutOfBoundsException
	 *             偏移量为负 或 偏移量越界
	 */
	public static String toStr(byte[] b, Charset charset, int offset, int length, char terminator) {
		if (b == null || b.length < 1) {
			throw new IllegalArgumentException("数组不能为空！");
		}
		if (length < 1) {
			throw new IllegalArgumentException("长度错误！length:" + length);
		}
		if (offset < 0) {
			throw new ArrayIndexOutOfBoundsException("偏移量不能为负！");
		}
		if (offset + 1 > b.length) {
			throw new ArrayIndexOutOfBoundsException("偏移量越界！最大值：" + (b.length - 1) + " 当前值：" + offset);
		}
		if (offset + length > b.length) {// 修正长度
			length = b.length - offset;
		}

		for (int i = 0; i < length; i++) {
			if ((b[offset + i] & 0xFF) == terminator) {
				length = i;
				break;
			}
		}
		if (length == 0) {
			return "";
		}

		byte[] temp;
		if (offset == 0 && length == b.length) {
			temp = b;
		} else {
			temp = new byte[length];
			System.arraycopy(b, offset, temp, 0, temp.length);
		}

		if (charset == null) {
			return new String(temp);
		}

		return new String(temp, charset);
	}

	/**
	 * byte[] -> String（ASCII编码；以'\0'为结束符）
	 * 
	 * @param b
	 *            byte数组（不能为空）
	 * @return String（不为NULL）
	 */
	public static String toString(byte[] b) {
		if (b == null || b.length < 1) {
			throw new IllegalArgumentException("数组不能为空！");
		}

		return toStr(b, StandardCharsets.US_ASCII, '\0');
	}

	/**
	 * byte[] -> String（指定字符集；以'\0'为结束符）
	 * 
	 * @param b
	 *            byte数组（不能为空）
	 * @param charsetName
	 *            字符集名（为空：使用默认字符集）
	 * @return String（不为NULL）
	 * @throws IllegalArgumentException
	 *             数组为空
	 * @throws UnsupportedCharsetException
	 *             字符集不支持
	 */
	public static String toString(byte[] b, String charsetName) {
		if (b == null || b.length < 1) {
			throw new IllegalArgumentException("数组不能为空！");
		}

		if (charsetName == null || "".equals(charsetName = charsetName.trim())) {
			return toStr(b, null, '\0');
		}

		return toStr(b, Charset.forName(charsetName), '\0');
	}

	/**
	 * byte[] -> String（指定字符集；指定结束符）
	 * 
	 * @param b
	 *            byte数组（不能为空）
	 * @param charsetName
	 *            字符集名（为空：使用默认字符集）
	 * @param terminator
	 *            结束符
	 * @return String（不为NULL）
	 * @throws IllegalArgumentException
	 *             数组为空
	 * @throws UnsupportedCharsetException
	 *             字符集不支持
	 */
	public static String toString(byte[] b, String charsetName, char terminator) {
		if (b == null || b.length < 1) {
			throw new IllegalArgumentException("数组不能为空！");
		}

		if (charsetName == null || "".equals(charsetName = charsetName.trim())) {
			return toStr(b, null, terminator);
		}

		return toStr(b, Charset.forName(charsetName), terminator);
	}

	/**
	 * byte[] -> String（ASCII编码；以'\0'为结束符）
	 * 
	 * @param b
	 *            byte数组（不能为空）
	 * @param offset
	 *            偏移量（从0开始）
	 * @param length
	 *            长度（大于零）
	 * @return String（不为NULL）
	 * @throws IllegalArgumentException
	 *             数组为空 或 长度错误
	 * @throws ArrayIndexOutOfBoundsException
	 *             偏移量为负 或 偏移量越界
	 */
	public static String toString(byte[] b, int offset, int length) {
		if (b == null || b.length < 1) {
			throw new IllegalArgumentException("数组不能为空！");
		}
		if (length < 1) {
			throw new IllegalArgumentException("长度错误！length:" + length);
		}
		if (offset < 0) {
			throw new ArrayIndexOutOfBoundsException("偏移量不能为负！");
		}
		if (offset + 1 > b.length) {
			throw new ArrayIndexOutOfBoundsException("偏移量越界！最大值：" + (b.length - 1) + " 当前值：" + offset);
		}
		if (offset + length > b.length) {// 修正长度
			length = b.length - offset;
		}

		return toStr(b, StandardCharsets.US_ASCII, offset, length, '\0');
	}

	/**
	 * byte[] -> String（指定字符集；以'\0'为结束符）
	 * 
	 * @param b
	 *            byte数组（不能为空）
	 * @param charsetName
	 *            字符集名（为空：使用默认字符集）
	 * @param offset
	 *            偏移量（从0开始）
	 * @param length
	 *            长度（大于零）
	 * @return String（不为NULL）
	 * @throws IllegalArgumentException
	 *             数组为空 或 长度错误
	 * @throws ArrayIndexOutOfBoundsException
	 *             偏移量为负 或 偏移量越界
	 * @throws UnsupportedCharsetException
	 *             字符集不支持
	 */
	public static String toString(byte[] b, String charsetName, int offset, int length) {
		if (b == null || b.length < 1) {
			throw new IllegalArgumentException("数组不能为空！");
		}
		if (length < 1) {
			throw new IllegalArgumentException("长度错误！length:" + length);
		}
		if (offset < 0) {
			throw new ArrayIndexOutOfBoundsException("偏移量不能为负！");
		}
		if (offset + 1 > b.length) {
			throw new ArrayIndexOutOfBoundsException("偏移量越界！最大值：" + (b.length - 1) + " 当前值：" + offset);
		}
		if (offset + length > b.length) {// 修正长度
			length = b.length - offset;
		}

		if (charsetName == null || "".equals(charsetName = charsetName.trim())) {
			return toStr(b, null, offset, length, '\0');
		}

		return toStr(b, Charset.forName(charsetName), offset, length, '\0');
	}

	/**
	 * byte[] -> String（指定字符集；指定结束符）
	 * 
	 * @param b
	 *            byte数组（不能为空）
	 * @param charsetName
	 *            字符集名（为空：使用默认字符集）
	 * @param offset
	 *            偏移量（从0开始）
	 * @param length
	 *            读取长度（大于零）
	 * @param terminator
	 *            结束符
	 * @return String（不为NULL）
	 * @throws IllegalArgumentException
	 *             数组为空 或 长度错误
	 * @throws ArrayIndexOutOfBoundsException
	 *             偏移量为负 或 偏移量越界
	 * @throws UnsupportedCharsetException
	 *             字符集不支持
	 */
	public static String toString(byte[] b, String charsetName, int offset, int length, char terminator) {
		if (b == null || b.length < 1) {
			throw new IllegalArgumentException("数组不能为空！");
		}
		if (length < 1) {
			throw new IllegalArgumentException("长度错误！length:" + length);
		}
		if (offset < 0) {
			throw new ArrayIndexOutOfBoundsException("偏移量不能为负！");
		}
		if (offset + 1 > b.length) {
			throw new ArrayIndexOutOfBoundsException("偏移量越界！最大值：" + (b.length - 1) + " 当前值：" + offset);
		}
		if (offset + length > b.length) {// 修正长度
			length = b.length - offset;
		}

		if (charsetName == null || "".equals(charsetName = charsetName.trim())) {
			return toStr(b, null, offset, length, terminator);
		}

		return toStr(b, Charset.forName(charsetName), offset, length, terminator);
	}

	/**
	 * String -> byte[]（ASCII编码；以'\0'为结束符）
	 * 
	 * @param str
	 *            字符串
	 * @param length
	 *            输出长度（大于零）
	 * @return byte[length]
	 * @throws IllegalArgumentException
	 *             长度错误
	 */
	public static byte[] stringToByte(String str, int length) {
		if (length < 1) {
			throw new IllegalArgumentException("长度错误！length:" + length);
		}
		if (str == null || "".equals(str)) {
			return new byte[length];
		}

		return stringToByte(str, "ASCII", length);
	}

	/**
	 * String -> byte[]（指定字符集；以'\0'为结束符）
	 * 
	 * @param str
	 *            字符串
	 * @param charsetName
	 *            字符集名（为空：使用默认字符集）
	 * @param length
	 *            输出长度（大于零）
	 * @return byte[length]
	 * @throws IllegalArgumentException
	 *             长度错误
	 * @throws UnsupportedCharsetException
	 *             字符集不支持
	 */
	public static byte[] stringToByte(String str, String charsetName, int length) {
		if (length < 1) {
			throw new IllegalArgumentException("长度错误！length:" + length);
		}
		if (str == null || "".equals(str)) {
			return new byte[length];
		}

		byte[] temp;
		if (charsetName == null || "".equals(charsetName = charsetName.trim())) {
			temp = str.getBytes();
		} else {
			temp = str.getBytes(Charset.forName(charsetName));
		}
		byte[] result = new byte[length];
		if (temp.length > 0) {
			// 因为“'\0'= 0x00”，故结束符不再进行处理
			System.arraycopy(temp, 0, result, 0, Math.min(temp.length, length));
		}
		return result;
	}

	/**
	 * byte -> byte[8]（按bit位拆分）
	 * 
	 * @param b
	 *            字节
	 * @return byte[8]
	 */
	public static byte[] toBinaryArray(byte b) {
		byte[] array = new byte[8];
		for (int i = 0; i < 8; i++) {
			array[i] = (byte) ((b >> (7 - i)) & 0x01);
		}
		return array;
	}

	/**
	 * 整数 -> 二进制字符串（无分隔符；长度8/16/32/64；不带0b）
	 * 
	 * @param n
	 *            整数（byte/char/short/int/long）
	 * @return 二进制字符串
	 * @throws NullPointerException
	 *             整数为空
	 */
	public static String toBinaryStr(Number n) {
		if (n == null) {
			throw new NullPointerException("整数对象不能为空！");
		}

		int size = 8;// bit位数
		if (n instanceof Short) {
			size = 16;
		} else if (n instanceof Integer || n instanceof AtomicInteger) {
			size = 32;
		} else if (n instanceof Long || n instanceof AtomicLong) {
			size = 64;
		}

		String s = Long.toBinaryString(n.longValue());
		return (size > s.length() ? String.format("%0" + (size - s.length()) + "d", 0) : "") + s;
	}

	/**
	 * 整数 -> 二进制字符串（无分隔符；指定长度；不带0b）
	 * 
	 * @param n
	 *            整数（byte/char/short/int/long）
	 * @param length
	 *            长度
	 * @return 二进制字符串
	 */
	public static String toBinaryStr(long n, int length) {
		String s = Long.toBinaryString(n);
		if (s.length() == length) {
			return s;
		} else if (s.length() > length) {
			return s.substring(s.length() - length);
		} else {
			return String.format("%0" + (length - s.length()) + "d", 0) + s;
		}
	}

	/**
	 * byte[4] -> IPv4（点分10进制）
	 * 
	 * @param b
	 *            byte数组（不能为空；长度：4）
	 * @return IPv4
	 * @throws IllegalArgumentException
	 *             数组为空或长度不足
	 */
	public static String byteToIPv4(byte[] b) {
		if (b == null || b.length < 4) {
			throw new IllegalArgumentException("数组为空或长度不足！");
		}

		return byteToIPv4(b, 0);
	}

	/**
	 * byte[] -> IPv4（点分10进制）
	 * 
	 * @param b
	 *            byte数组（不能为空）
	 * @param offset
	 *            偏移量（从0开始）
	 * @return IPv4
	 * @throws IllegalArgumentException
	 *             数组为空
	 * @throws ArrayIndexOutOfBoundsException
	 *             偏移量为负 或 偏移量越界 或 下标越界
	 */
	public static String byteToIPv4(byte[] b, int offset) {
		if (b == null || b.length < 1) {
			throw new IllegalArgumentException("数组不能为空！");
		}
		if (offset < 0) {
			throw new ArrayIndexOutOfBoundsException("偏移量不能为负！");
		}
		if (offset + 1 > b.length) {
			throw new ArrayIndexOutOfBoundsException("偏移量越界！最大值：" + (b.length - 1) + " 当前值：" + offset);
		}
		if (offset + 4 > b.length) {
			throw new ArrayIndexOutOfBoundsException(
					"数组长度不足或偏移量不合理，下标越界！最大值：" + (b.length - 1) + " 目标值：" + (offset + 3));
		}

		return new StringBuilder().append(b[offset] & 0xFF).append(".").append(b[offset + 1] & 0xFF).append(".")
				.append(b[offset + 2] & 0xFF).append(".").append(b[offset + 3] & 0xFF).toString();
	}

}
