package com.huangwei.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * AtomicInteger扩展
 */
public class AtomicInt {

	/** 内置AtomicInteger对象 */
	private final AtomicInteger i;
	/** 初始值 */
	private final int initialValue;

	/**
	 * AtomicInteger扩展（初始值为0）
	 */
	public AtomicInt() {
		i = new AtomicInteger();
		this.initialValue = 0;
	}

	/**
	 * AtomicInteger扩展（指定初始值）
	 * 
	 * @param initialValue
	 *            初始值
	 */
	public AtomicInt(int initialValue) {
		i = new AtomicInteger(initialValue);
		this.initialValue = initialValue;
	}

	/**
	 * 自增并返回新值（循环递增：到达上限后重置为下限）<br>
	 * 下限：初始值；上限：2147483647
	 * 
	 * @return 新值（下限 < n <= 上限）
	 * @throws RuntimeException
	 *             初始值大于等于Integer最大值
	 */
	public final int incrementAndGet() {
		if (initialValue >= Integer.MAX_VALUE) {
			throw new RuntimeException(
					"初始值必须小于Integer最大值！initialValue:" + initialValue + " upperBound:" + Integer.MAX_VALUE);
		}

		int current, next;
		for (;;) {
			current = this.i.get();
			next = (current >= Integer.MAX_VALUE ? initialValue : current) + 1;
			if (this.i.compareAndSet(current, next))
				return next;
		}
	}

	/**
	 * 自增并返回新值（循环递增：到达上限后重置为下限）<br>
	 * 下限：初始值；上限：指定值
	 * 
	 * @param upperBound
	 *            上限
	 * @return 新值（下限 < n <= 上限）
	 * @throws IllegalArgumentException
	 *             上限小于等于初始值
	 */
	public final int incrementAndGet(int upperBound) {
		if (upperBound <= initialValue) {
			throw new IllegalArgumentException("上限必须大于初始值！initialValue:" + initialValue + " upperBound:" + upperBound);
		}

		int current, next;
		for (;;) {
			current = this.i.get();
			next = (current >= upperBound ? initialValue : current) + 1;
			if (this.i.compareAndSet(current, next))
				return next;
		}
	}

	/**
	 * 自减并返回新值（循环递减：到达下限后重置为上限）<br>
	 * 下限：0；上限：初始值
	 * 
	 * @return 新值（下限 <= n < 上限）
	 * @throws RuntimeException
	 *             初始值小于等于0
	 */
	public final int decrementAndGet() {
		if (initialValue <= 0) {
			throw new RuntimeException("初始值必须大于0！initialValue:" + initialValue + " upperBound:" + 0);
		}

		int current, next;
		for (;;) {
			current = this.i.get();
			next = (current <= 0 ? initialValue : current) - 1;
			if (this.i.compareAndSet(current, next))
				return next;
		}
	}

	/**
	 * 自减并返回新值（循环递减：到达下限后重置为上限）<br>
	 * 下限：指定值；上限：初始值
	 * 
	 * @param lowerBound
	 *            下限
	 * @return 新值（下限 <= n < 上限）
	 * @throws IllegalArgumentException
	 *             下限大于等于初始值
	 */
	public final int decrementAndGet(int lowerBound) {
		if (lowerBound >= initialValue) {
			throw new IllegalArgumentException("下限必须小于初始值！initialValue:" + initialValue + " lowerBound:" + lowerBound);
		}

		int current, next;
		for (;;) {
			current = this.i.get();
			next = (current <= lowerBound ? initialValue : current) - 1;
			if (this.i.compareAndSet(current, next))
				return next;
		}
	}

}
