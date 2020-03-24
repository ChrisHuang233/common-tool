package com.huangwei;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 分页对象
 */
public class Page<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	/* ----------------------- Fields ----------------------- */

	/** 查询条件 */
	private Map<String, Object> condition;
	/** 偏移量（从0开始） */
	private int offset;
	/** 最大记录数（每页多少条）（大于等于1） */
	private int limit;
	/** 记录总数 */
	private int total;
	/** 当前页记录数 */
	private int size;
	/** 分页结果 */
	private List<T> result;

	/* -------------------- Constructors -------------------- */

	public Page() {
		super();
	}

	public Page(int offset, int limit, int total, List<T> result) {
		super();
		this.offset = offset;
		this.limit = limit;
		this.total = total;
		this.size = result == null ? 0 : result.size();
		this.result = result;
	}

	public Page(Map<String, Object> condition, int offset, int limit, int total, List<T> result) {
		super();
		this.condition = condition;
		this.offset = offset;
		this.limit = limit;
		this.total = total;
		this.size = result == null ? 0 : result.size();
		this.result = result;
	}

	/* ----------------------- Methods ----------------------- */

	@Override
	public String toString() {
		return "Page [condition=" + condition + ", offset=" + offset + ", limit=" + limit + ", total=" + total
				+ ", size=" + size + ", result=" + result + "]";
	}

	/* ----------------- Getters and Setters ----------------- */

	/** 查询条件 */
	public Map<String, Object> getCondition() {
		return condition;
	}

	/** 查询条件 */
	public void setCondition(Map<String, Object> condition) {
		this.condition = condition;
	}

	/** 偏移量（从0开始） */
	public int getOffset() {
		return offset;
	}

	/** 偏移量（从0开始） */
	public void setOffset(int offset) {
		this.offset = offset;
	}

	/** 最大记录数（每页多少条）（大于等于1） */
	public int getLimit() {
		return limit;
	}

	/** 最大记录数（每页多少条）（大于等于1） */
	public void setLimit(int limit) {
		this.limit = limit;
	}

	/** 记录总数 */
	public int getTotal() {
		return total;
	}

	/** 记录总数 */
	public void setTotal(int total) {
		this.total = total;
	}

	/** 当前页记录数 */
	public int getSize() {
		return size;
	}

	/** 当前页记录数 */
	public void setSize(int size) {
		this.size = size;
	}

	/** 分页结果 */
	public List<T> getResult() {
		return result;
	}

	/** 分页结果 */
	public void setResult(List<T> result) {
		this.result = result;
	}

}
