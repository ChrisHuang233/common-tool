package com.huangwei.util;

import java.util.Arrays;
import java.util.List;

/**
 * 数组工具
 */
public class ArrayUtil {

	/**
	 * 装箱（int[] -> Integer[]）
	 * 
	 * @param a
	 *            int数组
	 * @return null 或 Integer数组
	 */
	public static Integer[] box(int[] a) {
		if (a == null) {
			return null;
		}
		if (a.length < 1) {
			return new Integer[0];
		}

		Integer[] t = new Integer[a.length];
		for (int i = 0; i < a.length; i++) {
			t[i] = Integer.valueOf(a[i]);
		}
		return t;
	}

	/**
	 * 拆箱（Integer[] -> int[]）
	 * 
	 * @param a
	 *            Integer数组
	 * @return null 或 int数组
	 */
	public static int[] unbox(Integer[] a) {
		if (a == null) {
			return null;
		}
		if (a.length < 1) {
			return new int[0];
		}

		int[] t = new int[a.length];
		int n = 0;
		for (Integer i : a) {
			if (i == null) {
				continue;
			}

			t[n++] = i;
		}

		// 仅去除NULL，不拆箱
		// Arrays.stream(array).filter(i -> i != null).toArray(Integer[]::new);
		return n < 1 ? new int[0] : Arrays.copyOf(t, n);
	}

	/**
	 * List&lt;Integer&gt; -> int[]
	 * 
	 * @param list
	 *            Integer列表
	 * @return null 或 int数组
	 */
	public static int[] toIntArray(List<Integer> list) {
		if (list == null) {
			return null;
		}
		if (list.size() < 1) {
			return new int[0];
		}

		int[] t = new int[list.size()];
		int n = 0;
		for (Integer i : list) {
			if (i == null) {
				continue;
			}

			t[n++] = i;
		}

		return n < 1 ? new int[0] : Arrays.copyOf(t, n);
	}

	/**
	 * List&lt;Integer&gt; -> long[]
	 * 
	 * @param list
	 *            Long列表
	 * @return null 或 long数组
	 */
	public static long[] toLongArray(List<Long> list) {
		if (list == null) {
			return null;
		}
		if (list.size() < 1) {
			return new long[0];
		}

		long[] t = new long[list.size()];
		int n = 0;
		for (Long i : list) {
			if (i == null) {
				continue;
			}

			t[n++] = i;
		}

		return n < 1 ? new long[0] : Arrays.copyOf(t, n);
	}

}
