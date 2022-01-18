package com.huangwei.common;

import java.io.Serializable;

/**
 * 记录ID
 */
public class RecordId<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	/** 记录ID */
	private T id;

	/** 记录ID */
	public T getId() {
		return id;
	}

	/** 记录ID */
	public void setId(T id) {
		this.id = id;
	}

}
