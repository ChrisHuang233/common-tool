package com.huangwei.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFReader.SheetIterator;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class ExcelXlsxReader extends DefaultHandler {

	private SharedStringsTable sst;
	private StylesTable stylesTable;
	private String lastContents;
	private int sheetIndex = -1;
	private CellDataType nextDataType = CellDataType.SSTINDEX;
	private final DataFormatter formatter = new DataFormatter();
	private short formatIndex;
	private String formatString;
	// 解析后的数据
	private final List<List<String>> sheetData = new ArrayList<List<String>>();
	private boolean allowNullRow = true;
	private String dimension;
	private List<String> currentRow;
	private boolean isSSTIndex = false;

	/**
	 * 遍历工作簿中所有的电子表格
	 *
	 * @param filename
	 * @throws IOException
	 * @throws OpenXML4JException
	 * @throws SAXException
	 * @throws Exception
	 */
	public void process(String filename) throws IOException, OpenXML4JException, SAXException {
		OPCPackage pkg = OPCPackage.open(filename);
		doProcess(pkg);
	}

	public void process(InputStream in) throws IOException, OpenXML4JException, SAXException {
		OPCPackage pkg = OPCPackage.open(in);
		doProcess(pkg);
	}

	private void doProcess(OPCPackage pkg) throws IOException, OpenXML4JException, SAXException {
		XSSFReader r = new XSSFReader(pkg);
		stylesTable = r.getStylesTable();
		SharedStringsTable sst = r.getSharedStringsTable();
		XMLReader parser = fetchSheetParser(sst);
		SheetIterator sheets = (SheetIterator) r.getSheetsData();
		while (sheets.hasNext()) {
			InputStream sheet = sheets.next();
			// String sheetName = sheets.getSheetName();
			sheetIndex++;
			InputSource sheetSource = new InputSource(sheet);
			parser.parse(sheetSource);
			sheet.close();
			break; // 只遍历第一个sheet页
		}
		// sheetData = null;
	}

	public XMLReader fetchSheetParser(SharedStringsTable sst) throws SAXException {
		XMLReader parser = XMLReaderFactory.createXMLReader("com.sun.org.apache.xerces.internal.parsers.SAXParser");
		this.sst = sst;
		parser.setContentHandler(this);
		return parser;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		// 得到单元格内容的值
		lastContents += new String(ch, start, length);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		if ("dimension".equals(qName)) {
			dimension = attributes.getValue("ref");
			int longest = covertRowIdtoInt(dimension.substring(dimension.indexOf(":") + 1));
		}
		// 行
		if ("row".equals(qName)) {
			currentRow = new ArrayList<String>();
		}
		// 单元格
		if ("c".equals(qName)) {
			// 判断单元格的值是SST的索引
			if (attributes.getValue("t") != null && "s".equals(attributes.getValue("t"))) {
				isSSTIndex = true;
				nextDataType = CellDataType.SSTINDEX;
			} else {
				isSSTIndex = false;
				// 当单元格的值不是SST的索引放一个空值占位。
				currentRow.add("");
				// 判断单元格格式类型
				setNextDataType(attributes);
			}
		}
		lastContents = "";

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		// 行结束,存储一行数据
		if ("row".equals(qName)) {
			if (isWriteRow(currentRow)) {
				List<String> rowList = new ArrayList<String>(currentRow);
				sheetData.add(rowList);
			}
		}
		// 取值
		if ("v".equals(qName)) {
			// 单元格的值是SST的索引
			if (isSSTIndex) {
				lastContents = getDataValue(lastContents);
			} else {
				// 单元格的值不是SST的索引,删除占位空格，再取值
				currentRow.remove(currentRow.size() - 1);
				lastContents = getDataValue(lastContents);
			}
			currentRow.add(lastContents);
		}
	}

	/**
	 * 单元格中的数据类型枚举
	 */
	enum CellDataType {
		SSTINDEX,
		NUMBER,
		DATE,
		TIME,
		NULL
	}

	/**
	 * 在取值前处理单元格数据类型，目前只对日期格式进行处理，可扩展。
	 */
	public void setNextDataType(Attributes attributes) {
		nextDataType = CellDataType.NUMBER;
		formatIndex = -1;
		formatString = null;
		Set<String> dateFormatSet = new HashSet<String>() {
			{
				add("m/d/yy");
				add("yyyy-mm-dd;@");
				add("yyyy/m/d;@");
				add("yyyy/m/d h:mm;@");
				add("mm/dd/yy;@");
				add("m/d;@");
				add("yy/m/d;@");
				add("m/d/yy;@");
				add("[$-409]yyyy/m/d h:mm AM/PM;@");
				add("[$-F800]dddd");
				add(" mmmm dd");
				add(" yyyy");
			}
		};
		Set<String> timeFormatSet = new HashSet<String>() {
			{
				add("h:mm");
				add("h:mm;@");
			}
		};
		// String cellType = attributes.getValue("t");
		String cellStyleStr = attributes.getValue("s");
		if (cellStyleStr != null) {
			int styleIndex = Integer.parseInt(cellStyleStr);
			XSSFCellStyle style = stylesTable.getStyleAt(styleIndex);
			formatIndex = style.getDataFormat();
			formatString = style.getDataFormatString();
			// 对日期类型进行处理
			if (timeFormatSet.contains(formatString)) {
				nextDataType = CellDataType.TIME;
				formatString = "H:mm";
			} else if (dateFormatSet.contains(formatString)) {
				nextDataType = CellDataType.DATE;
				formatString = "yyyy-MM-dd";
			} else {
				nextDataType = CellDataType.NULL;
				formatString = BuiltinFormats.getBuiltinFormat(formatIndex);
			}
		}
	}

	private boolean isWriteRow(List<String> list) {
		boolean flag = false;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) != null && !"".equals(list.get(i))) {
				flag = true;
				break;
			}
		}
		if (allowNullRow) {
			if (!flag) {
				flag = true;// 允许一次空行
				allowNullRow = false;
			}
		}
		return flag;

	}

	public static int covertRowIdtoInt(String rowId) {
		int firstDigit = -1;
		for (int c = 0; c < rowId.length(); ++c) {
			if (Character.isDigit(rowId.charAt(c))) {
				firstDigit = c;
				break;
			}
		}
		String newRowId = rowId.substring(0, firstDigit);
		int num = 0;
		int result = 0;
		int length = newRowId.length();
		for (int i = 0; i < length; i++) {
			char ch = newRowId.charAt(length - i - 1);
			num = (int) (ch - 'A' + 1);
			num *= Math.pow(26, i);
			result += num;
		}
		return result;
	}

	/**
	 * 对解析出来的数据进行类型处理
	 */
	public String getDataValue(String value) {
		String thisStr = null;
		switch (nextDataType) {
		case SSTINDEX:
			String sstIndex = value;
			try {
				int idx = Integer.parseInt(sstIndex);
				XSSFRichTextString rtss = new XSSFRichTextString(sst.getEntryAt(idx));
				thisStr = rtss.toString();
				rtss = null;
			} catch (NumberFormatException ex) {
				thisStr = value;
			}
			break;
		case DATE:
			thisStr = formatter.formatRawCellContents(Double.parseDouble(value), formatIndex, formatString);
			break;
		case TIME:
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(formatString);
				Date javaDate = DateUtil.getJavaDate(Double.parseDouble(value));
				thisStr = sdf.format(javaDate);
			} catch (Exception e) {
				thisStr = value;
			}
			break;
		default:
			thisStr = value;
			break;
		}

		return thisStr;
	}

	public List<List<String>> getSheetData() {
		return sheetData;
	}

}