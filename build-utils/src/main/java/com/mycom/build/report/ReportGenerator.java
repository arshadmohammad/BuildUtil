package com.mycom.build.report;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.mycom.build.model.ConfigParamModel;
import com.mycom.build.writer.ExcelWriteUtil;

public class ReportGenerator {
	private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
	public static void main(String[] args) {
		System.out.println(dateFormat.format(new Date()));
	}
	

	public static void writeConfigParamReport(List<ConfigParamModel> baseParams, List<ConfigParamModel> currentParams,
			List<ConfigParamModel> deleteFromBase, List<ConfigParamModel> newInCurrent, String baseReleaseName,
			String currentReleaseName) {

		Workbook wb = new HSSFWorkbook();
		CellStyle headerStyle = ExcelWriteUtil.getHeaderStyle(wb);
		String[] coloums = new String[] { "Jar Name", "Class", "Configuration", "Configuration Value" };

		// base release configurations
		Sheet baseSheet = wb.createSheet(baseReleaseName);
		ExcelWriteUtil.createHeader(baseSheet, coloums, headerStyle);
		addDataRow(wb, baseSheet, baseParams);
		ExcelWriteUtil.autoFitColumnSizes(baseSheet, coloums.length);

		// current release configurations
		Sheet currentSheet = wb.createSheet(currentReleaseName);
		ExcelWriteUtil.createHeader(currentSheet, coloums, headerStyle);
		addDataRow(wb, currentSheet, currentParams);
		ExcelWriteUtil.autoFitColumnSizes(currentSheet, coloums.length);

		// configurations deleted from base
		Sheet baseDeleteSheet = wb.createSheet(baseReleaseName + "(Deleted)");
		ExcelWriteUtil.createHeader(baseDeleteSheet, coloums, headerStyle);
		addDataRow(wb, baseDeleteSheet, deleteFromBase);
		ExcelWriteUtil.autoFitColumnSizes(baseDeleteSheet, coloums.length);

		// configurations added in current
		Sheet addedInCurrentSheet = wb.createSheet(baseReleaseName + "(Added)");
		ExcelWriteUtil.createHeader(addedInCurrentSheet, coloums, headerStyle);
		addDataRow(wb, addedInCurrentSheet, newInCurrent);
		ExcelWriteUtil.autoFitColumnSizes(addedInCurrentSheet, coloums.length);

		String fileName = "ConfigurationParameterAnalysis" + dateFormat.format(new Date()) + ".xls";
		ExcelWriteUtil.autoFitColumnSizes(baseSheet, coloums.length);
		ExcelWriteUtil.writeWorkbook(wb, fileName);
	}

	private static void addDataRow(Workbook wb, Sheet sheet, List<ConfigParamModel> baseParams) {
		int rowIndex = 1;
		CellStyle rowStyle = ExcelWriteUtil.getCellStyle(wb);
		for (ConfigParamModel configParamModel : baseParams) {
			Row row = sheet.createRow(rowIndex++);
			row.setRowStyle(rowStyle);

			Cell cell0 = row.createCell(0);
			cell0.setCellValue(configParamModel.jarName);

			Cell cell1 = row.createCell(1);
			cell1.setCellValue(configParamModel.className);

			Cell cell2 = row.createCell(2);
			cell2.setCellValue(configParamModel.fieldName);

			Cell cell3 = row.createCell(3);
			cell3.setCellValue(configParamModel.fieldValue);
		}
	}

}
