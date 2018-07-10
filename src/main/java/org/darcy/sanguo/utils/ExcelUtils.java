package org.darcy.sanguo.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.darcy.ServerStartup;

/**
 * Excel加载文件内容工具类
 */
public class ExcelUtils {

	/**
	 * 根据文件名称，获取excel文件的内容，返回Row的列表
	 * 
	 * @param fileName
	 *            excel文件的路径，如：data/commons/effect.xls
	 */
	public static List<Row> getRowList(String fileName) {
		return getRowList(fileName, 1);
	}

	/**
	 * 根据文件名称，获取excel文件的内容，返回Row的列表
	 * 
	 * @param fileName
	 *            excel文件的路径，如：effect.xls
	 * 
	 * @param type
	 *            1：commons； 2：server
	 */
	public static List<Row> getRowList(String fileName, Integer type) {
		return getRowList(fileName, type, 0);
	}

	/**
	 * 根据文件名称，获取excel文件的内容，返回Row的列表
	 * 
	 * @param fileName
	 *            excel文件的路径，如：effect.xls
	 * @param type
	 *            1：commons； 2：server
	 * @param sheetIndex
	 *            excel文件的sheet下标
	 */
	public static List<Row> getRowList(String fileName, Integer type, Integer sheetIndex) {
		String prefix = "data/commons/";
		if (type == 2)
			prefix = "data/server/";
		List<Row> result = new ArrayList<Row>();
		HSSFWorkbook book = null;
		try {
			URL url = ServerStartup.class.getClassLoader().getResource(prefix + fileName);
			File f = new File(url.getFile());
			book = new HSSFWorkbook(new FileInputStream(f));
			HSSFSheet sheet = book.getSheetAt(sheetIndex);
			int rowIndex = 0;// 行号
			for (Iterator<Row> iter = (Iterator<Row>) sheet.rowIterator(); iter.hasNext();) {
				Row row = iter.next();
				rowIndex++;
				// commons目录，第1,2,3行是表头，非数据，跳过
				if (type == 1 && rowIndex <= 3) {
					continue;
				}
				// server目录，第1行是表头，非数据，跳过
				if (type == 2 && rowIndex <= 1) {
					continue;
				}
				result.add(row);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				book.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 根据文件名称，获取网关配置信息
	 * 
	 * @param fileName
	 *            excel文件的路径，如：effect.xls
	 */
	public static List<Row> getGateInfo(String fileName) {
		String prefix = "cfg/gate/";
		List<Row> result = new ArrayList<Row>();
		HSSFWorkbook book = null;
		try {
			URL url = ServerStartup.class.getClassLoader().getResource(prefix + fileName);
			File f = new File(url.getFile());
			book = new HSSFWorkbook(new FileInputStream(f));
			HSSFSheet sheet = book.getSheetAt(0);
			int rowIndex = 0;// 行号
			for (Iterator<Row> iter = (Iterator<Row>) sheet.rowIterator(); iter.hasNext();) {
				Row row = iter.next();
				rowIndex++;
				// 第1行是表头，非数据，跳过
				if (rowIndex <= 1) {
					continue;
				}
				result.add(row);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				book.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

}
