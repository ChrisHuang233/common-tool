package com.huangwei.common;

/**
 * 摘要信息
 */
public class SummaryInfo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	/** ID */
	private Long id;
	/** 名称 */
	private String name;
	/** 全称 */
	private String fullname;

	/**
	 * 摘要信息
	 */
	public SummaryInfo() {
		super();
	}

	/**
	 * 摘要信息
	 * 
	 * @param id
	 *            ID
	 * @param name
	 *            名称
	 */
	public SummaryInfo(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	/**
	 * 摘要信息
	 * 
	 * @param id
	 *            ID
	 * @param name
	 *            名称
	 * @param fullname
	 *            全称
	 */
	public SummaryInfo(Long id, String name, String fullname) {
		super();
		this.id = id;
		this.name = name;
		this.fullname = fullname;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[clazz=SummaryInfo, id=").append(id).append(", name=").append(name).append(", fullname=")
				.append(fullname).append("]");
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

	/** 全称 */
	public String getFullname() {
		return fullname;
	}

	/** 全称 */
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

}
