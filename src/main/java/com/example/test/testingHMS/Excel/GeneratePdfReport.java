package com.example.test.testingHMS.Excel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class GeneratePdfReport {

    public static ByteArrayInputStream citiesReport(List<PatientRegistration> cities,String path) {

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        document.open();
     
        Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
    	Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
    	
    	
    	
		
        
        try {

        	/*Paragraph p=new Paragraph();
        	p.add("Patient Details");
        	document.add(p);
        	*/
        	
        	
        	Paragraph p51 = new Paragraph("PATIENT DETAILS", headFont);
			p51.setAlignment(Element.ALIGN_CENTER);

			document.add(p51);
        	
        	
        	PdfPCell cell=new PdfPCell();
        	// cell.setBorder(Rectangle.BOX);
        	
        	PdfPTable table96 = new PdfPTable(1);
			table96.setWidths(new float[] { 5f });
			table96.setSpacingBefore(10);

			PdfPCell hcell96;
			hcell96 = new PdfPCell(new Phrase("PATIENT DETAILS", headFont));
			hcell96.setBorder(Rectangle.NO_BORDER);
			hcell96.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell96.setPaddingLeft(25f);

			table96.addCell(hcell96);
			cell.addElement(table96);
        	
        	
        	
        	 /*PdfPTable table1 = new PdfPTable(1);
        	 table1.setWidthPercentage(100);
             table1.setWidths(new int[]{5});
             
             PdfPCell hcell1;
             hcell1 = new PdfPCell(new Phrase("Patient Details", headFont));
             hcell1.setBorder(Rectangle.NO_BORDER);
             hcell1.setHorizontalAlignment(Element.ALIGN_CENTER);
             hcell1.setPaddingTop(15f);
             table1.addCell(hcell1);
             
             cell.addElement(table1);*/
        	
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{4, 3, 3,3});

            

            PdfPCell hcell;
            hcell = new PdfPCell(new Phrase("RegId", headFont));
            hcell.setBorder(Rectangle.NO_BORDER);
            hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
            hcell.setPaddingTop(5f);
            hcell.setPaddingBottom(10f);
            hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(hcell);

            hcell = new PdfPCell(new Phrase("Patient Name", headFont));
            hcell.setBorder(Rectangle.NO_BORDER);
            hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
            hcell.setPaddingLeft(5f);
            hcell.setPaddingTop(5f);
            hcell.setPaddingBottom(10f);
            hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(hcell);

            hcell = new PdfPCell(new Phrase("Reg. Date", headFont));
            hcell.setBorder(Rectangle.NO_BORDER);
            hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
            hcell.setPaddingLeft(40f);
            hcell.setPaddingTop(5f);
            hcell.setPaddingBottom(10f);
            hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(hcell);


            hcell = new PdfPCell(new Phrase("Payment", headFont));
            hcell.setBorder(Rectangle.NO_BORDER);
            hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            hcell.setPaddingTop(5f);
            hcell.setPaddingBottom(10f);
            hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(hcell);
          
            for (PatientRegistration city : cities) {
            	
            
            	
            	Timestamp timestamp1 = city.getRegDate();
    			DateFormat dateFormat1 = new SimpleDateFormat("dd-MMM-yyyy ");

    			Calendar calendar1 = Calendar.getInstance();
    			calendar1.setTimeInMillis(timestamp1.getTime());

    			String regDate = dateFormat1.format(calendar1.getTime());
            	
            	
            	
                PdfPCell cell1;

                cell1 = new PdfPCell(new Phrase(city.getRegId(),headFont1));
                cell1.setBorder(Rectangle.NO_BORDER);
                cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell1);

                cell1 = new PdfPCell(new Phrase(city.getPatientDetails().getTitle()+". "+city.getPatientDetails().getFirstName()+" "+city.getPatientDetails().getLastName(),headFont1));
                cell1.setBorder(Rectangle.NO_BORDER);
                cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell1.setPaddingLeft(5f);
                table.addCell(cell1);

                cell1 = new PdfPCell(new Phrase(regDate,headFont1));
                cell1.setBorder(Rectangle.NO_BORDER);
                cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell1.setPaddingLeft(40f);
                table.addCell(cell1);
                
                cell1 = new PdfPCell(new Phrase(String.valueOf(city.getAdvanceAmount()),headFont1));
                cell1.setBorder(Rectangle.NO_BORDER);
                cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell1);
            }
            cell.addElement(table);
            document.add(cell);

            PdfWriter.getInstance(document, out);
            document.open();
          
            document.add(table);
            
            
            System.out.println("Your pdf file has been generated!");
        	
            document.close();
            
        } catch (DocumentException ex) {
        
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }

        
        return new ByteArrayInputStream(out.toByteArray());
    }
}