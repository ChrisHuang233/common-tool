package com.huangwei.util;

import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFFormulaEvaluator;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Excel工具类
 */
public class ExcelUtil {
	protected static final Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

	/** 空字符串 */
	private static final String EMPTY_STRING = "";

	/**
	 * 判断Excel类型 by 文件
	 *
	 * @param file
	 *            文件（不能为空）
	 * @return NULL(参数为空/未知类型) 或 Excel类型
	 * @throws IOException
	 *             IO异常
	 */
	public static ExcelType typeOf(File file) throws IOException {
		if (file == null || !file.exists()) {
			logger.error("[判断Excel类型]文件不存在！file:" + file);
			return null;
		}
		if (file.isDirectory()) {
			logger.error("[判断Excel类型]路径指向一个文件夹而非Excel文件！file:" + file + " path:" + file.getAbsolutePath());
			return null;
		}

		FileMagic fileMagic = null;
		BufferedInputStream input = null;
		try {
			input = new BufferedInputStream(new FileInputStream(file));
			fileMagic = FileMagic.valueOf(input);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (Exception e) {
					// do nothing
				}
			}
		}
		return toExcelType(fileMagic);
	}

	/**
	 * 判断Excel类型 by 输入流
	 *
	 * @param input
	 *            输入流（不能为空）
	 * @return NULL(参数为空/未知类型) 或 Excel类型
	 * @throws IOException
	 *             IO异常
	 */
	public static ExcelType typeOf(InputStream input) throws IOException {
		if (input == null) {
			return null;
		}

		InputStream is = FileMagic.prepareToCheckMagic(input);
		return toExcelType(FileMagic.valueOf(is));
	}

	/**
	 * FileMagic -> Excel类型
	 *
	 * @param fileMagic
	 *            文件魔术编号
	 * @return NULL(匹配失败) 或 Excel类型
	 */
	private static ExcelType toExcelType(FileMagic fileMagic) {
		// 如果FileMagic的值是OLE2，则Excel文件为XLS格式
		if (FileMagic.OLE2.equals(fileMagic)) {
			return ExcelType.XLS;
		}
		// 如果FileMagic的值是OOXML，则Excel文件为XLSX格式
		if (FileMagic.OOXML.equals(fileMagic)) {
			return ExcelType.XLSX;
		}
		// 未知
		logger.warn("[FileMagic->Excel类型]类型不匹配...fileMagic:" + fileMagic);
		return null;
	}

	/**
	 * 文件 -> Excel工作簿
	 *
	 * @param file
	 *            文件（不能为空）
	 * @return NULL(参数错误/非Excel文件) 或 Excel工作簿
	 * @throws IOException
	 *             读写异常
	 */
	public static Workbook toWorkbook(File file) throws IOException {
		if (file == null || !file.exists()) {
			logger.error("[文件->Excel工作簿]文件不存在！file:" + file);
			return null;
		}
		if (file.isDirectory()) {
			logger.error("[文件->Excel工作簿]路径指向一个文件夹而非Excel文件！file:" + file + " path:" + file.getAbsolutePath());
			return null;
		}
		return toWorkbook(new FileInputStream(file));
	}

	/**
	 * 输入流 -> Excel工作簿<br>
	 * <br>
	 * 说明：请勿使用 POI 的“用户模式（org.apache.poi.ss.usermodel包相关实现类）”导入较大的Excel文件（数据量5W以上），以防发生OOM。
	 *
	 * @param input
	 *            输入流（不能为空）
	 * @return NULL(参数错误/非Excel文件) 或 Excel工作簿
	 * @throws IOException
	 *             读写异常
	 */
	public static Workbook toWorkbook(InputStream input) throws IOException {
		if (input == null) {
			logger.error("[输入流->Excel工作簿]输入流不能为空！input:" + input);
			return null;
		}

		// 使用缓冲流进行封装
		InputStream is = FileMagic.prepareToCheckMagic(input);
		// 判断文件类型
		ExcelType excelType = typeOf(is);
		// 创建工作簿
		if (ExcelType.XLS.equals(excelType)) {
			return new HSSFWorkbook(is);
		} else if (ExcelType.XLSX.equals(excelType)) {
			return new XSSFWorkbook(is);
		} else {
			logger.error("[输入流->Excel工作簿]未知文件类型！input:" + input + " excelType:" + excelType);
			is.close();
			return null;
		}
	}

	/**
	 * 获取公式计算器
	 *
	 * @param excel
	 *            Excel工作薄（不能为空）
	 * @return NULL(参数错误) 或 公式计算器
	 */
	public static FormulaEvaluator getFormulaEvaluator(Workbook excel) {
		if (excel == null) {
			return null;
		}
		// return excel.getCreationHelper().createFormulaEvaluator();
		FormulaEvaluator evaluator = null;
		if (excel instanceof HSSFWorkbook) {
			evaluator = new HSSFFormulaEvaluator((HSSFWorkbook) excel);
		} else if (excel instanceof XSSFWorkbook) {
			evaluator = new XSSFFormulaEvaluator((XSSFWorkbook) excel);
		} else if (excel instanceof SXSSFWorkbook) {
			evaluator = new SXSSFFormulaEvaluator((SXSSFWorkbook) excel);
		} else {
			logger.error("未知Workbook实例！workbookClass:" + excel.getClass());
		}
		return evaluator;
	}

	/**
	 * 单元格取值 - 字符串<br>
	 * <br>
	 * 说明：请使用 {@link ExcelUtil#dateValue(Cell, String)} 方法处理日期时间单元格。
	 *
	 * @param cell
	 *            Excel单元格（不能为空）
	 * @return 字符串值（不为NULL）
	 */
	public static String cellValue(Cell cell) {
		return cellValue(cell, null);
	}

	/**
	 * 单元格取值 - 字符串<br>
	 * <br>
	 * 说明：请使用 {@link ExcelUtil#dateValue(Cell, String)} 方法处理日期时间单元格。
	 *
	 * @param cell
	 *            Excel单元格（不能为空）
	 * @param evaluator
	 *            公式计算器（作用：计算公式单元格的值；可为空）
	 * @return 字符串值（不为NULL）
	 */
	public static String cellValue(Cell cell, FormulaEvaluator evaluator) {
		if (cell == null) {
			logger.error("[单元格取值]单元格不能为空！cell:" + cell + " evaluator:" + evaluator);
			return EMPTY_STRING;
		}

		String value = EMPTY_STRING;
		CellType cellType = cell.getCellTypeEnum();
		switch (cellType) {
		case NUMERIC:// 数字
			if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {// 日期时间
				value = _dateValue(cell);
			} else {
				// 使用BigDecimal包装再获取PlainString，可以防止获取到科学计数值
				value = BigDecimal.valueOf(cell.getNumericCellValue()).stripTrailingZeros().toPlainString();
			}
			break;
		case STRING:// 字符串
			value = cell.getStringCellValue();
			break;
		case FORMULA:// 公式
			if (evaluator == null) {
				value = formulaValue(cell);
			} else {
				value = evaluateFormula(cell, evaluator);
			}
			break;
		case BLANK:// 空值
			value = EMPTY_STRING;
			break;
		case BOOLEAN:// 布尔值
			value = String.valueOf(cell.getBooleanCellValue());
			break;
		case ERROR:// 错误
			if (cell instanceof XSSFCell) {
				value = ((XSSFCell) cell).getErrorCellString();
			} else {
				value = String.valueOf(cell.getErrorCellValue());
			}
			break;
		default:
			logger.error("[单元格取值]单元格类型未知！cell:" + cell + " evaluator:" + evaluator + " valueType:" + cellType);
			value = "UNKNOWN";
			break;
		}
		return value == null ? EMPTY_STRING : value.trim();
	}

	/**
	 * 单元格取值 - 日期时间字符串<br>
	 * <br>
	 * 说明：<br>
	 * ①如果单元格并非日期时间类型，则返回NULL；<br>
	 * ②不能确定具体时间格式，结果可能出现偏差。
	 *
	 * @param cell
	 *            单元格（不能为空）
	 * @return NULL 或 日期时间字符串
	 */
	private static String _dateValue(Cell cell) {
		if (cell == null) {
			logger.error("[获取日期时间]单元格不能为空！cell:" + cell);
			return null;
		}
		CellType cellType = cell.getCellTypeEnum();
		if (!CellType.NUMERIC.equals(cellType)) {
			logger.error("[获取日期时间]单元格并非日期时间类型！cell:" + cell + " cellType:" + cellType);
			return null;
		}

		return formatDate(cell.getDateCellValue(), cell.getCellStyle().getDataFormatString());
	}

	/**
	 * 格式化Excel日期时间
	 *
	 * @param date
	 *            日期时间（不能为空）
	 * @param dataFormat
	 *            单元格数据格式（注意：此参数并非 SimpleDateFormat 的格式化参数！）（不能为空）
	 * @return 日期时间字符串（不为NULL）
	 */
	public static String formatDate(Date date, String dataFormat) {
		if (date == null) {
			logger.error("[格式化Excel日期时间]日期时间不能为空！date:" + date + " dataFormat:" + dataFormat);
			return EMPTY_STRING;
		}

		String pattern;
		if (dataFormat == null) {
			pattern = "yyyy-MM-dd HH:mm:ss";
		} else if (dataFormat.contains("yy") || dataFormat.contains("mmm") || dataFormat.contains("d")
				|| dataFormat.contains("aa")) {
			if (dataFormat.contains(":mm")) {
				pattern = "yyyy-MM-dd HH:mm";
			} else {
				pattern = "yyyy-MM-dd";
			}
		} else if (dataFormat.contains(":mm")) {
			pattern = "HH:mm";
		} else {
			pattern = "yyyy-MM-dd HH:mm:ss";
		}
		try {
			return new SimpleDateFormat(pattern).format(date);
		} catch (Exception e) {
			logger.error("[格式化Excel日期时间]格式化出错！date:" + date + " dataFormat:" + dataFormat + " pattern:" + pattern, e);
			return null;
		}
	}

	/**
	 * 单元格取值 - 日期时间<br>
	 * <br>
	 * 说明：如果单元格并非日期时间格式，则返回NULL。
	 *
	 * @param cell
	 *            单元格（不能为空）
	 * @return NULL 或 日期时间
	 */
	public static Date dateValue(Cell cell) {
		if (cell == null) {
			logger.error("[获取日期时间]单元格不能为空！cell:" + cell);
			return null;
		}
		CellType cellType = cell.getCellTypeEnum();
		if (!CellType.NUMERIC.equals(cellType)) {
			logger.error("[获取日期时间]单元格并非日期时间类型！cell:" + cell + " cellType:" + cellType);
			return null;
		}

		return cell.getDateCellValue();
	}

	/**
	 * 单元格取值 - 日期时间字符串<br>
	 * <br>
	 * 说明：如果单元格并非日期时间格式，则返回NULL。
	 *
	 * @param cell
	 *            单元格（不能为空）
	 * @param pattern
	 *            (预期)时间格式（不能为空）
	 * @return NULL 或 日期时间字符串
	 * @throws IllegalArgumentException
	 *             时间格式为空
	 */
	public static String dateValue(Cell cell, String pattern) {
		if (cell == null) {
			logger.error("[获取日期时间]单元格不能为空！cell:" + cell + " pattern:" + pattern);
			return null;
		}
		if (pattern == null || "".equals(pattern = pattern.trim())) {
			logger.error("[获取日期时间]时间格式不能为空！cell:" + cell + " pattern:" + pattern);
			throw new IllegalArgumentException("时间格式不能为空！");
		}
		CellType cellType = cell.getCellTypeEnum();
		if (CellType.STRING.equals(cellType)) {
			logger.warn("[获取日期时间]单元格预期为日期时间类型，实际为字符串类型！cell:" + cell + " pattern:" + pattern + " cellType:" + cellType);
			return cell.getStringCellValue();
		}
		if (!CellType.NUMERIC.equals(cell.getCellTypeEnum())) {
			logger.error("[获取日期时间]单元格并非日期时间类型！cell:" + cell + " pattern:" + pattern + " cellType:" + cellType);
			return null;
		}

		try {
			return new SimpleDateFormat(pattern).format(cell.getDateCellValue());
		} catch (Exception e) {
			logger.error("[获取日期时间]格式化出错！cell:" + cell + " dataFormat:" + cell.getCellStyle().getDataFormatString()
					+ " pattern:" + pattern, e);
			return null;
		}
	}

	/**
	 * 单元格取值 - 计算公式
	 *
	 * @param cell
	 *            单元格（不能为空）
	 * @param evaluator
	 *            公式计算器（不能为空）
	 * @return 计算结果（不为NULL）
	 */
	private static String evaluateFormula(Cell cell, FormulaEvaluator evaluator) {
		if (cell == null) {
			logger.error("[计算公式]单元格不能为空！cell:" + cell + " evaluator:" + evaluator);
			return EMPTY_STRING;
		}
		CellType cellType = cell.getCellTypeEnum();
		if (!CellType.FORMULA.equals(cellType)) {
			logger.warn("[计算公式]单元格并非公式类型，转为常规方法取值！cell:" + cell + " evaluator:" + evaluator + " cellType:" + cellType);
			return cellValue(cell, evaluator);
		}
		if (evaluator == null) {
			logger.warn("[计算公式]公式计算器不存在，使用单元格缓存值！cell:" + cell + " evaluator:" + evaluator);
			return formulaValue(cell);
		}

		String value = EMPTY_STRING;
		CellValue cellValue = evaluator.evaluate(cell);
		cellType = cellValue.getCellTypeEnum();
		switch (cellType) {// 判断结果类型
		case NUMERIC:// 数字
			if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {// 日期时间
				value = _dateValue(cell);
			} else {
				value = BigDecimal.valueOf(cellValue.getNumberValue()).stripTrailingZeros().toPlainString();
			}
			break;
		case STRING:// 字符串
			value = cellValue.getStringValue();
			break;
		case FORMULA:// 公式
			value = cell.getCellFormula();
			break;
		case BLANK:// 空值
			value = EMPTY_STRING;
			break;
		case BOOLEAN:// 布尔值
			value = String.valueOf(cellValue.getBooleanValue());
			break;
		case ERROR:// 错误
			if (cell instanceof XSSFCell) {
				value = ((XSSFCell) cell).getErrorCellString();
			} else {
				value = String.valueOf(cellValue.getErrorValue());
			}
			break;
		default:
			logger.error("[计算公式]结果类型未知！cell:" + cell + " evaluator:" + evaluator + " valueType:" + cellType);
			value = "UNKNOWN";
			break;
		}
		return value;
	}

	/**
	 * 单元格取值 - 公式缓存值
	 *
	 * @param cell
	 *            单元格（不能为空）
	 * @return 字符串值（不为NULL）
	 */
	private static String formulaValue(Cell cell) {
		if (cell == null) {
			logger.error("[获取公式缓存值]单元格不能为空！cell:" + cell);
			return EMPTY_STRING;
		}
		CellType cellType = cell.getCellTypeEnum();
		if (!CellType.FORMULA.equals(cellType)) {
			logger.warn("[获取公式缓存值]单元格并非公式类型，转为常规方法取值！cell:" + cell + " cellType:" + cellType);
			return cellValue(cell, null);
		}

		String value = EMPTY_STRING;
		cellType = cell.getCachedFormulaResultTypeEnum();
		switch (cellType) {
		case NUMERIC:// 数字
			if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {// 日期时间
				value = _dateValue(cell);
			} else {
				value = BigDecimal.valueOf(cell.getNumericCellValue()).stripTrailingZeros().toPlainString();
			}
			break;
		case STRING:// 字符串
			value = cell.getStringCellValue();
			break;
		case BOOLEAN:// 布尔值
			value = String.valueOf(cell.getBooleanCellValue());
			break;
		case ERROR:// 错误
			if (cell instanceof XSSFCell) {
				value = ((XSSFCell) cell).getErrorCellString();
			} else {
				value = String.valueOf(cell.getErrorCellValue());
			}
			break;
		default:
			logger.error("[获取公式缓存值]缓存值类型未知！cell:" + cell + " valueType:" + cellType);
			value = "UNKNOWN";
			break;
		}
		return value;
	}

	/**
	 * Excel导出<br>
	 * <br>
	 * 说明：请尽量使用 SXSSFWorkbook 实例导出Excel。
	 *
	 * @param request
	 *            HTTP请求（可以为空）
	 * @param response
	 *            HTTP响应（不能为空）
	 * @param excel
	 *            Excel工作薄（不能为空）
	 * @param filename
	 *            文件名（为空：Excel_时间戳.xls/Excel_时间戳.xlsx）
	 * @throws IllegalArgumentException
	 *             HTTP响应为空 或 Excel工作薄为空
	 */
	public static void export(HttpServletRequest request, HttpServletResponse response, Workbook excel,
			String filename) {
		if (response == null) {
			throw new IllegalArgumentException("HTTP响应不能为空！");
		}
		if (excel == null) {
			throw new IllegalArgumentException("Excel工作薄不能为空！");
		}
		final String suffix = ((excel instanceof HSSFWorkbook) ? ExcelType.XLS : ExcelType.XLSX).suffix();
		if (filename == null || "".equals(filename = filename.trim())) {// 文件名为空
			filename = "Excel_" + System.currentTimeMillis() + suffix;
		} else if (!ExcelType.isExcel(filename)) {// 无后缀名
			filename += suffix;
		}

		logger.debug("[Excel导出]Begin...filename:{} currentTime:{}", filename, System.currentTimeMillis());
		OutputStream output = null;
		try {
			// 设置响应头
			setResponseHeader(request, response, filename);
			// 输出数据
			output = response.getOutputStream();
			excel.write(output);
			output.flush();
		} catch (Exception e) {
			logger.error("[Excel导出]出错！filename:{}", filename, e);
		} finally {
			if (excel instanceof SXSSFWorkbook) {
				((SXSSFWorkbook) excel).dispose();
				excel = null;
			}
		}
		logger.debug("[Excel导出]End...filename:{} currentTime:{}", filename, System.currentTimeMillis());
	}

	/**
	 * 设置响应头
	 *
	 * @param request
	 *            HTTP请求（可以为空）
	 * @param response
	 *            HTTP响应（不能为空）
	 * @param filename
	 *            文件名（不能为空）
	 */
	private static void setResponseHeader(HttpServletRequest request, HttpServletResponse response, String filename) {
		if (response == null || filename == null) {
			return;
		}
		// 设置响应头
		response.reset();
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Headers",
				"Accept, Accept-Language, Content-Language, Content-Type, Origin");
		response.setHeader("Access-Control-Allow-Origin",
				StringUtil.ifEmpty(request == null ? null : request.getHeader("Origin"), "*"));
		response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
		// response.setContentType("application/octet-stream");// 二进制流（通用）
		if (ExcelType.XLS.like(filename)) {// 旧版Excel（Office1997-2003）
			response.setContentType("application/vnd.ms-excel; charset=UTF-8");
		} else {
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet; charset=UTF-8");
		}
		// 以附件的形式下载
		String encodedName = JavaScriptUtil.encodeUri(filename);
		response.setHeader("Content-Disposition",
				"attachment; filename=\"" + encodedName + "\"; filename*=UTF-8''" + encodedName);
	}

	/**
	 * 单页Excel导出（简单样式）<br>
	 * <br>
	 * 说明：请勿使用此方法导出大量数据，以防发生OOM。
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
	 * Excel类型
	 */
	public static enum ExcelType {

		/** .xls格式（Office1997-2003） */
		XLS(".xls"),
		/** .xlsx格式 */
		XLSX(".xlsx");

		/** 后缀名（.xls/.xlsx） */
		private String suffix;
		/** 正则 */
		private String regex;

		private ExcelType(String suffix) {
			this.suffix = suffix;
			this.regex = "(?i).*\\" + suffix + "$";
		}

		/**
		 * 文件名 -> Excel类型
		 *
		 * @param filename
		 *            文件名（不能为空）
		 * @return NULL(匹配失败) 或 Excel类型
		 */
		public static ExcelType typeOf(String filename) {
			if (filename == null || "".equals(filename = filename.trim())) {
				return null;
			}

			for (ExcelType type : ExcelType.values()) {
				if (filename.matches(type.regex)) {
					return type;
				}
			}
			return null;
		}

		/**
		 * 是否是Excel文件
		 *
		 * @param filename
		 *            文件名（不能为空）
		 * @return true:是 false:否
		 */
		public static boolean isExcel(String filename) {
			if (filename == null || "".equals(filename = filename.trim())) {
				return false;
			}

			// for (ExcelType type : ExcelType.values()) {
			// if (filename.matches(type.regex)) {
			// return true;
			// }
			// }
			// return false;
			return filename.matches("(?i).*(\\.xls|\\.xlsx)$");
		}

		/** 后缀名（.xls/.xlsx） */
		public String suffix() {
			return suffix;
		}

		/**
		 * 是否同类
		 *
		 * @param filename
		 *            文件名（不能为空）
		 * @return true:是 false:否
		 */
		public boolean like(String filename) {
			if (filename == null || "".equals(filename = filename.trim())) {
				return false;
			}

			return filename.matches(regex);
		}

	}

}
