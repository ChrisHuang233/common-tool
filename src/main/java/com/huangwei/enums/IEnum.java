package com.huangwei.enums;

/**
 * 枚举抽象接口 - 有值(Value)枚举
 *
 * @param <E>
 *            枚举类型
 * @param <T>
 *            值类型
 */
public interface IEnum<E extends Enum<?>, T> {

	/**
	 * 是否相等
	 * 
	 * @param value
	 *            值
	 * @return true:相等 false:不相等
	 */
	boolean equal(T value);

	/** 值 */
	T value();

}
