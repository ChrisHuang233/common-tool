package com.huangwei.excel;

import org.apache.poi.xssf.usermodel.XSSFComment;

/**
 * Sheet解析回调
 */
public interface SheetCallback {

	/**
	 * 页眉
	 * 
	 * @param text
	 *            内容
	 * @param tagName
	 *            标签名
	 */
	public void header(String text, String tagName);

	/**
	 * 行解析 - 开始
	 * 
	 * @param rowIndex
	 *            行索引（从0开始）
	 * @param columnSize
	 *            有效列数（-1:不确定 >=0:列数）
	 */
	public void rowStart(int rowIndex, int columnSize);

	/**
	 * 单元格解析 - 完成
	 * 
	 * @param cellIndex
	 *            单元格索引（从0开始）
	 * @param cellReference
	 *            单元格坐标（例：A1, M2, Z3）（非空）
	 * @param strValue
	 *            (单元格)字符串值（可能为空）
	 * @param objValue
	 *            (单元格)Object值（允许的实例：String, Double, Date, Boolean）（可能为空）
	 * @param comment
	 *            (单元格)注释（可能为空）
	 */
	public void cell(int cellIndex, String cellReference, String strValue, Object objValue, XSSFComment comment);

	/**
	 * 行解析 - 完成
	 * 
	 * @param rowIndex
	 *            行索引（从0开始）
	 */
	public void rowEnd(int rowIndex);

	/**
	 * 页脚
	 * 
	 * @param text
	 *            内容
	 * @param tagName
	 *            标签名
	 */
	public void footer(String text, String tagName);

	/**
	 * 工作表解析 - 完成
	 * 
	 * @param rowNumber
	 *            行数（>=0）
	 */
	public void sheetEnd(int rowNumber);

}
