package jnetwork;

import jnetwork.Graph;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExcelUtils {
	/**
	 * Write a graph to an excel file. Output file must be of format xls.
	 * 
	 * @param graph
	 * @param url
	 */
	public static <T> void writeGraph(Graph<T, ? extends Excelable> graph, String url) {
		writeEdges(graph.edges(), url);
	}

	/**
	 * Write a set of Excelable edges into an excel file. Output file must be of the
	 * format xls.
	 * 
	 * @param edges
	 * @param url
	 */
	public static void writeEdges(Collection<? extends Excelable> edges, String url) {
		String[] header = null;
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();

		for (Excelable e : edges) {
			header = e.header();
			break;
		}

		HSSFRow row = sheet.createRow(0);
		int cellnum = 0;
		for (String col : header) {
			row.createCell(cellnum++).setCellValue(col);
		}

		int rownum = 1;
		for (Excelable e : edges) {
			float[] attrs = e.items();
			cellnum = 0;
			row = sheet.createRow(rownum++);
			for (float attr : attrs) {
				row.createCell(cellnum++).setCellValue(attr);
			}
		}
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(url);
			workbook.write(out);
			workbook.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}
}
