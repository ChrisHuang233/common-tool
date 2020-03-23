package com.huangwei.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 对象工具
 */
public final class ObjectUtil {

	private ObjectUtil() {
		throw new AssertionError("工具类，不能实例化！");
	}

	/**
	 * 对象 -> 字符串
	 * 
	 * @param obj
	 *            对象
	 * @return 空字符串 或 JSON字符串
	 */
	public static String toStr(Object obj) {
		if (obj == null) {
			return StringUtil.Empty;
		}

		return JsonUtil.toString(obj);
	}

	/**
	 * 对象 -> 字符串
	 * 
	 * @param obj
	 *            对象
	 * @return null 或 JSON字符串
	 */
	public static String toString(Object obj) {
		if (obj == null) {
			return null;
		}

		return JsonUtil.toString(obj);
	}

	/**
	 * 如果对象为Null，返回给定的默认值<br>
	 * <br>
	 * 参考：<br>
	 * ①C#: ??运算符(null合并运算符)<br>
	 * ②MySQL: IFNULL(expr1, expr2)<br>
	 * ③SQL Server/MS Access: ISNULL(expr1, expr2)
	 * 
	 * @param obj
	 *            对象
	 * @param nullDefault
	 *            默认值
	 * @return 对象值 或 默认值
	 * @throws ClassCastException
	 *             默认值不能转换为对象所属的类型
	 */
	@SuppressWarnings("unchecked")
	public static <T> T ifNull(T obj, Object nullDefault) {
		if (obj == null) {
			return (T) nullDefault;
		}

		return obj;
	}

	/**
	 * 深度克隆/深层复制
	 * 
	 * @param <T>
	 *            泛型（对象类型）
	 * @param src
	 *            源对象（不能为空）
	 * @return null(参数为空) 或 对象副本
	 * @throws RuntimeException
	 *             IOException 或 ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T deepClone(final T src) {
		if (src == null) {
			return null;
		}

		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		try {
			final ByteArrayOutputStream byteOut = new ByteArrayOutputStream(512);
			out = new ObjectOutputStream(byteOut);
			out.writeObject(src);
			out.close();
			out = null;

			final ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
			in = new ObjectInputStream(byteIn);
			return (T) in.readObject();
		} catch (final IOException e) {
			throw new RuntimeException("IOException while cloning object.", e);
		} catch (final ClassNotFoundException e) {
			throw new RuntimeException("ClassNotFoundException while reading cloned object data.", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
					// Ignore Exception
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					// Ignore Exception
				}
			}
		}
	}

}
