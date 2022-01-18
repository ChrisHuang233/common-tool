package com.huangwei.common;

/**
 * 复选框选项
 */
public class CheckboxItem implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	/** ID */
	private Long id;
	/** 名称 */
	private String name;
	/** 是否选中（true:已选中 false:未选中） */
	private boolean checked;

	/**
	 * 复选框选项
	 */
	public CheckboxItem() {
		super();
	}

	/**
	 * 复选框选项
	 * 
	 * @param id
	 *            ID
	 * @param name
	 *            名称
	 * @param checked
	 *            是否选中（true:已选中 false:未选中）
	 */
	public CheckboxItem(Long id, String name, boolean checked) {
		super();
		this.id = id;
		this.name = name;
		this.checked = checked;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[clazz=CheckboxItem, id=").append(id).append(", name=").append(name).append(", checked=")
				.append(checked).append("]");
		return builder.toString();
	}

	/** ID */
	public Long getId() {
		return id;
	}

	/** ID */
	public void setId(Long id) {
		this.id = id;
	}

	/** 名称 */
	public String getName() {
		return name;
	}

	/** 名称 */
	public void setName(String name) {
		this.name = name;
	}

	/** 是否选中（true:已选中 false:未选中） */
	public boolean isChecked() {
		return checked;
	}

	/** 是否选中（true:已选中 false:未选中） */
	public void setChecked(boolean checked) {
		this.checked = checked;
	}

}
