package com.huangwei.util;

import java.io.*;
import java.util.*;

/**
 * 对象工具
 */
public final class ObjectUtil {

	private ObjectUtil() {
		throw new AssertionError("工具类，不能实例化！");
	}

	/**
	 * 对象 -> JSON字符串
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
	 * 对象 -> JSON字符串
	 * 
	 * @param obj
	 *            对象
	 * @return NULL 或 JSON字符串
	 */
	public static String toString(Object obj) {
		if (obj == null) {
			return null;
		}

		return JsonUtil.toString(obj);
	}

	/**
	 * 是否为空<br>
	 * <br>
	 * 空的定义：null/空字符串/空集合
	 * 
	 * @param obj
	 *            对象
	 * @return true:为空 false:非空
	 */
	public static boolean isEmpty(Object obj) {
		if (obj == null) {
			return true;
		}

		if (obj instanceof String) {
			return "".equals(obj);
		} else if (obj.getClass().isArray()) {
			return ((Object[]) obj).length < 1;
		} else if (obj instanceof Dictionary) {
			return ((Dictionary<?, ?>) obj).size() < 1;
		} else if (obj instanceof Map) {
			return ((Map<?, ?>) obj).size() < 1;
		} else if (obj instanceof Collection) {
			return ((Collection<?>) obj).size() < 1;
		} else {
			return false;
		}
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
	 * 计算Map/Set不扩容初始容量
	 * 
	 * @param expectedSize
	 *            期望大小（不能为空，不能为负数）
	 * @return 不扩容初始容量
	 */
	public static int mapCapacity(Integer expectedSize) {
		if (expectedSize == null || expectedSize < 1) {
			expectedSize = 1;
		}
		if (expectedSize < 3) {
			return expectedSize + 1;
		}
		if (expectedSize < 1073741824) {
			// This is the calculation used in JDK8 to resize when a putAll happens;
			// It seems to be the most conservative calculation we can make.
			// 0.75 is the default load factor.
			return (int) ((float) expectedSize / 0.75F + 1.0F);
		}
		return Integer.MAX_VALUE;// any large value
	}

	/**
	 * 合并列表
	 * 
	 * @param a
	 *            列表A
	 * @param b
	 *            列表B
	 * @return NULL 或 (合并后的)列表
	 */
	public static <T> List<T> mergeList(final List<T> a, final List<T> b) {
		int n = (a == null ? 0 : a.size()) + (b == null ? 0 : b.size());
		if (n < 1) {
			return null;
		}

		List<T> list = new ArrayList<T>(n);
		if (a != null && !a.isEmpty()) {
			list.addAll(a);
		}
		if (b != null && !b.isEmpty()) {
			list.addAll(b);
		}
		return list;
	}

	/**
	 * 深度克隆/深层复制
	 * 
	 * @param <T>
	 *            泛型（对象类型）
	 * @param src
	 *            源对象（不能为空）
	 * @return NULL(参数为空) 或 对象副本
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
