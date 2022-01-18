package com.huangwei.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.util.SAXHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.StylesTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.huangwei.util.ExcelUtil;
import com.huangwei.util.ExcelUtil.ExcelType;

/**
 * Excel解读器<br>
 * <br>
 * 说明：此工具类使用 POI 的“事件模式（org.apache.poi.xssf.eventusermodel包相关类）”实现Excel文件的读取和解析。
 */
public class ExcelReader {
	protected static final Logger logger = LoggerFactory.getLogger(ExcelReader.class);

	/** 空字符串 */
	private static final String EMPTY_STRING = "";

	/**
	 * 读取Excel - 第一个Sheet
	 * 
	 * @param input
	 *            输入流（不能为空）
	 * @param callback
	 *            回调函数（不能为空）
	 * @throws Exception
	 *             异常
	 */
	public static void read(InputStream input, SheetCallback callback) throws Exception {
		if (input == null) {
			logger.error("[读取Excel]输入流不能为空！input:" + input + " callback:" + callback);
			throw new IllegalArgumentException("输入流不能为空！");
		}
		if (callback == null) {
			logger.error("[读取Excel]回调函数不能为空！input:" + input + " callback:" + callback);
			throw new IllegalArgumentException("回调函数不能为空！");
		}

		read(input, new int[] { 1 }, callback);
	}

	/**
	 * 读取Excel - 某个Sheet
	 * 
	 * @param input
	 *            输入流（不能为空）
	 * @param sheetNo
	 *            目标Sheet编号（从1开始）
	 * @param callback
	 *            回调函数（不能为空）
	 * @throws Exception
	 *             异常
	 */
	public static void read(InputStream input, int sheetNo, SheetCallback callback) throws Exception {
		if (input == null) {
			logger.error("[读取Excel]输入流不能为空！input:" + input + " callback:" + callback);
			throw new IllegalArgumentException("输入流不能为空！");
		}
		if (callback == null) {
			logger.error("[读取Excel]回调函数不能为空！input:" + input + " callback:" + callback);
			throw new IllegalArgumentException("回调函数不能为空！");
		}
		if (sheetNo < 1) {
			sheetNo = 1;
		}

		read(input, new int[] { sheetNo }, callback);
	}

	/**
	 * 读取Excel
	 * 
	 * @param path
	 *            文件路径（不能为空）
	 * @param sheetNumbers
	 *            目标Sheet编号（从1开始；为空：读取所有）
	 * @param callback
	 *            回调函数（不能为空）
	 * @throws Exception
	 *             异常
	 */
	public static void read(String path, int[] sheetNumbers, SheetCallback callback) throws Exception {
		if (path == null || "".equals(path = path.trim())) {
			logger.error("[读取Excel]文件路径不能为空！path:" + path + " sheetNumbers:" + Arrays.toString(sheetNumbers)
					+ " callback:" + callback);
			throw new IllegalArgumentException("文件路径不能为空！");
		}
		File file = new File(path);
		if (!file.exists()) {
			logger.error("[读取Excel]文件不存在！path:" + path + " sheetNumbers:" + Arrays.toString(sheetNumbers) + " callback:"
					+ callback);
			throw new IllegalArgumentException("文件不存在！");
		}
		if (file.isDirectory()) {
			logger.error("[读取Excel]路径指向一个文件夹而非Excel文件！path:" + path + " path:" + file.getAbsolutePath()
					+ " sheetNumbers:" + Arrays.toString(sheetNumbers) + " callback:" + callback);
			throw new IllegalArgumentException("路径指向一个文件夹而非Excel文件！");
		}
		if (callback == null) {
			logger.error("[读取Excel]回调函数不能为空！path:" + path + " sheetNumbers:" + Arrays.toString(sheetNumbers)
					+ " callback:" + callback);
			throw new IllegalArgumentException("回调函数不能为空！");
		}

		final ExcelReader reader = new ExcelReader();
		reader.process(new FileInputStream(file), sheetNumbers, callback);
	}

	/**
	 * 读取Excel
	 * 
	 * @param file
	 *            文件（不能为空）
	 * @param sheetNumbers
	 *            目标Sheet编号（从1开始；为空：读取所有）
	 * @param callback
	 *            回调函数（不能为空）
	 * @throws Exception
	 *             异常
	 */
	public static void read(File file, int[] sheetNumbers, SheetCallback callback) throws Exception {
		if (file == null || !file.exists()) {
			logger.error("[读取Excel]文件不存在！file:" + file + " sheetNumbers:" + Arrays.toString(sheetNumbers) + " callback:"
					+ callback);
			throw new IllegalArgumentException("文件不存在！");
		}
		if (file.isDirectory()) {
			logger.error("[读取Excel]路径指向一个文件夹而非Excel文件！file:" + file + " path:" + file.getAbsolutePath()
					+ " sheetNumbers:" + Arrays.toString(sheetNumbers) + " callback:" + callback);
			throw new IllegalArgumentException("路径指向一个文件夹而非Excel文件！");
		}
		if (callback == null) {
			logger.error("[读取Excel]回调函数不能为空！file:" + file + " sheetNumbers:" + Arrays.toString(sheetNumbers)
					+ " callback:" + callback);
			throw new IllegalArgumentException("回调函数不能为空！");
		}

		final ExcelReader reader = new ExcelReader();
		reader.process(new FileInputStream(file), sheetNumbers, callback);
	}

	/**
	 * 读取Excel
	 * 
	 * @param input
	 *            输入流（不能为空）
	 * @param sheetNumbers
	 *            目标Sheet编号（从1开始；为空：读取所有）
	 * @param callback
	 *            回调函数（不能为空）
	 * @throws Exception
	 *             异常
	 */
	public static void read(InputStream input, int[] sheetNumbers, SheetCallback callback) throws Exception {
		if (input == null) {
			logger.error("[读取Excel]输入流不能为空！input:" + input + " sheetNumbers:" + Arrays.toString(sheetNumbers)
					+ " callback:" + callback);
			throw new IllegalArgumentException("输入流不能为空！");
		}
		if (callback == null) {
			logger.error("[读取Excel]回调函数不能为空！input:" + input + " sheetNumbers:" + Arrays.toString(sheetNumbers)
					+ " callback:" + callback);
			throw new IllegalArgumentException("回调函数不能为空！");
		}

		final ExcelReader reader = new ExcelReader();
		reader.process(input, sheetNumbers, callback);
	}

	/**
	 * 处理Excel
	 * 
	 * @param input
	 *            输入流（不能为空）
	 * @param sheetNumbers
	 *            目标Sheet编号（从1开始；为空：读取所有）
	 * @param callback
	 *            回调函数（不能为空）
	 * @throws Exception
	 *             异常
	 */
	private void process(InputStream input, int[] sheetNumbers, SheetCallback callback) throws Exception {
		if (input == null) {
			logger.error("[读取Excel]输入流不能为空！input:" + input + " sheetNumbers:" + Arrays.toString(sheetNumbers)
					+ " callback:" + callback);
			throw new IllegalArgumentException("输入流不能为空！");
		}
		if (callback == null) {
			logger.error("[读取Excel]回调函数不能为空！input:" + input + " sheetNumbers:" + Arrays.toString(sheetNumbers)
					+ " callback:" + callback);
			throw new IllegalArgumentException("回调函数不能为空！");
		}

		InputStream is = FileMagic.prepareToCheckMagic(input);
		ExcelType type = ExcelUtil.typeOf(is);
		if (ExcelType.XLS.equals(type)) {
			// TODO 读取XLS
			logger.error("[读取Excel]暂未实现.xls格式Excel文件的解析！input:" + input + " sheetNumbers:"
					+ Arrays.toString(sheetNumbers) + " callback:" + callback + " type:" + type);
		} else if (ExcelType.XLSX.equals(type)) {
			readXlsx(OPCPackage.open(is), sheetNumbers, callback);
		} else {
			logger.error("[读取Excel]未知文件类型！input:" + input + " sheetNumbers:" + Arrays.toString(sheetNumbers)
					+ " callback:" + callback + " type:" + type);
		}
	}

	/**
	 * 读取XLSX
	 * 
	 * @param pkg
	 *            源（不能为空）
	 * @param sheetNumbers
	 *            目标Sheet编号（从1开始；为空：读取所有）
	 * @param callback
	 *            回调函数（不能为空）
	 * @throws Exception
	 *             异常
	 */
	private void readXlsx(OPCPackage pkg, int[] sheetNumbers, SheetCallback callback) throws Exception {
		if (pkg == null || callback == null) {
			return;
		}
		try {
			XSSFReader reader = new XSSFReader(pkg);
			StylesTable styles = reader.getStylesTable();
			ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(pkg);
			XSSFReader.SheetIterator sheets = (XSSFReader.SheetIterator) reader.getSheetsData();
			InputStream sheet = null;
			String sheetName;
			XMLReader parser;
			int sheetNumber = 0;// Sheet数量/Sheet编号
			while (sheets.hasNext()) {
				sheetNumber++;
				try {
					sheet = sheets.next();
					sheetName = sheets.getSheetName();
					if (sheetNumbers != null && !contains(sheetNumbers, sheetNumber)) {
						continue;
					}

					logger.debug("[读取Excel]解析Sheet开始...sheetNo:" + sheetNumber + " sheetName:" + sheetName);
					parser = SAXHelper.newXMLReader();
//					parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
					parser.setContentHandler(new XlsxHandler(styles, strings, false, callback));
					parser.parse(new InputSource(sheet));
					logger.debug("[读取Excel]解析Sheet结束...sheetNo:" + sheetNumber + " sheetName:" + sheetName);
				} catch (Exception e) {
					logger.error("[读取Excel]出错！", e);
				} finally {
					if (sheet != null) {
						try {
							sheet.close();
						} catch (Exception e) {
							// ignore
						}
					}
				}
			}
			if (sheetNumber < 1) {
				logger.warn("[读取Excel]工作薄应至少含有一个工作表！sheetNumber:" + sheetNumber);
			}
		} finally {
			if (pkg != null) {
				try {
					pkg.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}

	/**
	 * 对象值 -> 字符串值
	 * 
	 * @param objValue
	 *            对象值
	 * @return 字符串值（不为NULL）
	 */
	public static String stringValue(Object objValue) {
		return stringValue(objValue, null);
	}

	/**
	 * 对象值 -> 字符串值
	 * 
	 * @param objValue
	 *            对象值
	 * @param pattern
	 *            (预期)时间格式（当且仅当值为 java.util.Date 实例时有效）
	 * @return 字符串值（不为NULL）
	 */
	public static String stringValue(Object objValue, final String pattern) {
		if (objValue == null) {
			return EMPTY_STRING;
		}

		if (objValue instanceof Date) {
			String p = pattern;
			if (p == null || "".equals(p = p.trim())) {
				if (objValue instanceof java.sql.Date) {
					p = "yyyy-MM-dd";
				} else {
					p = "yyyy-MM-dd HH:mm:ss";
				}
			}
			try {
				return new SimpleDateFormat(p).format((Date) objValue);
			} catch (Exception e) {
				logger.error("[对象值 -> 字符串值]格式化出错！objValue:" + objValue + " pattern:" + p, e);
				return EMPTY_STRING;
			}
		} else if (objValue.getClass().isArray()) {
			return Arrays.toString((Object[]) objValue);
		} else {
			return objValue.toString();
		}
	}

	/**
	 * 数组是否包含目标
	 * 
	 * @param array
	 *            数组
	 * @param valueToFind
	 *            查找目标
	 * @return true:包含 false:不包含
	 */
	private static boolean contains(final int[] array, final int valueToFind) {
		if (array == null) {
			return false;
		}

		for (int i = 0, length = array.length; i < length; i++) {
			if (valueToFind == array[i]) {
				return true;
			}
		}
		return false;
	}

}
