package com.mycom.build.writer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class ExcelWriteUtil {

	public static CellStyle getHeaderStyle(Workbook wb) {
		short color = HSSFColor.GREEN.index;
		CellStyle style = getCellStyle(wb, color);
		Font font = wb.createFont();
		font.setFontHeightInPoints((short) 14);
		style.setFont(font);
		return style;
	}

	public static CellStyle getCellStyle(Workbook wb, short color) {
		CellStyle style = wb.createCellStyle();
		setBorders(style);
		style.setFillForegroundColor(color);
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		return style;
	}

	public static CellStyle getYellowCellStyle(Workbook wb) {
		short color = HSSFColor.YELLOW.index;
		return getCellStyle(wb, color);
	}

	public static CellStyle getRedCellStyle(Workbook wb) {
		short color = HSSFColor.RED.index;
		return getCellStyle(wb, color);
	}

	public static CellStyle getCellStyle(Workbook wb) {
		short color = HSSFColor.WHITE.index;
		return getCellStyle(wb, color);
	}

	private static void setBorders(CellStyle style) {
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(HSSFColor.BLACK.index);
		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setLeftBorderColor(HSSFColor.BLACK.index);
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setRightBorderColor(HSSFColor.BLACK.index);
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setTopBorderColor(HSSFColor.BLACK.index);
	}

	public static void createHeader(Sheet sheet, String[] coloums, CellStyle headerStyle) {
		Row row = sheet.createRow(0);
		for (int i = 0; i < coloums.length; i++) {
			String columnName = coloums[i];
			Cell cell = row.createCell(i);
			cell.setCellStyle(headerStyle);
			cell.setCellValue(columnName);
		}
	}

	public static void writeWorkbook(Workbook wb, String fileName) {
		FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream(fileName);
			wb.write(fileOut);
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void autoFitColumnSizes(Sheet sheet, int numberOfColumns) {
		for (int i = 0; i < numberOfColumns; i++) {
			sheet.autoSizeColumn(i);
		}
	}

}
