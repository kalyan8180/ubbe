package com.example.test.testingHMS.Excel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.example.test.testingHMS.patient.model.PatientRegistration;

public class ExcelView {

	public static ByteArrayInputStream customersToExcel(List<PatientRegistration> customers, String path) throws IOException  {

		String[] COLUMNs = {"RegId", "Name", "Reg. Date", "Payment"};
		try(
				Workbook workbook = new XSSFWorkbook();
				ByteArrayOutputStream out = new ByteArrayOutputStream();
		){
			CreationHelper createHelper = workbook.getCreationHelper();
	 
			Sheet sheet = workbook.createSheet("Customers");
	 
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setColor(IndexedColors.BLUE.getIndex());
	 
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFont(headerFont);
	 
			// Row for Header
			Row headerRow = sheet.createRow(0);
	 
			// Header
			for (int col = 0; col < COLUMNs.length; col++) {
				Cell cell = headerRow.createCell(col);
				cell.setCellValue(COLUMNs[col]);
				cell.setCellStyle(headerCellStyle);
			}
	 
			// CellStyle for Age
			CellStyle ageCellStyle = workbook.createCellStyle();
			ageCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("#"));
	 
			int rowIdx = 1;
			for (PatientRegistration customer : customers) {
				Row row = sheet.createRow(rowIdx++);
	 
				row.createCell(0).setCellValue(customer.getRegId());
				row.createCell(1).setCellValue(customer.getPatientDetails().getTitle()+". "+customer.getPatientDetails().getFirstName()+" "+customer.getPatientDetails().getLastName());
				row.createCell(2).setCellValue(customer.getRegDate().toString().substring(0,10));
				row.createCell(3).setCellValue(customer.getAdvanceAmount());
	 
				/*Cell ageCell = row.createCell(3);
				ageCell.setCellValue(customer.getPatientDetails().getAge());
				ageCell.setCellStyle(ageCellStyle);*/
			}
	 
			workbook.write(out);
			
			FileOutputStream fileOut = new FileOutputStream(path+"ap.xls");
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();
            System.out.println("Your excel file has been generated!");
			return new ByteArrayInputStream(out.toByteArray());
			
		}
	}

}