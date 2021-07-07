package com.huangwei.pair;

import java.io.Serializable;

/**
 * 字符串值对<br>
 * <br>
 * 参照：org.apache.http.message.BasicNameValuePair
 */
public class StringPair implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;

	/** 左值 */
	private final String left;
	/** 右值 */
	private final String right;

	/**
	 * 字符串值对
	 * 
	 * @param left
	 *            左值（不能为空）
	 * @param right
	 *            右值
	 */
	public StringPair(final String left, final String right) {
		super();
		if (left == null) {
			throw new IllegalArgumentException("Left may not be null");
		}

		this.left = left;
		this.right = right;
	}

	@Override
	public String toString() {
		final int length = left.length() + 7 + (right == null ? 4 : right.length());
		StringBuilder builder = new StringBuilder(length);
		builder.append("{\"").append(left).append("\":\"").append(right).append("\"}");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (obj instanceof StringPair) {
			final StringPair other = (StringPair) obj;
			return left.equals(other.left) && (right == null ? other.right == null : right.equals(other.right));
		}
		return true;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/** 左值 */
	public String getLeft() {
		return left;
	}

	/** 右值 */
	public String getRight() {
		return right;
	}

}
