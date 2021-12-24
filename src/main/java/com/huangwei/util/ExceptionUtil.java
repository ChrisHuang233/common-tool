package com.huangwei.util;

/**
 * 异常工具类
 */
public class ExceptionUtil {

	/**
	 * 获取异常详细信息（默认读取2条堆栈信息）
	 *
	 * @param e
	 *            异常
	 * @return null（参数为空） 或 异常信息
	 */
	public static String detail(Throwable e) {
		return detail(e, 2);
	}

	/**
	 * 获取异常详细信息
	 *
	 * @param e
	 *            异常
	 * @param n
	 *            堆栈信息读取数量（NULL或小于零：不读取；等于零：不限数量；大于零：指定数量）
	 * @return null（参数为空） 或 异常信息
	 */
	public static String detail(Throwable e, Integer n) {
		if (e == null) {
			return null;
		}

		StringBuilder detail = new StringBuilder();
		detail.append(e.toString()).append(";");

		/* 读取堆栈信息 */
		if (n != null && n >= 0) {
			StackTraceElement[] stack = e.getStackTrace();
			if (stack != null && stack.length > 0) {
				for (int i = 0; i < stack.length; i++) {
					detail.append(" at ").append(stack[i]).append(";");
					if (n > 0 && i + 1 >= n) {
						break;
					}
				}
			}
		}

		/* 获取原因 */
		String cause = getCause(e);
		if (cause.length() > 0) {
			detail.append(getCause(e));
		}

		return detail.toString();
	}

	/**
	 * 获取原因
	 *
	 * @param t
	 *            异常
	 * @return 原因（不为NULL）
	 */
	private static String getCause(Throwable t) {
		if (t == null) {
			return "";
		}

		Throwable c = t.getCause();
		if (c == null) {
			return "";
		} else {
			return " Caused by: " + c.toString() + ";" + getCause(c);
		}
	}

}
