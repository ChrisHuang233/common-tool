package com.huangwei.excel;

import static org.apache.poi.xssf.usermodel.XSSFRelation.NS_SPREADSHEETML;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.model.CommentsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTComment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.huangwei.util.ExcelUtil;

/**
 * Excel解析器 - .xlsx格式专用<br>
 * <br>
 * 说明：此解析器参考 {@link org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler} 实现，并做些许调整。
 */
public class XlsxHandler extends DefaultHandler {
	protected static final Logger logger = LoggerFactory.getLogger(XlsxHandler.class);

	/**
	 * These are the different kinds of cells we support. We keep track of the current one between the start and end.
	 */
	enum XssfDataType {
		BOOLEAN, ERROR, FORMULA, INLINE_STRING, SST_STRING, NUMBER;
	}

	/**
	 * Table with the styles used for formatting
	 */
	private StylesTable stylesTable;

	/**
	 * Table with cell comments
	 */
	private CommentsTable commentsTable;

	/**
	 * Read only access to the shared strings table, for looking up (most) string cell's contents
	 */
	private ReadOnlySharedStringsTable sharedStringsTable;

	/** 解析回调 */
	private final SheetCallback output;
	/** 数据格式器 */
	private final DataFormatter formatter;
	/** 公式单元格取值方式（true:公式 false:结果） */
	private boolean formulasNotResults;

	// Set when V start element is seen
	private boolean vIsOpen;
	// Set when F start element is seen
	private boolean fIsOpen;
	// Set when an Inline String "is" is seen
	private boolean isIsOpen;
	// Set when a header/footer element is seen
	private boolean hfIsOpen;

	// Set when cell start element is seen;
	// used when cell close element is seen.
	private XssfDataType nextDataType;

	// Used to format numeric cell values.
	private short formatIndex;
	private String formatString;
	/** Sheet有效列数（-1:不确定 >=0:列数） */
	private int columnSize = -1;
	/** 当前行索引（-1:无数据 >=0:行索引） */
	private int rowIndex = -1;
	/** 下一行索引 */
	private int nextRowIndex = 0;// some sheets do not have rowNums, Excel can read them so we should try to handle them
									// correctly as well
	/** 上个单元格索引（从0开始） */
	private int lastCellIndex = 0;
	/** 当前单元格索引（从0开始） */
	private int cellIndex = 0;
	/** 当前单元格坐标 */
	private String cellRef;
	/** (单元格)字符串值 */
	private String strValue = "";
	/** (单元格)Object值（允许的实例：String, Double, Date, Boolean） */
	private Object objValue = null;
	/** (单元格)备注 */
	private XSSFComment cellComment = null;

	// Gathers characters as they are seen.
	private StringBuffer value = new StringBuffer();
	private StringBuffer formula = new StringBuffer();
	private StringBuffer headerFooter = new StringBuffer();

	private Queue<CellAddress> commentCellRefs;

	/**
	 * Excel解析器 - .xlsx格式专用
	 * 
	 * @param styles
	 *            样式表
	 * @param comments
	 *            注释表
	 * @param strings
	 *            字符串表
	 * @param dataFormatter
	 *            数据格式器
	 * @param formulasNotResults
	 *            公式单元格取值方式（true:公式 false:结果）
	 * @param callback
	 *            解析回调
	 */
	public XlsxHandler(StylesTable styles, CommentsTable comments, ReadOnlySharedStringsTable strings,
			DataFormatter dataFormatter, boolean formulasNotResults, SheetCallback callback) {
		this.stylesTable = styles;
		this.commentsTable = comments;
		this.sharedStringsTable = strings;
		this.formatter = dataFormatter;
		this.formulasNotResults = formulasNotResults;
		this.output = callback;
		this.nextDataType = XssfDataType.NUMBER;
		init();
	}

	/**
	 * Excel解析器 - .xlsx格式专用
	 * 
	 * @param styles
	 *            样式表
	 * @param strings
	 *            字符串表
	 * @param dataFormatter
	 *            数据格式器
	 * @param formulasNotResults
	 *            公式单元格取值方式（true:公式 false:结果）
	 * @param callback
	 *            解析回调
	 */
	public XlsxHandler(StylesTable styles, ReadOnlySharedStringsTable strings, DataFormatter dataFormatter,
			boolean formulasNotResults, SheetCallback callback) {
		this(styles, null, strings, dataFormatter, formulasNotResults, callback);
	}

	/**
	 * Excel解析器 - .xlsx格式专用
	 * 
	 * @param styles
	 *            样式表
	 * @param strings
	 *            字符串表
	 * @param formulasNotResults
	 *            公式单元格取值方式（true:公式 false:结果）
	 * @param callback
	 *            解析回调
	 */
	public XlsxHandler(StylesTable styles, ReadOnlySharedStringsTable strings, boolean formulasNotResults,
			SheetCallback callback) {
		this(styles, strings, new DataFormatter(), formulasNotResults, callback);
	}

	private void init() {
		if (commentsTable != null) {
			commentCellRefs = new LinkedList<CellAddress>();
			// noinspection deprecation
			for (CTComment comment : commentsTable.getCTComments().getCommentList().getCommentArray()) {
				commentCellRefs.add(new CellAddress(comment.getRef()));
			}
		}
	}

	private boolean isTextTag(String name) {
		if ("v".equals(name)) {
			// Easy, normal v text tag
			return true;
		}
		if ("inlineStr".equals(name)) {
			// Easy inline string
			return true;
		}
		if ("t".equals(name) && isIsOpen) {
			// Inline string <is><t>...</t></is> pair
			return true;
		}
		// It isn't a text tag
		return false;
	}

	@Override
	@SuppressWarnings("unused")
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (uri != null && !NS_SPREADSHEETML.equals(uri)) {
			return;
		}

		if (isTextTag(localName)) {
			vIsOpen = true;
			// Clear contents cache
			value.setLength(0);
		} else if ("dimension".equals(localName)) {// Sheet尺寸
			String ref = attributes.getValue("ref");
			if (ref != null && !"".equals(ref)) {
				if (ref.contains(":")) {
					String[] array = ref.split(":");
					if (array.length == 2) {
						CellAddress begin = new CellAddress(array[0]);
						CellAddress end = new CellAddress(array[1]);
						columnSize = end.getColumn() - begin.getColumn() + 1;
					}
				} else {
					columnSize = 1;
				}
			}
		} else if ("is".equals(localName)) {
			// Inline string outer tag
			isIsOpen = true;
		} else if ("f".equals(localName)) {
			// Clear contents cache
			formula.setLength(0);

			// Mark us as being a formula if not already
			if (nextDataType == XssfDataType.NUMBER) {
				nextDataType = XssfDataType.FORMULA;
			}

			// Decide where to get the formula string from
			String type = attributes.getValue("t");
			if (type != null && "shared".equals(type)) {
				// Is it the one that defines the shared, or uses it?
				String ref = attributes.getValue("ref");
				String si = attributes.getValue("si");
				if (ref != null) {
					// This one defines it
					// TODO Save it somewhere
					fIsOpen = true;
				} else {
					// This one uses a shared formula
					// TODO Retrieve the shared formula and tweak it to
					// match the current cell
					if (formulasNotResults) {
						logger.warn("shared formulas not yet supported!");
					}
					/*
					 * else { // It's a shared formula, so we can't get at the formula string yet // However, they don't
					 * care about the formula string, so that's ok! }
					 */
				}
			} else {
				fIsOpen = true;
			}
		} else if ("oddHeader".equals(localName) || "evenHeader".equals(localName) || "firstHeader".equals(localName)
				|| "firstFooter".equals(localName) || "oddFooter".equals(localName) || "evenFooter".equals(localName)) {
			hfIsOpen = true;
			// Clear contents cache
			headerFooter.setLength(0);
		} else if ("row".equals(localName)) {
			String rowNumStr = attributes.getValue("r");
			if (rowNumStr != null) {
				rowIndex = Integer.parseInt(rowNumStr) - 1;
			} else {
				rowIndex = nextRowIndex;
			}
			output.rowStart(rowIndex, columnSize);
		} else if ("c".equals(localName)) {// c => cell
			// Set up defaults.
			this.nextDataType = XssfDataType.NUMBER;
			this.formatIndex = -1;
			this.formatString = null;
			this.cellRef = attributes.getValue("r");
			this.lastCellIndex = cellIndex;
			this.cellIndex = new CellAddress(cellRef).getColumn();
			this.strValue = "";
			this.objValue = null;
			this.cellComment = null;
			String cellType = attributes.getValue("t");
			String cellStyleStr = attributes.getValue("s");
			if ("b".equals(cellType)) {
				nextDataType = XssfDataType.BOOLEAN;
			} else if ("e".equals(cellType)) {
				nextDataType = XssfDataType.ERROR;
			} else if ("inlineStr".equals(cellType)) {
				nextDataType = XssfDataType.INLINE_STRING;
			} else if ("s".equals(cellType)) {
				nextDataType = XssfDataType.SST_STRING;
			} else if ("str".equals(cellType)) {
				nextDataType = XssfDataType.FORMULA;
			} else {
				// Number, but almost certainly with a special style or format
				XSSFCellStyle style = null;
				if (stylesTable != null) {
					if (cellStyleStr != null) {
						int styleIndex = Integer.parseInt(cellStyleStr);
						style = stylesTable.getStyleAt(styleIndex);
					} else if (stylesTable.getNumCellStyles() > 0) {
						style = stylesTable.getStyleAt(0);
					}
				}
				if (style != null) {
					this.formatIndex = style.getDataFormat();
					this.formatString = style.getDataFormatString();
					if (this.formatString == null) {
						this.formatString = BuiltinFormats.getBuiltinFormat(this.formatIndex);
					}
				}
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (uri != null && !NS_SPREADSHEETML.equals(uri)) {
			return;
		}

		// v => contents of a cell
		if (isTextTag(localName)) {
			vIsOpen = false;
			// Process the value contents as required, now we have it all
			switch (nextDataType) {
			case BOOLEAN:
				char first = value.charAt(0);
				strValue = first == '0' ? "FALSE" : "TRUE";
				objValue = Boolean.valueOf(strValue);
				break;
			case ERROR:
				strValue = "ERROR:" + value;
				objValue = strValue;
				break;
			case INLINE_STRING:
				// TODO: Can these ever have formatting on them?
				XSSFRichTextString rtsi = new XSSFRichTextString(value.toString());
				strValue = rtsi.toString();
				objValue = strValue;
				break;
			case SST_STRING:
				String sstIndex = value.toString();
				try {
					int idx = Integer.parseInt(sstIndex);
					XSSFRichTextString rtss = new XSSFRichTextString(sharedStringsTable.getEntryAt(idx));
					strValue = rtss.toString();
					objValue = strValue;
				} catch (NumberFormatException e) {
					logger.error("Failed to parse SST index '" + sstIndex, e);
				}
				break;
			case FORMULA:
				if (formulasNotResults) {
					strValue = formula.toString();
					objValue = strValue;
				} else {
					String fv = value.toString();
					if (this.formatString != null) {
						try {
							// Try to use the value as a formattable number
							convertNumericValue(fv, this.formatIndex, this.formatString);
						} catch (Exception e) {
							// Formula is a String result not a Numeric one
							strValue = fv;
							objValue = strValue.isEmpty() ? null : strValue;
						}
					} else {
						// No formatting applied, just do raw value in all cases
						strValue = fv;
						objValue = strValue.isEmpty() ? null : strValue;
					}
				}
				break;
			case NUMBER:
				String n = value.toString();
				if (this.formatString != null && n.length() > 0) {
					convertNumericValue(n, this.formatIndex, this.formatString);
				} else {
					strValue = n;
					objValue = strValue.isEmpty() ? null : strValue;
				}
				break;
			default:
				strValue = "(TODO: Unexpected type: " + nextDataType + ")";
				objValue = strValue;
				break;
			}

			// Do we have a comment for this cell?
			checkForEmptyCellComments(EmptyCellCommentsCheckType.CELL);
			cellComment = commentsTable != null ? commentsTable.findCellComment(new CellAddress(cellRef)) : null;
		} else if ("is".equals(localName)) {
			isIsOpen = false;
		} else if ("f".equals(localName)) {
			fIsOpen = false;
		} else if ("c".equals(localName)) {// cell
			fIsOpen = false;
			// 对齐列
			if (cellIndex - lastCellIndex > 1) {
				while (lastCellIndex + 1 < cellIndex) {
					outputEmptyCell(++lastCellIndex);
				}
			}
			output.cell(cellIndex, cellRef, strValue, objValue, cellComment);
		} else if ("row".equals(localName)) {
			// 对齐列
			if (columnSize > 0 && columnSize - cellIndex > 1) {
				while (cellIndex + 1 < columnSize) {
					outputEmptyCell(++cellIndex);
				}
			}
			// Handle any "missing" cells which had comments attached
			checkForEmptyCellComments(EmptyCellCommentsCheckType.END_OF_ROW);

			// Finish up the row
			output.rowEnd(rowIndex);

			// some sheets do not have rowNum set in the XML, Excel can read them so we should try to read them as well
			nextRowIndex = rowIndex + 1;
		} else if ("sheetData".equals(localName)) {
			// Handle any "missing" cells which had comments attached
			checkForEmptyCellComments(EmptyCellCommentsCheckType.END_OF_SHEET_DATA);
		} else if ("oddHeader".equals(localName) || "evenHeader".equals(localName) || "firstHeader".equals(localName)) {
			hfIsOpen = false;
			output.header(headerFooter.toString(), localName);
		} else if ("oddFooter".equals(localName) || "evenFooter".equals(localName) || "firstFooter".equals(localName)) {
			hfIsOpen = false;
			output.footer(headerFooter.toString(), localName);
		} else if ("worksheet".equals(localName)) {
			output.sheetEnd(rowIndex + 1);
		}
	}

	/**
	 * Captures characters only if a suitable element is open. Originally was just "v"; extended for inlineStr also.
	 */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (vIsOpen) {
			value.append(ch, start, length);
		}
		if (fIsOpen) {
			formula.append(ch, start, length);
		}
		if (hfIsOpen) {
			headerFooter.append(ch, start, length);
		}
	}

	/**
	 * 单元格值转换 - 数字类型
	 * 
	 * @param value
	 *            数字字符串
	 * @param formatIndex
	 *            (数据)格式索引
	 * @param formatString
	 *            (数据)格式字符串
	 */
	private void convertNumericValue(String value, int formatIndex, String formatString) {
		if (value == null || value.isEmpty()) {
			this.strValue = "";
			this.objValue = null;
			return;
		}

		double d = Double.parseDouble(value);
		if (org.apache.poi.ss.usermodel.DateUtil.isADateFormat(formatIndex, formatString)) {
			Date date = org.apache.poi.ss.usermodel.DateUtil.getJavaDate(d);
			this.strValue = ExcelUtil.formatDate(date, formatString);
			this.objValue = date;
		} else {
			String v = formatter.formatRawCellContents(d, formatIndex, formatString).replaceAll("\\(|\\)", "");
			if (value.length() < v.length()) {
				v = value;
			}
			// 使用BigDecimal包装再获取PlainString，可以防止获取到科学计数值
			this.strValue = new BigDecimal(v).stripTrailingZeros().toPlainString();
			this.objValue = Double.valueOf(strValue);
		}
	}

	/**
	 * Do a check for, and output, comments in otherwise empty cells.
	 */
	private void checkForEmptyCellComments(EmptyCellCommentsCheckType type) {
		if (commentCellRefs != null && !commentCellRefs.isEmpty()) {
			// If we've reached the end of the sheet data, output any
			// comments we haven't yet already handled
			if (type == EmptyCellCommentsCheckType.END_OF_SHEET_DATA) {
				while (!commentCellRefs.isEmpty()) {
					outputEmptyCellComment(commentCellRefs.remove());
				}
				return;
			}

			// At the end of a row, handle any comments for "missing" rows before us
			if (this.cellRef == null) {
				if (type == EmptyCellCommentsCheckType.END_OF_ROW) {
					while (!commentCellRefs.isEmpty()) {
						if (commentCellRefs.peek().getRow() == rowIndex) {
							outputEmptyCellComment(commentCellRefs.remove());
						} else {
							return;
						}
					}
					return;
				} else {
					throw new IllegalStateException(
							"Cell ref should be null only if there are only empty cells in the row; rowNum: "
									+ rowIndex);
				}
			}

			CellAddress nextCommentCellRef;
			do {
				CellAddress cellRef = new CellAddress(this.cellRef);
				CellAddress peekCellRef = commentCellRefs.peek();
				if (type == EmptyCellCommentsCheckType.CELL && cellRef.equals(peekCellRef)) {
					// remove the comment cell ref from the list if we're about to handle it alongside the cell content
					commentCellRefs.remove();
					return;
				} else {
					// fill in any gaps if there are empty cells with comment mixed in with non-empty cells
					int comparison = peekCellRef.compareTo(cellRef);
					if (comparison > 0 && type == EmptyCellCommentsCheckType.END_OF_ROW
							&& peekCellRef.getRow() <= rowIndex) {
						nextCommentCellRef = commentCellRefs.remove();
						outputEmptyCellComment(nextCommentCellRef);
					} else if (comparison < 0 && type == EmptyCellCommentsCheckType.CELL
							&& peekCellRef.getRow() <= rowIndex) {
						nextCommentCellRef = commentCellRefs.remove();
						outputEmptyCellComment(nextCommentCellRef);
					} else {
						nextCommentCellRef = null;
					}
				}
			} while (nextCommentCellRef != null && !commentCellRefs.isEmpty());
		}
	}

	/**
	 * 输出空单元格
	 * 
	 * @param cellIndex
	 *            单元格索引（从0开始）
	 */
	private void outputEmptyCell(int cellIndex) {
		output.cell(cellIndex, CellReference.convertNumToColString(cellIndex) + (rowIndex + 1), "", null, null);
	}

	/**
	 * Output an empty-cell comment.
	 */
	private void outputEmptyCellComment(CellAddress cellRef) {
		XSSFComment comment = commentsTable.findCellComment(cellRef);
		output.cell(cellRef.getColumn(), cellRef.formatAsString(), "", null, comment);
	}

	private enum EmptyCellCommentsCheckType {
		CELL, END_OF_ROW, END_OF_SHEET_DATA
	}

}
