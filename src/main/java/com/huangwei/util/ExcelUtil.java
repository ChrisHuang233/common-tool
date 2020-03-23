package com.huangwei.util;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
			response.setContentType("text/html;charset=UTF-8");
			response.reset();
//			response.setContentType("application/octet-stream");// 二进制流（通用）
//			response.setContentType("application/x-xls; charset=UTF-8");// Excel
			response.setContentType("application/vnd.ms-excel; charset=UTF-8");// Excel
			// 以附件的形式下载，并提供一个默认文件名
			response.setHeader("Content-Disposition", encodeFilename(request, filename));

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
		SXSSFWorkbook excel = new SXSSFWorkbook(
				(columnValues == null || columnValues.isEmpty()) ? SXSSFWorkbook.DEFAULT_WINDOW_SIZE
						: (columnValues.size() / 100 < SXSSFWorkbook.DEFAULT_WINDOW_SIZE
								? SXSSFWorkbook.DEFAULT_WINDOW_SIZE
								: columnValues.size() / 100));
		Sheet sheet = excel.createSheet(sheetName);
		sheet.setDefaultColumnWidth(20);
		sheet.setDefaultRowHeight((short) 500);// 行高
		int rowCount = 0;// 行计数器
		Row titleRow = null;// 标题
		if (title != null && "".equals(title = title.trim())) {
			titleRow = sheet.createRow(rowCount++);
			titleRow.createCell(0).setCellValue(new XSSFRichTextString(title));// 标题
		}
		if (columnNames != null && columnNames.length > 0) {
			Row headerRow = sheet.createRow(rowCount++);// 表头
			Cell cell;
			for (int i = 0; i < columnNames.length; i++) {
				if (titleRow != null && i > 0) {
					titleRow.createCell(i);
				}
				cell = headerRow.createCell(i);
				cell.setCellValue(new XSSFRichTextString(columnNames[i]));
			}
			if (titleRow != null && columnNames.length > 1) {
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnNames.length - 1));// 合并单元格
			}
		}
		if (columnValues != null && !columnValues.isEmpty()) {
			Row row;
			Cell cell;
			for (String[] values : columnValues) {
				if (values == null || values.length < 1) {
					continue;
				}

				row = sheet.createRow(rowCount++);
				for (int j = 0; j < values.length; j++) {
					cell = row.createCell(j);
					cell.setCellValue(new XSSFRichTextString(values[j] == null ? "" : values[j]));
				}
			}
		}
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

		String encodedName = null;
		try {
			// 空格经URLEncoder转换后会变成"+"(加号)，需要替换为"%20"
			encodedName = URLEncoder.encode(filename, "UTF-8").replace("+", "%20");
		} catch (UnsupportedEncodingException e) {// 正常情况下不会出错
			logger.error("文件名编码出错！filename:" + filename, e);
			encodedName = filename;
		}

		return "attachment; filename=\"" + encodedName + "\"; filename*=UTF-8''" + encodedName;
	}

	/**
	 * 文件名编码（解决HTTP头编码问题(Content-Disposition)）<br>
	 * <br>
	 * 注意：如果有浏览器不支持或不兼容，请尝试使用 {@link #encodeFilename(String)}。
	 * 
	 * @param request
	 *            HTTP请求（可以为空）
	 * @param filename
	 *            文件名（不能为空；必须带有后缀名）
	 * @return Content-Disposition的值
	 * @throws IllegalArgumentException
	 *             文件名为空
	 */
	public static String encodeFilename(HttpServletRequest request, String filename) {
		if (filename == null || "".equals(filename = filename.trim())) {
			throw new IllegalArgumentException("文件名不能为空！");
		}
		if (request == null || request.getHeader("User-Agent") == null) {
			return encodeFilename(filename);
		}

		String encodedName = null;
		try {
			encodedName = URLEncoder.encode(filename, "UTF-8").replace("+", "%20");

			String userAgent = request.getHeader("User-Agent").toLowerCase();
			if (userAgent.contains("msie") || userAgent.contains("trident")) {// IE
				// encodedName = URLEncoder.encode(filename, "UTF-8").replace("+", "%20");
			} else if (userAgent.contains("mozilla")) {// Firefox, Chrome, ...
				encodedName = new String(filename.getBytes("UTF-8"), "ISO-8859-1");
			}
		} catch (UnsupportedEncodingException e) {// 正常情况下不会出错
			logger.error("文件名编码出错！filename:" + filename, e);
			if (encodedName == null) {
				encodedName = filename;
			}
		}

		return "attachment; filename=\"" + encodedName + "\"";
	}

}
