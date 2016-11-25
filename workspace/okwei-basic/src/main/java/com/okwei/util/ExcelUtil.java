package com.okwei.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.biff.EmptyCell;
import jxl.format.Alignment;
import jxl.format.CellFormat;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.NumberFormat;
import jxl.write.WritableCell;
import jxl.write.WritableCellFeatures;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;


/**
 * JXL操作Excel公共类
 * 
 * 1、导出excel：数据为：list-ObjectBean，每个list为一行，ObjectBean需有get方法
 * 2、读取excel：读入JVM的数据为：list<Object>；实体bean需有set方法，且属性顺序与excel中行一致
 * 
 * @ClassName: ExcelUtils
 * @Description: TODO
 * @author xiehz
 * @date 2014年7月4日 上午11:09:23
 *
 */
public class ExcelUtil {

	private final static String EXCEL_DATE_FORMAT = "EXCEL_DATE_FORMAT";
	private final static String EXCEL_TITLE_FORMAT = "EXCEL_TITLE_FORMAT";

	private WritableWorkbook workbook; // 用来创建excel文件
	private WritableSheet sheet; // 工作表
	@SuppressWarnings("unchecked")
	private Map format; // 存放显示风格

	@SuppressWarnings("unchecked")
	public ExcelUtil() {
		format = new HashMap();
		format.put(EXCEL_DATE_FORMAT, "yyyy-MM-dd hh:mm:ss"); // 时间显示格式
		format.put(CellType.BOOLEAN, this.getDataCellFormat(CellType.BOOLEAN));
		format.put(CellType.NUMBER, this.getDataCellFormat(CellType.NUMBER));
		format.put(CellType.DATE, this.getDataCellFormat(CellType.DATE));
		format.put(CellType.STRING_FORMULA, this.getDataCellFormat(CellType.STRING_FORMULA));
		format.put(EXCEL_TITLE_FORMAT, this.getTitleCellFormat());
	}

	/**
	 * 初始化表格属性
	 * 
	 * @param sheet
	 */
	public void initialSheetSetting(WritableSheet sheet) {
		try {
			sheet.getSettings().setDefaultColumnWidth(15); // 设置列的默认宽度
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 插入公式
	 * 
	 * @param sheet
	 * @param col
	 * @param row
	 * @param formula
	 * @param format
	 */
	public void insertFormula(WritableSheet sheet, Integer col, Integer row, String formula, WritableCellFormat format) {
		try {
			Formula f = new Formula(col, row, formula, format);
			sheet.addCell(f);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 插入一行数据
	 * 
	 * @param sheet
	 *            工作表
	 * @param row
	 *            行号
	 * @param data
	 *            内容 内容类型必须为String
	 * @param format
	 *            风格
	 */
	public void insertRowData(WritableSheet sheet, Integer row, List<String> data, WritableCellFormat format) {
		try {
			Label label;
			for (int i = 0; i < data.size(); i++) {
				label = new Label(i, row, (String) data.get(i), format);
				sheet.addCell(label);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 插入一行数据
	 * 
	 * @param sheet
	 *            工作表
	 * @param row
	 *            行号
	 * @param data
	 *            内容
	 */
	public void insertRowData(WritableSheet sheet, Integer row, Object data) {
		try {
			// 取实体的属性
			Field fields[] = data.getClass().getDeclaredFields();
			if (fields != null && fields.length > 0) {
				for (int i = 0; i < fields.length; i++) {
					// 取属性的具体值
					Object o = ObjectUtil.getPropertyValue(data, fields[i].getName());
					// 取属性的类型
					String typeName = fields[i].getType().getSimpleName();
					WritableCell cell = this.getWritableCell(i, row, o, typeName);
					// 添加到表格上
					sheet.addCell(cell);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据不同的属性类型，返回相应的WritableCell
	 * 
	 * @param col
	 *            列号
	 * @param row
	 *            行号
	 * @param data
	 *            内容
	 * @param typeName
	 *            类型
	 * @return 与类型一致的cell
	 */
	private WritableCell getWritableCell(Integer col, Integer row, Object data, String typeName) {
		WritableCell result = null;
		if (data != null) {
			if ("int".equals(typeName) || "Interger".equals(typeName) || "double".equals(typeName) || "Double".equals(typeName) || "long".equals(typeName)
					|| "Long".equals(typeName) || "float".equals(typeName) || "Float".equals(typeName)) {
				result = new Number(col, row, (Integer) data, (CellFormat) format.get(CellType.NUMBER));
			} else if ("boolean".equals(typeName) || "Boolean".equals(typeName)) {
				result = new jxl.write.Boolean(col, row, (Boolean) data, (CellFormat) format.get(CellType.BOOLEAN));
			} else if ("Date".equals(typeName)) {
				result = new jxl.write.DateTime(col, row, (Date) data, (CellFormat) format.get(CellType.DATE));
			} else {
				result = new Label(col, row, data.toString(), (CellFormat) format.get(CellType.STRING_FORMULA));
			}
		} else {
			result = new EmptyCell(col, row);
		}
		return result;
	}

	/**
	 * 插入单元格数据
	 * 
	 * @param sheet
	 * @param col
	 * @param row
	 * @param data
	 */
	public void insertOneCellData(WritableSheet sheet, Integer col, Integer row, Object data, WritableCellFormat format) {
		try {
			if (data instanceof Double) {
				jxl.write.Number labelNF = new jxl.write.Number(col, row, (Double) data, format);
				sheet.addCell(labelNF);
			} else if (data instanceof java.lang.Boolean) {

				jxl.write.Boolean labelB = new jxl.write.Boolean(col, row, (java.lang.Boolean) data, format);
				sheet.addCell(labelB);
			} else if (data instanceof Date) {
				jxl.write.DateTime labelDT = new jxl.write.DateTime(col, row, (Date) data, format);
				sheet.addCell(labelDT);
				setCellComments(labelDT, "这是个创建表的日期说明！"); // 给单元格加注释(公共方法)
			} else {
				Label label = new Label(col, row, data.toString(), format);
				sheet.addCell(label);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 合并单元格，并插入数据
	 * 
	 * @param sheet
	 * @param col_start
	 * @param row_start
	 * @param col_end
	 * @param row_end
	 * @param data
	 * @param format
	 */
	public void mergeCellsAndInsertData(WritableSheet sheet, Integer col_start, Integer row_start, Integer col_end, Integer row_end, Object data,
			WritableCellFormat format) {
		try {
			sheet.mergeCells(col_start, row_start, col_end, row_end);// 左上角到右下角
			insertOneCellData(sheet, col_start, row_start, data, format);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 给单元格加注释
	 * 
	 * @param label
	 *            对象
	 * @param comments
	 *            注释内容
	 */
	public void setCellComments(Object label, String comments) {
		WritableCellFeatures cellFeatures = new WritableCellFeatures();
		cellFeatures.setComment(comments);
		if (label instanceof jxl.write.Number) {
			jxl.write.Number num = (jxl.write.Number) label;
			num.setCellFeatures(cellFeatures);
		} else if (label instanceof jxl.write.Boolean) {
			jxl.write.Boolean bool = (jxl.write.Boolean) label;
			bool.setCellFeatures(cellFeatures);
		} else if (label instanceof jxl.write.DateTime) {
			jxl.write.DateTime dt = (jxl.write.DateTime) label;
			dt.setCellFeatures(cellFeatures);
		} else {
			Label _label = (Label) label;
			_label.setCellFeatures(cellFeatures);
		}
	}

	/**
	 * 读取Excel文件，把数据放到list中
	 * 
	 * @param inputFile
	 *            要读取的excel文件
	 * @param inputFileSheetIndex
	 *            读第几个工作表
	 * @param rowNum
	 *            从第几行数据开始
	 * @param bean
	 *            以那个Bean读取
	 * @return 返回一个List，list中放Class对象
	 */
	@SuppressWarnings("unchecked")
	public List<Object> readDataFromExcel(InputStream inputFile, int inputFileSheetIndex, int rowNum, Class bean) {

		List<Object> list = new ArrayList<Object>();
		Workbook book = null;
		Cell cell = null;
		WorkbookSettings setting = new WorkbookSettings();
		// java.util.Locale locale = new java.util.Locale("zh", "CN"); // 本地
		// setting.setLocale(locale);
		// setting.setEncoding("ISO-8859-1"); // 设置字符集编码
		try {
			book = Workbook.getWorkbook(inputFile, setting);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			Object tmpObject = null;
			Sheet sheet = book.getSheet(inputFileSheetIndex);
			for (int rowIndex = rowNum; rowIndex < sheet.getRows(); rowIndex++) {// 行
				// 实例一个bean
				tmpObject = bean.newInstance();
				// bean的属性，注意：bean中的属性顺序应该与表格中的属性一致。
				Field[] fields = bean.getDeclaredFields();
				for (int colIndex = 0; colIndex < sheet.getColumns(); colIndex++) {// 列
					cell = sheet.getCell(colIndex, rowIndex);
					// 给tmpObject设置属性值
					ObjectUtil.setProperty(tmpObject, fields[colIndex].getName(), getParameter(fields[colIndex], cell.getContents()));
				}
				// 把tmpObject添加到list中
				list.add(tmpObject);
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		book.close();
		return list;
	}

	/**
	 * 把字符串转换成相应的类型的对象
	 * 
	 * @param field
	 *            属性
	 * @param msg
	 *            值
	 * @return 字符串转换成与属性类型一致的对象
	 */
	@SuppressWarnings("deprecation")
	private Object getParameter(Field field, String msg) {
		Object result = null;

		try {
			String typeName = field.getType().getSimpleName();
			if ("int".equals(typeName) || "Integer".equals(typeName)) {
				if (msg.indexOf(".") > 0) {
					msg = msg.substring(0, msg.indexOf("."));
				}
				result = Integer.parseInt(msg);
			} else if ("double".equals(typeName) || "Double".equals(typeName)) {
				result = Double.parseDouble(msg);
			} else if ("boolean".equals(typeName) || "Boolean".equals(typeName)) {
				result = Boolean.parseBoolean(msg);
			} else if ("Date".equals(typeName)) {
				DateFormat dateFormat = new SimpleDateFormat((String) format.get(ExcelUtil.EXCEL_DATE_FORMAT));
				result = dateFormat.parse(msg);
			} else {
				result = msg;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 得到数据表头格式
	 * 
	 * @return
	 */
	public WritableCellFormat getTitleCellFormat() {
		WritableCellFormat wcf = null;
		try {
			// 字体样式(字体,大小,是否粗体,是否斜体)
			WritableFont wf = new WritableFont(WritableFont.TIMES, 11, WritableFont.BOLD, false);
			wcf = new WritableCellFormat(wf);// 实例化文字格式化
			// 对齐方式
			wcf.setAlignment(Alignment.CENTRE); // 水平
			wcf.setVerticalAlignment(VerticalAlignment.CENTRE); // 垂直
		} catch (WriteException e) {
			e.printStackTrace();
		}
		return wcf;
	}

	/**
	 * 得到数据格式(默认左对齐)
	 * 
	 * @return
	 */
	public WritableCellFormat getDataCellFormat(CellType type) {
		WritableCellFormat wcf = null;
		try {
			// 字体样式
			if (type == CellType.NUMBER || type == CellType.NUMBER_FORMULA) {// 数字
				NumberFormat nf = new NumberFormat("#.00"); // 保留小数点后两位
				wcf = new WritableCellFormat(nf);
			} else if (type == CellType.DATE || type == CellType.DATE_FORMULA) {// 日期
				jxl.write.DateFormat df = new jxl.write.DateFormat((String) format.get(EXCEL_DATE_FORMAT)); // 时间显示格式
				wcf = new jxl.write.WritableCellFormat(df);
			} else {
				WritableFont wf = new WritableFont(WritableFont.TIMES, 11, WritableFont.NO_BOLD, false);// 字体样式(字体,大小,是否粗体,是否斜体)
				wcf = new WritableCellFormat(wf);
			}
			// 对齐方式
			wcf.setAlignment(Alignment.LEFT);
			wcf.setVerticalAlignment(VerticalAlignment.CENTRE);

			wcf.setWrap(false);// 自动换行
		} catch (WriteException e) {
			e.printStackTrace();
		}

		return wcf;
	}

	/**
	 * 得到数据格式(重载) 可以设置水平对齐模式
	 * 
	 * @param type
	 * @param align
	 *            设置水平对齐模式
	 * @return
	 */
	public WritableCellFormat getDataCellFormat(CellType type, Alignment align) {
		WritableCellFormat wcf = null;
		try {
			// 字体样式
			if (type == CellType.NUMBER || type == CellType.NUMBER_FORMULA) {// 数字
				NumberFormat nf = new NumberFormat("#.00"); // 保留小数点后两位
				wcf = new WritableCellFormat(nf);
			} else if (type == CellType.DATE || type == CellType.DATE_FORMULA) {// 日期
				jxl.write.DateFormat df = new jxl.write.DateFormat((String) format.get(CellType.DATE)); // 时间显示格式
				wcf = new jxl.write.WritableCellFormat(df);
			} else {
				WritableFont wf = new WritableFont(WritableFont.TIMES, 12, WritableFont.NO_BOLD, false);// 字体样式(字体,大小,是否粗体,是否斜体)
				wcf = new WritableCellFormat(wf);
			}
			// 对齐方式
			wcf.setAlignment(align);
			wcf.setVerticalAlignment(VerticalAlignment.CENTRE);

			wcf.setWrap(true);// 自动换行
		} catch (WriteException e) {
			e.printStackTrace();
		}

		return wcf;
	}

	/**
	 * 创建目录
	 * 
	 * @param destDirName
	 *            目录路径
	 */
	public static void createDir(String destDirName) {
		File dir = new File(destDirName);
		// 如果目录不存在则创建目录
		if (!dir.exists()) {
			if (!destDirName.endsWith(File.separator))
				destDirName = destDirName + File.separator;
			// 创建单个目录
			if (dir.mkdirs()) {
				System.out.println("创建目录" + destDirName + "成功！");
			} else {
				System.out.println("创建目录" + destDirName + "成功！");
			}
		}

	}

	/**
	 * 生成并关闭工作簿
	 */
	public void writeAndClose() {
		try {
			workbook.write();
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 生成Excel文件(适合一个标题,剩余全是数据)
	 * 
	 * @param os
	 *            创建excel的流
	 * @param sheetName
	 *            工作表名称
	 * @param dataTitles
	 *            数据标题
	 * @param datas
	 *            数据--list中放行的内容，用list
	 */
	public void createExcelFile(OutputStream os, String sheetName, List<String> dataTitles, List<Object> datas) {

		try {

			// OutputStream os = new FileOutputStream(path);// 输出流指定文件路径
			workbook = Workbook.createWorkbook(os);// 创建工作薄
			sheet = workbook.createSheet(sheetName, 0); // 添加第一个工作表
			initialSheetSetting(sheet);// 初始化表格属性(公共方法)

			// 添加数据标题
			insertRowData(sheet, 0, dataTitles, getTitleCellFormat());

			// 插入一行 (公共方法)
			for (int i = 0; i < datas.size(); i++) {
				Object data = datas.get(i);
				insertRowData(sheet, i + 1, data);
			}

			workbook.write();
			workbook.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置表格显示风格
	 * 
	 * @param cellType
	 *            用来设置表头显示风格，需为：EXCEL_TITLE_FORMAT或日期显示格式
	 * @param cellFormat
	 *            自己定义WritableCellFormat显示风格
	 */
	@SuppressWarnings("unchecked")
	public void setCellFormat(String cellType, WritableCellFormat cellFormat) {
		if (this.format != null && cellFormat != null) {
			try {
				if (format.get(cellType) == null) {
					throw new Exception();
				} else {
					format.remove(cellType);
					format.put(cellType, cellFormat);
				}
			} catch (Exception e) {
				System.out.println("风格设置不正确。key应为：CellType或ExcelUtil的静态属性");
			}
		}
	}

	/**
	 * 设置显示风格
	 * 
	 * @param cellType
	 *            用来设置不同数据类型的显示格式，为CellType
	 * @param cellFormat
	 *            自己定义WritableCellFormat显示风格
	 */
	@SuppressWarnings("unchecked")
	public void setCellFormat(CellType cellType, WritableCellFormat cellFormat) {
		if (this.format != null && cellFormat != null) {
			try {
				if (format.get(cellType) == null) {
					throw new Exception();
				} else {
					format.remove(cellType);
					format.put(cellType, cellFormat);
				}
			} catch (Exception e) {
				System.out.println("风格设置不正确。key应为：CellType或ExcelUtil的静态属性");
			}
		}
	}

	/**
	 * 
	 * @Title: exportElcel
	 * @Description:exportTile是sheet的名字，整个标题的名字，titleColumn要显示的列的数组，responseData单元格中填充的数据 
	 *                                                                                    orderMap顺序显示关系map
	 * @param @param resp
	 * @param @param exportTitle
	 * @param @param titleColumn
	 * @param @param responseData
	 * @param @throws Exception
	 * @return void
	 */
	public static void exportElcel(HttpServletResponse resp, String exportTitle, String[] titleColumn, List<Map<String, String>> responseData,
			Map<Integer, String> orderMap) throws Exception {
		String strTime = "";
		try {
			SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyyMMddhhmmss");
			strTime = simpledateformat.format(new Date());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		strTime += exportTitle;
		resp.reset();
		resp.setContentType("application/x-msdownload");
		resp.setHeader("Content-Disposition", "attachment; filename=" + new String(strTime.getBytes("utf-8"), "ISO8859-1") + ".xls");
		ServletOutputStream fileOut = null;
		WritableWorkbook book = null;
		WritableCellFormat wcfLeft = new WritableCellFormat();
		wcfLeft.setVerticalAlignment(VerticalAlignment.CENTRE);
		wcfLeft.setAlignment(Alignment.LEFT);
		wcfLeft.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
		try {
			fileOut = resp.getOutputStream();
			book = jxl.Workbook.createWorkbook(fileOut);
			WritableSheet sheet = book.createSheet(exportTitle, 0);
			Label label = null; // 用于保存文本cell
			int titleRowCount = sheet.getRows(); // 记录表头占用几行
			WritableFont titleFont = new WritableFont(WritableFont.COURIER, 12, WritableFont.BOLD, false);// 写第一行
			titleFont.setColour(Colour.DARK_BLUE);
			WritableCellFormat titleCellFormat = new WritableCellFormat(titleFont);
			titleCellFormat.setAlignment(Alignment.CENTRE);
			label = new Label(0, 0, exportTitle, titleCellFormat);
			sheet.mergeCells(0, 0, 6, 0);
			sheet.addCell(label);
			for (int lie = 0; lie < titleColumn.length; lie++) {
				label = new Label(lie, 1, titleColumn[lie], titleCellFormat);
				sheet.addCell(label);
			}
			titleRowCount = 2;
			for (int i = 0; i < responseData.size(); i++) {
				Map<String, String> report = responseData.get(i);
				for (int j = 0; j < orderMap.size(); j++) {
					String key = orderMap.get(j);
					label = new Label(j, i + titleRowCount, report.get(key), wcfLeft);
					sheet.addCell(label);
				}
			}
			book.write();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (book != null) {
				book.close();
			}
			if (fileOut != null) {
				fileOut.close();
			}
		}
	}

	// 测试
	/*public static void main(String[] args) throws FileNotFoundException {

		ExcelUtil jxl = new ExcelUtil(); // 创建目录
		ExcelUtil.createDir("C:/ExcelTEMP/"); // 文件路径
		String filePath = "C:/ExcelTEMP/test.xls";

		List<Object> lists = new ArrayList<Object>();
		for (int i = 0; i < 20; i++) {
			Bean bean = new Bean();
			bean.setId(i + 1);
			bean.setUsername("adsf**" + i + "***");
			if (i % 2 == 0) {
				bean.setFlag(true);
				bean.setSex("女");
			} else {
				bean.setFlag(false);
				bean.setSex("男");
			}
			bean.setCreateTime(new Date());

			lists.add(bean);
		}

		List<String> title = new ArrayList<String>();
		title.add("编号");
		title.add("姓名");
		title.add("性别");
		title.add("创建日期");
		title.add("正常否");

		jxl.createExcelFile(new FileOutputStream(filePath), "成绩单", title, lists);
		// 读excel文件
		List<Object> list = jxl.readDataFromExcel(new FileInputStream("c:/ExcelTEMP/test.xls"), 0, 1, Bean.class);
		for (int i = 0; i < list.size(); i++) {
			CommonUtil.print(list.get(i));
		}
	}*/

}
