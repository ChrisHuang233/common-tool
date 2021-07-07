package com.huangwei.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.List;

/**
 * Excel工具类
 */
public class ExcelUtil {
	protected static Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

	/**
	 * Excel导出
	 * 
	 * @param response
	 *            HTTP响应（不能为空）
	 * @param excel
	 *            Excel工作薄（不能为空）
	 * @param filename
	 *            文件名（带后缀名）（为空：Excel_时间戳.xlsx）
	 * @throws IllegalArgumentException
	 *             HTTP响应为空 或 Excel工作薄为空
	 */
	public static void export(HttpServletResponse response, SXSSFWorkbook excel, String filename) {
		if (response == null) {
			throw new IllegalArgumentException("HTTP响应不能为空！");
		}
		if (excel == null) {
			throw new IllegalArgumentException("Excel工作薄不能为空！");
		}

		export(null, response, excel, filename);
	}

	/**
	 * Excel导出
	 * 
	 * @param request
	 *            HTTP请求（可以为空）
	 * @param response
	 *            HTTP响应（不能为空）
	 * @param excel
	 *            Excel工作薄（不能为空）
	 * @param filename
	 *            文件名（带后缀名）（为空：Excel_时间戳.xlsx）
	 * @throws IllegalArgumentException
	 *             HTTP响应为空 或 Excel工作薄为空
	 */
	public static void export(HttpServletRequest request, HttpServletResponse response, SXSSFWorkbook excel,
			String filename) {
		if (response == null) {
			throw new IllegalArgumentException("HTTP响应不能为空！");
		}
		if (excel == null) {
			throw new IllegalArgumentException("Excel工作薄不能为空！");
		}
		if (filename == null || "".equals(filename = filename.trim())) {// 文件名为空
			filename = "Excel_" + System.currentTimeMillis() + ".xlsx";
		} else if (!filename.matches("(?i).*(\\.xls|\\.xlsx)$")) {// 无后缀名
			filename += ".xlsx";
		}

		logger.debug("[Excel导出]Begin...filename:{} currentTime:{}", filename, System.currentTimeMillis());
		OutputStream output = null;
		try {
			// 设置响应头
			response.reset();
			response.setHeader("Cache-Control", "no-cache");
			response.setHeader("Access-Control-Allow-Credentials", "true");
			response.setHeader("Access-Control-Allow-Headers",
					"Accept, Accept-Language, Content-Language, Content-Type, Origin");
			response.setHeader("Access-Control-Allow-Origin", StringUtil.ifEmpty(request.getHeader("Origin"), "*"));
			response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
//			response.setContentType("application/octet-stream");// 二进制流（通用）
//			response.setContentType("application/x-xls; charset=UTF-8");// Excel
			response.setContentType("application/vnd.ms-excel; charset=UTF-8");// Excel
			// 以附件的形式下载，并提供一个默认文件名
			response.setHeader("Content-Disposition", encodeFilename(filename));
			// 输出数据
			output = response.getOutputStream();
			excel.write(output);
			output.flush();
		} catch (Exception e) {
			logger.error("[Excel导出]出错！filename:{}", filename, e);
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (Exception e) {
					logger.error("[Excel导出]关闭输出流出错！filename:{}", filename, e);
				}
			}
			if (excel != null) {
				excel.dispose();
				excel = null;
			}
		}
		logger.debug("[Excel导出]End...filename:{} currentTime:{}", filename, System.currentTimeMillis());
	}

	/**
	 * 单页Excel导出（简单样式）
	 * 
	 * @param response
	 *            HTTP响应（不能为空）
	 * @param sheetName
	 *            工作表名称（为空：Excel_时间戳）（同时作为下载文件名）
	 * @param title
	 *            表名/标题（为空：无标题）
	 * @param columnNames
	 *            表头/列名（为空：无表头）
	 * @param columnValues
	 *            表数据（无数据时仅导出表头，表头和数据均为空时仅导出空文件）
	 * @throws IllegalArgumentException
	 *             HTTP响应为空
	 */
	public static void export(HttpServletResponse response, String sheetName, String title, String[] columnNames,
			List<String[]> columnValues) {
		if (response == null) {
			throw new IllegalArgumentException("HTTP响应不能为空！");
		}

		export(null, response, sheetName, title, columnNames, columnValues);
	}

	/**
	 * 单页Excel导出（简单样式）
	 * 
	 * @param request
	 *            HTTP请求（可以为空）
	 * @param response
	 *            HTTP响应（不能为空）
	 * @param sheetName
	 *            工作表名称（为空：Excel_时间戳）（同时作为下载文件名）
	 * @param title
	 *            表名/标题（为空：无标题）
	 * @param columnNames
	 *            表头/列名（为空：无表头）
	 * @param columnValues
	 *            表数据（无数据时仅导出表头，表头和数据均为空时仅导出空文件）
	 * @throws IllegalArgumentException
	 *             HTTP响应为空
	 */
	public static void export(HttpServletRequest request, HttpServletResponse response, String sheetName, String title,
			String[] columnNames, List<String[]> columnValues) {
		if (response == null) {
			throw new IllegalArgumentException("HTTP响应不能为空！");
		}
		if (sheetName == null || "".equals(sheetName = sheetName.trim())) {
			sheetName = "Excel_" + System.currentTimeMillis();
		}

		logger.debug("[Excel导出]Begin...sheetName:{} title:{} currentTime:{}", sheetName, title,
				System.currentTimeMillis());
		// Excel工作薄
		SXSSFWorkbook excel = new SXSSFWorkbook(
				Math.max(columnValues == null ? 0 : (columnValues.size() / 100), SXSSFWorkbook.DEFAULT_WINDOW_SIZE));
		Sheet sheet = excel.createSheet(sheetName);// 工作表
		sheet.setDefaultColumnWidth(20);
		sheet.setDefaultRowHeight((short) 500);// 行高
		int rowCount = 0;// 行计数器
		int maxColumnNumber = 0;// 最大列数
		Row titleRow = null;// 标题
		if (title != null && !"".equals(title = title.trim())) {
			// 标题样式
			CellStyle titleStyle = excel.createCellStyle();
			titleStyle.setAlignment(HorizontalAlignment.CENTER);// 水平居中
			// 标题
			titleRow = sheet.createRow(rowCount++);
			Cell titleCell = titleRow.createCell(0);
			titleCell.setCellValue(new XSSFRichTextString(title));
			titleCell.setCellStyle(titleStyle);
		}
		if (columnNames != null && columnNames.length > 0) {// 表头不为空
			maxColumnNumber = Math.max(maxColumnNumber, columnNames.length);
			Row headerRow = sheet.createRow(rowCount++);// 表头
			Cell cell;
			for (int i = 0; i < columnNames.length; i++) {
				cell = headerRow.createCell(i);
				cell.setCellValue(new XSSFRichTextString(columnNames[i]));
			}
		}
		if (columnValues != null && !columnValues.isEmpty()) {// 数据不为空
			Row row;
			Cell cell;
			for (String[] values : columnValues) {
				if (values == null || values.length < 1) {
					continue;
				}

				maxColumnNumber = Math.max(maxColumnNumber, values.length);
				row = sheet.createRow(rowCount++);
				for (int j = 0; j < values.length; j++) {
					cell = row.createCell(j);
					cell.setCellValue(new XSSFRichTextString(values[j] == null ? "" : values[j]));
				}
			}
		}
		// 扩大标题列跨度
		if (titleRow != null && maxColumnNumber > 1) {
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, maxColumnNumber - 1));// 合并单元格
		}
		// 导出
		export(request, response, excel, sheetName);
		logger.debug("[Excel导出]End...sheetName:{} title:{} currentTime:{}", sheetName, title,
				System.currentTimeMillis());
	}

	/**
	 * 文件名编码（解决HTTP头编码问题(Content-Disposition)）
	 * 
	 * @param filename
	 *            文件名（不能为空；必须带有后缀名）
	 * @return Content-Disposition的值
	 * @throws IllegalArgumentException
	 *             文件名为空
	 */
	public static String encodeFilename(String filename) {
		if (filename == null || "".equals(filename = filename.trim())) {
			throw new IllegalArgumentException("文件名不能为空！");
		}

		String encodedName = JavaScriptUtil.encodeUri(filename);
		return "attachment; filename=\"" + encodedName + "\"; filename*=UTF-8''" + encodedName;
	}

}
