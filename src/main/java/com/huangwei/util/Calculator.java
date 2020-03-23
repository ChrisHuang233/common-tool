package com.huangwei.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 计算器
 */
public class Calculator {

	/** 常量 - 字符串 - 整数零（0） */
	public static final String STRING_ZERO = "0";
	/** 常量 - 字符串 - 浮点数零（0） */
	public static final String STRING_DOUBLE_ZERO = "0.0";

	/**
	 * 加法（A + B）
	 * 
	 * @param a
	 *            数据A
	 * @param b
	 *            数据B
	 * @return 和
	 */
	public static double add(Number a, Number b) {
		if (a == null && b == null) {
			return 0D;
		}

		return new BigDecimal(a == null ? STRING_ZERO : a.toString())
				.add(new BigDecimal(b == null ? STRING_ZERO : b.toString())).doubleValue();
	}

	/**
	 * 加法（A + B）<br>
	 * <br>
	 * 舍入方式：四舍五入
	 * 
	 * @param a
	 *            数据A
	 * @param b
	 *            数据B
	 * @param scale
	 *            精度（大于等于零）（保留几位小数）
	 * @return 和
	 * @throws IllegalArgumentException
	 *             精度错误
	 */
	public static double add(Number a, Number b, int scale) {
		if (a == null && b == null) {
			return 0D;
		}
		if (scale < 0) {
			throw new IllegalArgumentException("精度错误，其值必须大于等于零！scale:" + scale);
		}

		return add(a, b, scale, RoundingMode.HALF_UP);
	}

	/**
	 * 加法（A + B）
	 * 
	 * @param a
	 *            数据A
	 * @param b
	 *            数据B
	 * @param scale
	 *            精度（大于等于零）（保留几位小数）
	 * @param roundingMode
	 *            舍入模式（参见：{@link java.math.RoundingMode}）
	 * @return 和
	 * @throws IllegalArgumentException
	 *             精度错误
	 */
	public static double add(Number a, Number b, int scale, RoundingMode roundingMode) {
		if (a == null && b == null) {
			return 0D;
		}
		if (scale < 0) {
			throw new IllegalArgumentException("精度错误，其值必须大于等于零！scale:" + scale);
		}

		return new BigDecimal(a == null ? STRING_ZERO : a.toString())
				.add(new BigDecimal(b == null ? STRING_ZERO : b.toString())).setScale(scale, roundingMode)
				.doubleValue();
	}

	/**
	 * 加法（A + B） -> 字符串
	 * 
	 * @param a
	 *            数据A
	 * @param b
	 *            数据B
	 * @return 和（字符串格式）
	 */
	public static String addToStr(Number a, Number b) {
		if (a == null && b == null) {
			return STRING_ZERO;
		}

		return new BigDecimal(a == null ? STRING_ZERO : a.toString())
				.add(new BigDecimal(b == null ? STRING_ZERO : b.toString())).toString();
	}

	/**
	 * 加法（A + B） -> 字符串<br>
	 * <br>
	 * 舍入方式：四舍五入
	 * 
	 * @param a
	 *            数据A
	 * @param b
	 *            数据B
	 * @param scale
	 *            精度（大于等于零）（保留几位小数）
	 * @return 和（字符串格式）
	 * @throws IllegalArgumentException
	 *             精度错误
	 */
	public static String addToStr(Number a, Number b, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("精度错误，其值必须大于等于零！scale:" + scale);
		}

		return addToStr(a, b, scale, RoundingMode.HALF_UP);
	}

	/**
	 * 加法（A + B） -> 字符串
	 * 
	 * @param a
	 *            数据A
	 * @param b
	 *            数据B
	 * @param scale
	 *            精度（大于等于零）（保留几位小数）
	 * @param roundingMode
	 *            舍入模式（参见：{@link java.math.RoundingMode}）
	 * @return 和（字符串格式）
	 * @throws IllegalArgumentException
	 *             精度错误
	 */
	public static String addToStr(Number a, Number b, int scale, RoundingMode roundingMode) {
		if (scale < 0) {
			throw new IllegalArgumentException("精度错误，其值必须大于等于零！scale:" + scale);
		}

		return new BigDecimal(a == null ? STRING_ZERO : a.toString())
				.add(new BigDecimal(b == null ? STRING_ZERO : b.toString())).setScale(scale, roundingMode).toString();
	}

	/**
	 * 减法（A - B）
	 * 
	 * @param a
	 *            数据A
	 * @param b
	 *            数据B
	 * @return 差（可能为负）
	 */
	public static double subtract(Number a, Number b) {
		if (a == null && b == null) {
			return 0D;
		}

		return new BigDecimal(a == null ? STRING_ZERO : a.toString())
				.subtract(new BigDecimal(b == null ? STRING_ZERO : b.toString())).doubleValue();
	}

	/**
	 * 减法（A - B）<br>
	 * <br>
	 * 舍入方式：四舍五入
	 * 
	 * @param a
	 *            数据A
	 * @param b
	 *            数据B
	 * @param scale
	 *            精度（大于等于零）（保留几位小数）
	 * @return 差（可能为负）
	 * @throws IllegalArgumentException
	 *             精度错误
	 */
	public static double subtract(Number a, Number b, int scale) {
		if (a == null && b == null) {
			return 0D;
		}
		if (scale < 0) {
			throw new IllegalArgumentException("精度错误，其值必须大于等于零！scale:" + scale);
		}

		return subtract(a, b, scale, RoundingMode.HALF_UP);
	}

	/**
	 * 减法（A - B）
	 * 
	 * @param a
	 *            数据A
	 * @param b
	 *            数据B
	 * @param scale
	 *            精度（大于等于零）（保留几位小数）
	 * @param roundingMode
	 *            舍入模式（参见：{@link java.math.RoundingMode}）
	 * @return 差（可能为负）
	 * @throws IllegalArgumentException
	 *             精度错误
	 */
	public static double subtract(Number a, Number b, int scale, RoundingMode roundingMode) {
		if (a == null && b == null) {
			return 0D;
		}
		if (scale < 0) {
			throw new IllegalArgumentException("精度错误，其值必须大于等于零！scale:" + scale);
		}

		return new BigDecimal(a == null ? STRING_ZERO : a.toString())
				.subtract(new BigDecimal(b == null ? STRING_ZERO : b.toString())).setScale(scale, roundingMode)
				.doubleValue();
	}

	/**
	 * 减法（A - B） -> 字符串
	 * 
	 * @param a
	 *            数据A
	 * @param b
	 *            数据B
	 * @return 差（可能为负）（字符串格式）
	 */
	public static String subtractToStr(Number a, Number b) {
		if (a == null && b == null) {
			return STRING_ZERO;
		}

		return new BigDecimal(a == null ? STRING_ZERO : a.toString())
				.subtract(new BigDecimal(b == null ? STRING_ZERO : b.toString())).toString();
	}

	/**
	 * 减法（A - B） -> 字符串<br>
	 * <br>
	 * 舍入方式：四舍五入
	 * 
	 * @param a
	 *            数据A
	 * @param b
	 *            数据B
	 * @param scale
	 *            精度（大于等于零）（保留几位小数）
	 * @return 差（可能为负）（字符串格式）
	 * @throws IllegalArgumentException
	 *             精度错误
	 */
	public static String subtractToStr(Number a, Number b, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("精度错误，其值必须大于等于零！scale:" + scale);
		}

		return subtractToStr(a, b, scale, RoundingMode.HALF_UP);
	}

	/**
	 * 减法（A - B） -> 字符串
	 * 
	 * @param a
	 *            数据A
	 * @param b
	 *            数据B
	 * @param scale
	 *            精度（大于等于零）（保留几位小数）
	 * @param roundingMode
	 *            舍入模式（参见：{@link java.math.RoundingMode}）
	 * @return 差（可能为负）（字符串格式）
	 * @throws IllegalArgumentException
	 *             精度错误
	 */
	public static String subtractToStr(Number a, Number b, int scale, RoundingMode roundingMode) {
		if (scale < 0) {
			throw new IllegalArgumentException("精度错误，其值必须大于等于零！scale:" + scale);
		}

		return new BigDecimal(a == null ? STRING_ZERO : a.toString())
				.subtract(new BigDecimal(b == null ? STRING_ZERO : b.toString())).setScale(scale, roundingMode)
				.toString();
	}

	/**
	 * 乘法（A × B）
	 * 
	 * @param a
	 *            数据A
	 * @param b
	 *            数据B
	 * @return 积
	 * @throws NullPointerException
	 *             参数为空
	 */
	public static double multiply(Number a, Number b) {
		if (a == null && b == null) {
			return 0D;
		}
		if (a == null || b == null) {
			throw new NullPointerException("数据" + (a == null ? "A" : "B") + "不能为空！");
		}

		return new BigDecimal(a.toString()).multiply(new BigDecimal(b.toString())).doubleValue();
	}

	/**
	 * 乘法（A × B）<br>
	 * <br>
	 * 舍入方式：四舍五入
	 * 
	 * @param a
	 *            数据A
	 * @param b
	 *            数据B
	 * @param scale
	 *            精度（大于等于零）（保留几位小数）
	 * @return 积
	 * @throws NullPointerException
	 *             参数为空
	 * @throws IllegalArgumentException
	 *             精度错误
	 */
	public static double multiply(Number a, Number b, int scale) {
		if (a == null && b == null) {
			return 0D;
		}
		if (a == null || b == null) {
			throw new NullPointerException("数据" + (a == null ? "A" : "B") + "不能为空！");
		}
		if (scale < 0) {
			throw new IllegalArgumentException("精度错误，其值必须大于等于零！scale:" + scale);
		}

		return multiply(a, b, scale, RoundingMode.HALF_UP);
	}

	/**
	 * 乘法（A × B）
	 * 
	 * @param a
	 *            数据A
	 * @param b
	 *            数据B
	 * @param scale
	 *            精度（大于等于零）（保留几位小数）
	 * @param roundingMode
	 *            舍入模式（参见：{@link java.math.RoundingMode}）
	 * @return 积
	 * @throws NullPointerException
	 *             参数为空
	 * @throws IllegalArgumentException
	 *             精度错误
	 */
	public static double multiply(Number a, Number b, int scale, RoundingMode roundingMode) {
		if (a == null && b == null) {
			return 0D;
		}
		if (a == null || b == null) {
			throw new NullPointerException("数据" + (a == null ? "A" : "B") + "不能为空！");
		}
		if (scale < 0) {
			throw new IllegalArgumentException("精度错误，其值必须大于等于零！scale:" + scale);
		}

		return new BigDecimal(a.toString()).multiply(new BigDecimal(b.toString())).setScale(scale, roundingMode)
				.doubleValue();
	}

	/**
	 * 乘法（A × B） -> 字符串
	 * 
	 * @param a
	 *            数据A
	 * @param b
	 *            数据B
	 * @return 积（字符串格式）
	 * @throws NullPointerException
	 *             参数为空
	 */
	public static String multiplyToStr(Number a, Number b) {
		if (a == null && b == null) {
			return STRING_ZERO;
		}
		if (a == null || b == null) {
			throw new NullPointerException("数据" + (a == null ? "A" : "B") + "不能为空！");
		}

		return new BigDecimal(a.toString()).multiply(new BigDecimal(b.toString())).toString();
	}

	/**
	 * 乘法（A × B） -> 字符串<br>
	 * <br>
	 * 舍入方式：四舍五入
	 * 
	 * @param a
	 *            数据A
	 * @param b
	 *            数据B
	 * @param scale
	 *            精度（大于等于零）（保留几位小数）
	 * @return 积（字符串格式）
	 * @throws NullPointerException
	 *             参数为空
	 * @throws IllegalArgumentException
	 *             精度错误
	 */
	public static String multiplyToStr(Number a, Number b, int scale) {
		if (a == null && b == null) {
			a = b = 0;
		}
		if (a == null || b == null) {
			throw new NullPointerException("数据" + (a == null ? "A" : "B") + "不能为空！");
		}
		if (scale < 0) {
			throw new IllegalArgumentException("精度错误，其值必须大于等于零！scale:" + scale);
		}

		return multiplyToStr(a, b, scale, RoundingMode.HALF_UP);
	}

	/**
	 * 乘法（A × B） -> 字符串
	 * 
	 * @param a
	 *            数据A
	 * @param b
	 *            数据B
	 * @param scale
	 *            精度（大于等于零）（保留几位小数）
	 * @param roundingMode
	 *            舍入模式（参见：{@link java.math.RoundingMode}）
	 * @return 积（字符串格式）
	 * @throws NullPointerException
	 *             参数为空
	 * @throws IllegalArgumentException
	 *             精度错误
	 */
	public static String multiplyToStr(Number a, Number b, int scale, RoundingMode roundingMode) {
		if (a == null && b == null) {
			a = b = 0;
		}
		if (a == null || b == null) {
			throw new NullPointerException("数据" + (a == null ? "A" : "B") + "不能为空！");
		}
		if (scale < 0) {
			throw new IllegalArgumentException("精度错误，其值必须大于等于零！scale:" + scale);
		}

		return new BigDecimal(a.toString()).multiply(new BigDecimal(b.toString())).setScale(scale, roundingMode)
				.toString();
	}

	/**
	 * 除法（A ÷ B）<br>
	 * <br>
	 * 舍入方式：四舍五入
	 * 
	 * @param a
	 *            数据A（不能为空）
	 * @param b
	 *            数据B（不能为空）
	 * @param scale
	 *            精度（大于等于零）（保留几位小数）
	 * @return 商
	 * @throws NullPointerException
	 *             参数为空
	 * @throws IllegalArgumentException
	 *             精度错误
	 */
	public static double divide(Number a, Number b, int scale) {
		if (a == null && b == null) {
			return 0D;
		}
		if (a == null || b == null) {
			throw new NullPointerException("数据" + (a == null ? "A" : "B") + "不能为空！");
		}
		if (scale < 0) {
			throw new IllegalArgumentException("精度错误，其值必须大于等于零！scale:" + scale);
		}

		return divide(a, b, scale, RoundingMode.HALF_UP);
	}

	/**
	 * 除法（A ÷ B）
	 * 
	 * @param a
	 *            数据A（不能为空）
	 * @param b
	 *            数据B（不能为空）
	 * @param scale
	 *            精度（大于等于零）（保留几位小数）
	 * @param roundingMode
	 *            舍入模式（参见：{@link java.math.RoundingMode}）
	 * @return 商
	 * @throws NullPointerException
	 *             参数为空
	 * @throws IllegalArgumentException
	 *             精度错误
	 */
	public static double divide(Number a, Number b, int scale, RoundingMode roundingMode) {
		if (a == null && b == null) {
			return 0D;
		}
		if (a == null || b == null) {
			throw new NullPointerException("数据" + (a == null ? "A" : "B") + "不能为空！");
		}
		if (scale < 0) {
			throw new IllegalArgumentException("精度错误，其值必须大于等于零！scale:" + scale);
		}

		return new BigDecimal(a.toString()).divide(new BigDecimal(b.toString()), scale, roundingMode).doubleValue();
	}

	/**
	 * 除法（A ÷ B） -> 字符串<br>
	 * <br>
	 * 舍入方式：四舍五入
	 * 
	 * @param a
	 *            数据A（不能为空）
	 * @param b
	 *            数据B（不能为空）
	 * @param scale
	 *            精度（大于等于零）（保留几位小数）
	 * @return 商（字符串格式）
	 * @throws NullPointerException
	 *             参数为空
	 * @throws IllegalArgumentException
	 *             精度错误
	 */
	public static String divideToStr(Number a, Number b, int scale) {
		if (a == null && b == null) {
			a = 0;
			b = 1;
		}
		if (a == null || b == null) {
			throw new NullPointerException("数据" + (a == null ? "A" : "B") + "不能为空！");
		}
		if (scale < 0) {
			throw new IllegalArgumentException("精度错误，其值必须大于等于零！scale:" + scale);
		}

		return divideToStr(a, b, scale, RoundingMode.HALF_UP);
	}

	/**
	 * 除法（A ÷ B） -> 字符串
	 * 
	 * @param a
	 *            数据A（不能为空）
	 * @param b
	 *            数据B（不能为空）
	 * @param scale
	 *            精度（大于等于零）（保留几位小数）
	 * @param roundingMode
	 *            舍入模式（参见：{@link java.math.RoundingMode}）
	 * @return 商（字符串格式）
	 * @throws NullPointerException
	 *             参数为空
	 * @throws IllegalArgumentException
	 *             精度错误
	 */
	public static String divideToStr(Number a, Number b, int scale, RoundingMode roundingMode) {
		if (a == null && b == null) {
			a = 0;
			b = 1;
		}
		if (a == null || b == null) {
			throw new NullPointerException("数据" + (a == null ? "A" : "B") + "不能为空！");
		}
		if (scale < 0) {
			throw new IllegalArgumentException("精度错误，其值必须大于等于零！scale:" + scale);
		}

		return new BigDecimal(a.toString()).divide(new BigDecimal(b.toString()), scale, roundingMode).toString();
	}

	/**
	 * 四舍五入
	 * 
	 * @param n
	 *            数据
	 * @param scale
	 *            精度（大于等于零）（保留几位小数）
	 * @return 四舍五入后的数据
	 * @throws IllegalArgumentException
	 *             精度错误
	 */
	public static double round(Number n, int scale) {
		if (n == null) {
			return 0D;
		}
		if (scale < 0) {
			throw new IllegalArgumentException("精度错误，其值必须大于等于零！scale:" + scale);
		}

		return round(n, scale, RoundingMode.HALF_UP);
	}

	/**
	 * 舍入
	 * 
	 * @param n
	 *            数据
	 * @param scale
	 *            精度（大于等于零）（保留几位小数）
	 * @param roundingMode
	 *            舍入模式（参见：{@link java.math.RoundingMode}）
	 * @return 舍入后的数据
	 * @throws IllegalArgumentException
	 *             精度错误
	 */
	public static double round(Number n, int scale, RoundingMode roundingMode) {
		if (n == null) {
			return 0D;
		}
		if (scale < 0) {
			throw new IllegalArgumentException("精度错误，其值必须大于等于零！scale:" + scale);
		}

		return new BigDecimal(n.toString()).setScale(scale, roundingMode).doubleValue();
	}

	/**
	 * 四舍五入 -> 字符串
	 * 
	 * @param n
	 *            数据
	 * @param scale
	 *            精度（大于等于零）（保留几位小数）
	 * @return 四舍五入后的数据（字符串格式）
	 * @throws IllegalArgumentException
	 *             精度错误
	 */
	public static String roundToStr(Number n, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("精度错误，其值必须大于等于零！scale:" + scale);
		}

		return roundToStr(n, scale, RoundingMode.HALF_UP);
	}

	/**
	 * 舍入 -> 字符串
	 * 
	 * @param n
	 *            数据
	 * @param scale
	 *            精度（大于等于零）（保留几位小数）
	 * @param roundingMode
	 *            舍入模式（参见：{@link java.math.RoundingMode}）
	 * @return 舍入后的数据（字符串格式）
	 * @throws IllegalArgumentException
	 *             精度错误
	 */
	public static String roundToStr(Number n, int scale, RoundingMode roundingMode) {
		if (scale < 0) {
			throw new IllegalArgumentException("精度错误，其值必须大于等于零！scale:" + scale);
		}

		return new BigDecimal(n == null ? STRING_ZERO : n.toString()).setScale(scale, roundingMode).toString();
	}

}
