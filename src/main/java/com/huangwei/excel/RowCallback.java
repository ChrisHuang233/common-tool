package com.huangwei.excel;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFComment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 行解析回调
 */
public abstract class RowCallback implements SheetCallback {
	protected static final Logger logger = LoggerFactory.getLogger(RowCallback.class);

	/** 行数据 */
	private List<String> row;

	/**
	 * 一行数据解析完成
	 * 
	 * @param rowIndex
	 *            行索引（从0开始）
	 * @param row
	 *            行数据（可能为空）
	 */
	public abstract void row(int rowIndex, List<String> row);

	/**
	 * 单个Sheet解析完成
	 * 
	 * @param rowNumber
	 *            行数（>=0）
	 */
	public abstract void sheet(int rowNumber);

	@Override
	public void header(String text, String tagName) {
		// Do nothing
	}

	@Override
	public void rowStart(int rowIndex, int columnSize) {
		row = new ArrayList<String>(Math.max(columnSize, 0));
	}

	@Override
	public void cell(int cellIndex, String cellReference, String strValue, Object objValue, XSSFComment comment) {
		row.add(strValue);
	}

	@Override
	public void rowEnd(int rowIndex) {
		try {
			row(rowIndex, row);
		} catch (Exception e) {
			logger.error("[行解析回调]出错！", e);
		}
	}

	@Override
	public void footer(String text, String tagName) {
		// Do nothing
	}

	@Override
	public void sheetEnd(int rowNumber) {
		try {
			sheet(rowNumber);
		} catch (Exception e) {
			logger.error("[工作表解析回调]出错！", e);
		}
	}

}
